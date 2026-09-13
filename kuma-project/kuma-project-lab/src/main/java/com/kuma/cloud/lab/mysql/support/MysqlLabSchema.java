package com.kuma.cloud.lab.mysql.support;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 重置并灌入 SQL 实验表，保证每次演示可重复。
 */
public final class MysqlLabSchema {

    public static final String DEPT_TABLE = "sql_lab_dept";
    public static final String EMPLOYEE_TABLE = "sql_lab_employee";

    private MysqlLabSchema() {}

    public static void reset(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("DROP TABLE IF EXISTS `" + EMPLOYEE_TABLE + "`");
        jdbcTemplate.execute("DROP TABLE IF EXISTS `" + DEPT_TABLE + "`");
        jdbcTemplate.execute("""
                CREATE TABLE `sql_lab_dept` (
                    `id`   bigint      NOT NULL,
                    `name` varchar(32) NOT NULL,
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `uk_sql_lab_dept_name` (`name`)
                ) ENGINE = InnoDB
                  DEFAULT CHARSET = utf8mb4
                  COMMENT = 'SQL 实验：部门'
                """);
        jdbcTemplate.execute("""
                CREATE TABLE `sql_lab_employee` (
                    `id`        bigint         NOT NULL,
                    `name`      varchar(32)    NOT NULL,
                    `dept_id`   bigint         NOT NULL,
                    `salary`    decimal(10, 2) NOT NULL,
                    `hire_date` date           NOT NULL,
                    PRIMARY KEY (`id`),
                    KEY `idx_sql_lab_employee_dept` (`dept_id`),
                    CONSTRAINT `fk_sql_lab_employee_dept`
                        FOREIGN KEY (`dept_id`) REFERENCES `sql_lab_dept` (`id`)
                ) ENGINE = InnoDB
                  DEFAULT CHARSET = utf8mb4
                  COMMENT = 'SQL 实验：员工'
                """);
        jdbcTemplate.update("""
                INSERT INTO `sql_lab_dept` (`id`, `name`)
                VALUES (1, '研发'), (2, '运营'), (3, '市场')
                """);
        jdbcTemplate.update("""
                INSERT INTO `sql_lab_employee` (`id`, `name`, `dept_id`, `salary`, `hire_date`)
                VALUES
                    (1, 'Alice', 1, 18000.00, '2022-03-01'),
                    (2, 'Bob',   1, 15000.00, '2023-01-15'),
                    (3, 'Carol', 1, 18000.00, '2021-07-20'),
                    (4, 'Dave',  2, 12000.00, '2024-02-01'),
                    (5, 'Eve',   2,  9000.00, '2024-06-10'),
                    (6, 'Frank', 3, 14000.00, '2023-09-01')
                """);
    }

    public static boolean hasColumn(JdbcTemplate jdbcTemplate, String table, String column) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?
                """,
                Integer.class,
                table,
                column);
        return count != null && count > 0;
    }
}
