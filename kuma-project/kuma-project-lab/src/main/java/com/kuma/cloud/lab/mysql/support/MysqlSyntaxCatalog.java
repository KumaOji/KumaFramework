package com.kuma.cloud.lab.mysql.support;

import com.kuma.cloud.lab.mysql.domain.vo.MysqlTopicVO;

import java.util.List;

/**
 * SQL 基础语法复习目录，与 {@code sql/mysql-basics.sql} 段落对应。
 */
public final class MysqlSyntaxCatalog {

    private MysqlSyntaxCatalog() {}

    public static List<MysqlTopicVO> topics() {
        return List.of(
                new MysqlTopicVO(
                        "create-table",
                        "建表",
                        "CREATE TABLE 定义列、主键、索引和外键",
                        """
                        CREATE TABLE sql_lab_employee (
                            id        BIGINT NOT NULL,
                            name      VARCHAR(32) NOT NULL,
                            dept_id   BIGINT NOT NULL,
                            salary    DECIMAL(10, 2) NOT NULL,
                            hire_date DATE NOT NULL,
                            PRIMARY KEY (id),
                            KEY idx_dept (dept_id)
                        );
                        """,
                        "列顺序：名字 → 类型 → 约束。主键/索引单独写更清晰。"),
                new MysqlTopicVO(
                        "add-column",
                        "加列",
                        "ALTER TABLE ... ADD COLUMN 给已有表追加字段",
                        """
                        ALTER TABLE sql_lab_employee
                            ADD COLUMN title VARCHAR(32) NULL COMMENT '职位' AFTER name;
                        """,
                        "AFTER / FIRST 控制位置。重复加同名列会报错，可先查 information_schema 或 DROP COLUMN。"),
                new MysqlTopicVO(
                        "select",
                        "基础查询",
                        "SELECT / WHERE / ORDER BY",
                        """
                        SELECT id, name, salary
                        FROM sql_lab_employee
                        WHERE salary >= 14000
                        ORDER BY salary DESC, id ASC;
                        """,
                        "ORDER BY 可多列；相等时用第二列打破平局。"),
                new MysqlTopicVO(
                        "group-by",
                        "分组",
                        "GROUP BY 聚合，HAVING 过滤分组结果",
                        """
                        SELECT d.name, COUNT(*) emp_count, SUM(e.salary) salary_sum
                        FROM sql_lab_employee e
                        JOIN sql_lab_dept d ON d.id = e.dept_id
                        GROUP BY d.id, d.name
                        HAVING COUNT(*) >= 2;
                        """,
                        "WHERE 在分组前过滤行，HAVING 在分组后过滤组。非聚合列必须进 GROUP BY。"),
                new MysqlTopicVO(
                        "window",
                        "开窗",
                        "窗口函数按 PARTITION BY 切分，不合并行",
                        """
                        SELECT name, salary,
                               ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC) rn,
                               RANK()       OVER (PARTITION BY dept_id ORDER BY salary DESC) rk,
                               SUM(salary)  OVER (PARTITION BY dept_id) dept_sum
                        FROM sql_lab_employee;
                        """,
                        "GROUP BY 会把多行收成一组；开窗保留每一行，只是多算一列。ROW_NUMBER 不并列，RANK 并列跳号。")
        );
    }
}
