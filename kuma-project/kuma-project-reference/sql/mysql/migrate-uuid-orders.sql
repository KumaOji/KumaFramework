-- One-time upgrade from the previous UUID schema. MySQL DDL commits implicitly.
-- Stop the old reference process before upgrading; original UUIDs remain in legacy_id.
ALTER TABLE kuma_reference_mysql.purchase_orders
    DROP PRIMARY KEY,
    CHANGE COLUMN id legacy_id VARCHAR(36) NULL,
    ADD COLUMN id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ADD CONSTRAINT uq_orders_legacy_id UNIQUE (legacy_id),
    CHANGE COLUMN created_at create_date DATETIME(6) NOT NULL,
    ADD COLUMN modify_date DATETIME(6),
    ADD COLUMN created_user VARCHAR(64),
    ADD COLUMN modify_user VARCHAR(64),
    ADD CONSTRAINT ck_order_money_range CHECK (unit_price * quantity <= 92233720368547758.07),
    ADD INDEX ix_orders_sku_status_date (sku, order_status, create_date DESC, id DESC);
UPDATE kuma_reference_mysql.purchase_orders SET modify_date = create_date;
