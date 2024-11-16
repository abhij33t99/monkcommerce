--
-- PostgreSQL database dump
--

-- Dumped from database version 17.0 (Debian 17.0-1.pgdg120+1)
-- Dumped by pg_dump version 17.0 (Debian 17.0-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: apply_bxgy_coupons(json, integer); Type: FUNCTION; Schema: public; Owner: root
--

CREATE FUNCTION public.apply_bxgy_coupons(items json, p_coupon_id integer) RETURNS integer[]
    LANGUAGE plpgsql
    AS $$
DECLARE
	get_product_id INTEGER;
	applicable_count INTEGER;
	res INTEGER[] := '{}';
BEGIN
	select v_get_product_id, v_applicable_count into get_product_id, applicable_count from
	get_bxgy_coupons(items) where v_coupon_id = p_coupon_id;

	res := array_append(res, get_product_id);
	res := array_append(res, applicable_count);
	RETURN res;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        -- Handle the case where no data is found
        RAISE NOTICE 'No applicable coupon found for coupon_id: %', p_coupon_id;
        RETURN NULL; -- or return an empty array if preferred
    WHEN OTHERS THEN
        -- Handle any other exceptions
        RAISE NOTICE 'An error occurred: %', SQLERRM;
        RETURN NULL; -- or return an empty array if preferred
END;
$$;


ALTER FUNCTION public.apply_bxgy_coupons(items json, p_coupon_id integer) OWNER TO root;

--
-- Name: get_bxgy_coupons(json); Type: FUNCTION; Schema: public; Owner: root
--

CREATE FUNCTION public.get_bxgy_coupons(items json) RETURNS TABLE(v_coupon_id integer, v_get_product_id integer, v_applicable_count integer)
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_product_id INTEGER;
	v_coupon_id INTEGER;
    v_quantity INTEGER;
    v_price DOUBLE PRECISION;
    min_buy INTEGER;
    min_get INTEGER;
    total_quantity INTEGER;
    repetition INTEGER;
    number_coupon_apply INTEGER;
    v_get_product_id INTEGER;
	item JSON;
	bxgy RECORD;
BEGIN

	RAISE NOTICE 'Items JSON: %', items;
	DROP TABLE IF EXISTS coupon_set;
    CREATE TEMPORARY TABLE coupon_set (
        coupon_id INTEGER,
        get_product_id INTEGER,
        applicable_count INTEGER,
		CONSTRAINT unique_coupon_product UNIQUE (coupon_id, get_product_id)
    );

	DROP TABLE IF EXISTS temp_input;
    CREATE TEMPORARY TABLE temp_input (
        product_id INTEGER,
        quantity INTEGER,
        price DOUBLE PRECISION
    );
    
    -- Insert data from the JSON array into the temporary table
    INSERT INTO temp_input (product_id, quantity, price)
    SELECT
        (elem->>'productId')::INTEGER,
        (elem->>'quantity')::INTEGER,
        (elem->>'price')::DOUBLE PRECISION
    FROM json_array_elements(items) AS elem;

    FOR item IN SELECT * FROM json_array_elements(items::json) LOOP
        v_product_id := (item->>'productId')::INTEGER;
        v_quantity := (item->>'quantity')::INTEGER;
        v_price := (item->>'price')::DOUBLE PRECISION;

        -- Find all the coupons with buy as product_id from buyxgetyproduct_mapping
        FOR bxgy IN (
            SELECT buyxgetydetails_id 
            FROM buyxgetyproduct_mapping bxgym1
            WHERE bxgym1.product_id = v_product_id AND transaction_type = 'BUY'
        ) LOOP
            SELECT bxgyds.buy_quantity, bxgyds.get_quantity, bxgyds.repetition_limit, bxgyds.coupon_id
            INTO min_buy, min_get, repetition, v_coupon_id
            FROM buyxgetydetails bxgyds
            WHERE bxgyds.id = bxgy.buyxgetydetails_id;

            -- Check if coupons are applicable by checking if the buy items for bxgy.buyxgetydetails_id are present
            SELECT SUM(ti.quantity) INTO total_quantity 
            FROM buyxgetyproduct_mapping b 
            JOIN temp_input ti ON b.product_id = ti.product_id
            WHERE b.buyxgetydetails_id = bxgy.buyxgetydetails_id AND b.transaction_type = 'BUY';

            -- Check if total quantity exceeds min_buy
            IF total_quantity IS NOT NULL AND total_quantity >= min_buy THEN
                -- Now check if corresponding get items are present 
                IF EXISTS (
                    SELECT 1 
                    FROM buyxgetyproduct_mapping b 
                    JOIN temp_input ti ON b.product_id = ti.product_id
                    WHERE b.buyxgetydetails_id = bxgy.buyxgetydetails_id
                    AND b.transaction_type = 'GET'
                ) THEN
                    number_coupon_apply := LEAST(total_quantity / min_buy, repetition);

                    -- Get the highest priced get product_id
                    SELECT b1.product_id INTO v_get_product_id 
                    FROM buyxgetyproduct_mapping b1 
                    JOIN temp_input ti1 ON b1.product_id = ti1.product_id
                    JOIN product p ON b1.product_id = p.id
                    WHERE b1.buyxgetydetails_id = bxgy.buyxgetydetails_id
                    AND b1.transaction_type = 'GET' 
                    ORDER BY p.price DESC 
                    LIMIT 1;

                    -- Insert into coupon_set
                    INSERT INTO coupon_set (coupon_id, get_product_id, applicable_count)
                    VALUES (v_coupon_id, v_get_product_id, number_coupon_apply)
                    ON CONFLICT (coupon_id, get_product_id) DO NOTHING;
                END IF;
            END IF;
        END LOOP;
    END LOOP;

    -- Return the results 
    RETURN QUERY SELECT * FROM coupon_set;
END;
$$;


ALTER FUNCTION public.get_bxgy_coupons(items json) OWNER TO root;

--
-- Name: process_products(json); Type: FUNCTION; Schema: public; Owner: root
--

CREATE FUNCTION public.process_products(products json) RETURNS TABLE(product_id integer, quantity integer, price double precision)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT
        (item->>'product_id')::INTEGER AS product_id,
        (item->>'quantity')::INTEGER AS quantity,
        (item->>'price')::DOUBLE PRECISION AS price
    FROM
        json_array_elements(products::json) AS item;
END;
$$;


ALTER FUNCTION public.process_products(products json) OWNER TO root;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: buyxgetydetails; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.buyxgetydetails (
    buy_quantity integer,
    coupon_id integer,
    get_quantity integer,
    id integer NOT NULL,
    repetition_limit integer
);


ALTER TABLE public.buyxgetydetails OWNER TO root;

--
-- Name: buyxgetydetails_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.buyxgetydetails_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.buyxgetydetails_id_seq OWNER TO root;

--
-- Name: buyxgetydetails_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.buyxgetydetails_id_seq OWNED BY public.buyxgetydetails.id;


--
-- Name: buyxgetyproduct_mapping; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.buyxgetyproduct_mapping (
    buyxgetydetails_id integer,
    id integer NOT NULL,
    product_id integer,
    transaction_type character varying(255),
    CONSTRAINT buyxgetyproduct_mapping_transaction_type_check CHECK (((transaction_type)::text = ANY ((ARRAY['BUY'::character varying, 'GET'::character varying])::text[])))
);


ALTER TABLE public.buyxgetyproduct_mapping OWNER TO root;

--
-- Name: buyxgetyproduct_mapping_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.buyxgetyproduct_mapping_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.buyxgetyproduct_mapping_id_seq OWNER TO root;

--
-- Name: buyxgetyproduct_mapping_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.buyxgetyproduct_mapping_id_seq OWNED BY public.buyxgetyproduct_mapping.id;


--
-- Name: cart_discount_details; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.cart_discount_details (
    coupon_id integer,
    discount integer,
    id integer NOT NULL,
    threshold double precision
);


ALTER TABLE public.cart_discount_details OWNER TO root;

--
-- Name: cart_discount_details_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.cart_discount_details_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.cart_discount_details_id_seq OWNER TO root;

--
-- Name: cart_discount_details_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.cart_discount_details_id_seq OWNED BY public.cart_discount_details.id;


--
-- Name: coupon; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.coupon (
    coupon_type_id integer,
    expiration_date date,
    id integer NOT NULL
);


ALTER TABLE public.coupon OWNER TO root;

--
-- Name: coupon_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.coupon_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.coupon_id_seq OWNER TO root;

--
-- Name: coupon_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.coupon_id_seq OWNED BY public.coupon.id;


--
-- Name: coupon_type; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.coupon_type (
    id integer NOT NULL,
    name character varying(255),
    CONSTRAINT coupon_type_name_check CHECK (((name)::text = ANY ((ARRAY['CART'::character varying, 'PRODUCT'::character varying, 'BUY_GET'::character varying])::text[])))
);


ALTER TABLE public.coupon_type OWNER TO root;

--
-- Name: coupon_type_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.coupon_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.coupon_type_id_seq OWNER TO root;

--
-- Name: coupon_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.coupon_type_id_seq OWNED BY public.coupon_type.id;


--
-- Name: product; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.product (
    id integer NOT NULL,
    price double precision,
    name character varying(255)
);


ALTER TABLE public.product OWNER TO root;

--
-- Name: product_discount_details; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.product_discount_details (
    coupon_id integer,
    discount integer,
    id integer NOT NULL,
    product_id integer
);


ALTER TABLE public.product_discount_details OWNER TO root;

--
-- Name: product_discount_details_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.product_discount_details_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.product_discount_details_id_seq OWNER TO root;

--
-- Name: product_discount_details_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.product_discount_details_id_seq OWNED BY public.product_discount_details.id;


--
-- Name: product_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.product_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.product_id_seq OWNER TO root;

--
-- Name: product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.product_id_seq OWNED BY public.product.id;


--
-- Name: buyxgetydetails id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.buyxgetydetails ALTER COLUMN id SET DEFAULT nextval('public.buyxgetydetails_id_seq'::regclass);


--
-- Name: buyxgetyproduct_mapping id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.buyxgetyproduct_mapping ALTER COLUMN id SET DEFAULT nextval('public.buyxgetyproduct_mapping_id_seq'::regclass);


--
-- Name: cart_discount_details id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.cart_discount_details ALTER COLUMN id SET DEFAULT nextval('public.cart_discount_details_id_seq'::regclass);


--
-- Name: coupon id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.coupon ALTER COLUMN id SET DEFAULT nextval('public.coupon_id_seq'::regclass);


--
-- Name: coupon_type id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.coupon_type ALTER COLUMN id SET DEFAULT nextval('public.coupon_type_id_seq'::regclass);


--
-- Name: product id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.product ALTER COLUMN id SET DEFAULT nextval('public.product_id_seq'::regclass);


--
-- Name: product_discount_details id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.product_discount_details ALTER COLUMN id SET DEFAULT nextval('public.product_discount_details_id_seq'::regclass);


--
-- Data for Name: buyxgetydetails; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.buyxgetydetails (buy_quantity, coupon_id, get_quantity, id, repetition_limit) FROM stdin;
3	1	1	1	2
3	2	1	2	2
\.


--
-- Data for Name: buyxgetyproduct_mapping; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.buyxgetyproduct_mapping (buyxgetydetails_id, id, product_id, transaction_type) FROM stdin;
1	1	1	BUY
1	2	2	BUY
1	3	3	GET
2	4	1	BUY
2	5	2	BUY
2	6	3	GET
\.


--
-- Data for Name: cart_discount_details; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.cart_discount_details (coupon_id, discount, id, threshold) FROM stdin;
\.


--
-- Data for Name: coupon; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.coupon (coupon_type_id, expiration_date, id) FROM stdin;
1	\N	1
1	\N	2
\.


--
-- Data for Name: coupon_type; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.coupon_type (id, name) FROM stdin;
1	BUY_GET
\.


--
-- Data for Name: product; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.product (id, price, name) FROM stdin;
1	50	product1
2	30	product2
3	25	product3
\.


--
-- Data for Name: product_discount_details; Type: TABLE DATA; Schema: public; Owner: root
--

COPY public.product_discount_details (coupon_id, discount, id, product_id) FROM stdin;
\.


--
-- Name: buyxgetydetails_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.buyxgetydetails_id_seq', 2, true);


--
-- Name: buyxgetyproduct_mapping_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.buyxgetyproduct_mapping_id_seq', 6, true);


--
-- Name: cart_discount_details_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.cart_discount_details_id_seq', 1, false);


--
-- Name: coupon_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.coupon_id_seq', 2, true);


--
-- Name: coupon_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.coupon_type_id_seq', 1, true);


--
-- Name: product_discount_details_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.product_discount_details_id_seq', 1, false);


--
-- Name: product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: root
--

SELECT pg_catalog.setval('public.product_id_seq', 3, true);


--
-- Name: buyxgetydetails buyxgetydetails_coupon_id_key; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.buyxgetydetails
    ADD CONSTRAINT buyxgetydetails_coupon_id_key UNIQUE (coupon_id);


--
-- Name: buyxgetydetails buyxgetydetails_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.buyxgetydetails
    ADD CONSTRAINT buyxgetydetails_pkey PRIMARY KEY (id);


--
-- Name: buyxgetyproduct_mapping buyxgetyproduct_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.buyxgetyproduct_mapping
    ADD CONSTRAINT buyxgetyproduct_mapping_pkey PRIMARY KEY (id);


--
-- Name: cart_discount_details cart_discount_details_coupon_id_key; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.cart_discount_details
    ADD CONSTRAINT cart_discount_details_coupon_id_key UNIQUE (coupon_id);


--
-- Name: cart_discount_details cart_discount_details_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.cart_discount_details
    ADD CONSTRAINT cart_discount_details_pkey PRIMARY KEY (id);


--
-- Name: coupon coupon_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.coupon
    ADD CONSTRAINT coupon_pkey PRIMARY KEY (id);


--
-- Name: coupon_type coupon_type_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.coupon_type
    ADD CONSTRAINT coupon_type_pkey PRIMARY KEY (id);


--
-- Name: product_discount_details product_discount_details_coupon_id_key; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.product_discount_details
    ADD CONSTRAINT product_discount_details_coupon_id_key UNIQUE (coupon_id);


--
-- Name: product_discount_details product_discount_details_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.product_discount_details
    ADD CONSTRAINT product_discount_details_pkey PRIMARY KEY (id);


--
-- Name: product product_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (id);


--
-- Name: buyxgetyproduct_mapping fk6shj7ah4n5nb70b8wb8vdhgit; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.buyxgetyproduct_mapping
    ADD CONSTRAINT fk6shj7ah4n5nb70b8wb8vdhgit FOREIGN KEY (product_id) REFERENCES public.product(id);


--
-- Name: product_discount_details fkf8dwode59d4bkiqy9u5t24dpa; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.product_discount_details
    ADD CONSTRAINT fkf8dwode59d4bkiqy9u5t24dpa FOREIGN KEY (product_id) REFERENCES public.product(id);


--
-- Name: coupon fkgt739o6i75as62tyftahc2a6o; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.coupon
    ADD CONSTRAINT fkgt739o6i75as62tyftahc2a6o FOREIGN KEY (coupon_type_id) REFERENCES public.coupon_type(id);


--
-- Name: cart_discount_details fkoflowvtom36q6rbrgubmqd0r7; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.cart_discount_details
    ADD CONSTRAINT fkoflowvtom36q6rbrgubmqd0r7 FOREIGN KEY (coupon_id) REFERENCES public.coupon(id);


--
-- Name: product_discount_details fkq0gwtduw2rsbkl7f3y414345t; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.product_discount_details
    ADD CONSTRAINT fkq0gwtduw2rsbkl7f3y414345t FOREIGN KEY (coupon_id) REFERENCES public.coupon(id);


--
-- Name: buyxgetyproduct_mapping fksmucxloctgoaoplyj0wnuixgm; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.buyxgetyproduct_mapping
    ADD CONSTRAINT fksmucxloctgoaoplyj0wnuixgm FOREIGN KEY (buyxgetydetails_id) REFERENCES public.buyxgetydetails(id);


--
-- Name: buyxgetydetails fksrenv6144huam134u5fakuut; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.buyxgetydetails
    ADD CONSTRAINT fksrenv6144huam134u5fakuut FOREIGN KEY (coupon_id) REFERENCES public.coupon(id);


--
-- PostgreSQL database dump complete
--

