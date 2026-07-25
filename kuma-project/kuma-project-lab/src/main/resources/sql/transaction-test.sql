-- Lab 项目数据库事务测试表。
-- 请在 MySQL 中选择 Lab 项目连接的数据库后执行。

CREATE TABLE IF NOT EXISTS `tx_demo_account` (
    `id`          bigint         NOT NULL,
    `owner_name`  varchar(64)    NOT NULL,
    `balance`     decimal(12, 2) NOT NULL,
    `update_time` datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '事务测试账户';

INSERT INTO `tx_demo_account` (`id`, `owner_name`, `balance`)
VALUES (1, 'Alice', 1000.00),
       (2, 'Bob', 1000.00)
ON DUPLICATE KEY UPDATE `owner_name` = VALUES(`owner_name`),
                        `balance` = VALUES(`balance`);

SELECT `id`, `owner_name`, `balance`, `update_time`
FROM `tx_demo_account`
ORDER BY `id`;
