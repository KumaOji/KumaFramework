CREATE TABLE purchase_orders (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    sku VARCHAR(64) NOT NULL,
    product_name VARCHAR(120) NOT NULL,
    unit_price DECIMAL(19, 2) NOT NULL,
    quantity INT NOT NULL,
    order_status VARCHAR(16) NOT NULL,
    version BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT ck_order_price CHECK (unit_price >= 0),
    CONSTRAINT ck_order_quantity CHECK (quantity BETWEEN 1 AND 1000),
    CONSTRAINT ck_order_status CHECK (order_status IN ('CREATED', 'CONFIRMED')),
    CONSTRAINT ck_order_version CHECK (version >= 0)
);
