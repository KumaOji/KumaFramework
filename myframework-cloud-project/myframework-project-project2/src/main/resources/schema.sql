-- Project2 data-mybatis 演示表
-- 在 blog 库下执行，或根据 kuma.mybatis.default-schema 调整
CREATE TABLE IF NOT EXISTS demo_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64),
    email VARCHAR(128),
    create_date DATETIME,
    created_user VARCHAR(64),
    modify_date DATETIME,
    modify_user VARCHAR(64)
);
