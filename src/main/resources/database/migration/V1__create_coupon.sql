CREATE TABLE IF NOT EXISTS coupon (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    coupon_name VARCHAR(100) NOT NULL,
    max_usage INT,
    current_usage INT DEFAULT 0,
    country VARCHAR(4),
    created_date DATE
);

CREATE UNIQUE INDEX idx_cname_country
ON coupon (coupon_name, country)