CREATE TABLE customer_x_coupon (
    customer_id BIGINT REFERENCES customer (id),
    coupon_id BIGINT REFERENCES coupon (id),
    PRIMARY KEY (customer_id, coupon_id)
);