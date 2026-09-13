-- MySQL 基础语法复习稿。
-- 可在 Lab 连接的数据库中整份执行，也可按段落单独跑。
-- 对应接口：GET /api/lab/mysql/syntax 、POST /api/lab/mysql/scenario

-- ============================================================================
-- 1. 建表 CREATE TABLE
-- ============================================================================
-- 规则：列名 类型 [约束] ；主键 / 索引单独声明更清晰。

DROP TABLE IF EXISTS `sql_lab_employee`;
DROP TABLE IF EXISTS `sql_lab_dept`;

CREATE TABLE `sql_lab_dept` (
    `id`   bigint      NOT NULL,
    `name` varchar(32) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sql_lab_dept_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = 'SQL 实验：部门';

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
  COMMENT = 'SQL 实验：员工';

INSERT INTO `sql_lab_dept` (`id`, `name`)
VALUES (1, '研发'),
       (2, '运营'),
       (3, '市场');

INSERT INTO `sql_lab_employee` (`id`, `name`, `dept_id`, `salary`, `hire_date`)
VALUES (1, 'Alice', 1, 18000.00, '2022-03-01'),
       (2, 'Bob',   1, 15000.00, '2023-01-15'),
       (3, 'Carol', 1, 18000.00, '2021-07-20'),
       (4, 'Dave',  2, 12000.00, '2024-02-01'),
       (5, 'Eve',   2,  9000.00, '2024-06-10'),
       (6, 'Frank', 3, 14000.00, '2023-09-01');

-- ============================================================================
-- 2. 加列 ALTER TABLE ... ADD COLUMN
-- ============================================================================
-- 常规写法：AFTER 指定位置；FIRST 放到最前。
-- 重复执行会报 Duplicate column，先判断或先 DROP。

ALTER TABLE `sql_lab_employee`
    ADD COLUMN `title` varchar(32) NULL COMMENT '职位' AFTER `name`;

UPDATE `sql_lab_employee` SET `title` = '工程师' WHERE `id` IN (1, 2, 3);
UPDATE `sql_lab_employee` SET `title` = '运营'   WHERE `id` IN (4, 5);
UPDATE `sql_lab_employee` SET `title` = '策划'   WHERE `id` = 6;

-- 查看表结构
SHOW COLUMNS FROM `sql_lab_employee`;

-- ============================================================================
-- 3. 基础查询 SELECT / WHERE / ORDER BY
-- ============================================================================

SELECT `id`, `name`, `title`, `salary`
FROM `sql_lab_employee`
WHERE `salary` >= 14000
ORDER BY `salary` DESC, `id` ASC;

-- ============================================================================
-- 4. 分组 GROUP BY / HAVING
-- ============================================================================
-- SELECT 里非聚合列必须出现在 GROUP BY 中（ONLY_FULL_GROUP_BY）。
-- HAVING 过滤的是分组后的结果，WHERE 过滤的是分组前的行。

SELECT d.`name`                                              AS dept_name,
       COUNT(*)                                              AS emp_count,
       SUM(e.`salary`)                                       AS salary_sum,
       ROUND(AVG(e.`salary`), 2)                             AS salary_avg
FROM `sql_lab_employee` e
JOIN `sql_lab_dept` d ON d.`id` = e.`dept_id`
GROUP BY d.`id`, d.`name`
HAVING COUNT(*) >= 2
ORDER BY salary_sum DESC;

-- ============================================================================
-- 5. 开窗 WINDOW：PARTITION BY 切分，ORDER BY 排序
-- ============================================================================
-- 开窗不合并行（和 GROUP BY 的区别）：每行都还在，只是多了窗口计算结果。
-- ROW_NUMBER 不并列；RANK 并列后跳号；DENSE_RANK 并列不跳号。

SELECT e.`name`,
       d.`name` AS dept_name,
       e.`salary`,
       ROW_NUMBER() OVER (PARTITION BY e.`dept_id` ORDER BY e.`salary` DESC) AS rn,
       RANK()       OVER (PARTITION BY e.`dept_id` ORDER BY e.`salary` DESC) AS rk,
       DENSE_RANK() OVER (PARTITION BY e.`dept_id` ORDER BY e.`salary` DESC) AS drk,
       SUM(e.`salary`) OVER (PARTITION BY e.`dept_id`)                      AS dept_salary_sum,
       SUM(e.`salary`) OVER (
           PARTITION BY e.`dept_id`
           ORDER BY e.`salary`
           ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
       ) AS running_sum
FROM `sql_lab_employee` e
JOIN `sql_lab_dept` d ON d.`id` = e.`dept_id`
ORDER BY d.`id`, e.`salary` DESC;

-- 取每个部门工资最高的人（开窗 + 外层过滤）
SELECT *
FROM (
    SELECT e.`name`,
           d.`name` AS dept_name,
           e.`salary`,
           ROW_NUMBER() OVER (PARTITION BY e.`dept_id` ORDER BY e.`salary` DESC, e.`id`) AS rn
    FROM `sql_lab_employee` e
    JOIN `sql_lab_dept` d ON d.`id` = e.`dept_id`
) ranked
WHERE ranked.rn = 1;
