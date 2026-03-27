CREATE TABLE IF NOT EXISTS coupon (
    id BIGINT PRIMARY KEY,
    coupon_name VARCHAR(50) NOT NULL,
    max_usage INT,
    current_usage INT DEFAULT 0,
    country VARCHAR,
    created_date DATE
);