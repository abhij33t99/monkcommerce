CREATE OR REPLACE FUNCTION get_bxgy_coupons(items JSON)
RETURNS TABLE(v_coupon_id INTEGER, v_get_product_id INTEGER, v_applicable_count INTEGER) AS $$
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
$$ LANGUAGE plpgsql;