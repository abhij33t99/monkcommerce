CREATE OR REPLACE FUNCTION apply_bxgy_coupons(items JSON, p_coupon_id INTEGER)
RETURNS INTEGER[] AS $$
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
$$ LANGUAGE plpgsql;
