package com.kuma.cloud.lab.mysql.service.impl;

import com.kuma.cloud.lab.mysql.domain.vo.MysqlOperationStepVO;
import com.kuma.cloud.lab.mysql.domain.vo.MysqlQueryResultVO;
import com.kuma.cloud.lab.mysql.domain.vo.MysqlScenarioVO;
import com.kuma.cloud.lab.mysql.domain.vo.MysqlTopicVO;
import com.kuma.cloud.lab.mysql.service.MysqlLabService;
import com.kuma.cloud.lab.mysql.support.MysqlLabSchema;
import com.kuma.cloud.lab.mysql.support.MysqlSyntaxCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MysqlLabServiceImpl implements MysqlLabService {

    private static final String ADD_TITLE_SQL = """
            ALTER TABLE `sql_lab_employee`
                ADD COLUMN `title` varchar(32) NULL COMMENT '职位' AFTER `name`
            """;

    private static final String GROUP_BY_SQL = """
            SELECT d.`name` AS dept_name,
                   COUNT(*) AS emp_count,
                   SUM(e.`salary`) AS salary_sum,
                   ROUND(AVG(e.`salary`), 2) AS salary_avg
            FROM `sql_lab_employee` e
            JOIN `sql_lab_dept` d ON d.`id` = e.`dept_id`
            GROUP BY d.`id`, d.`name`
            HAVING COUNT(*) >= 2
            ORDER BY salary_sum DESC
            """;

    private static final String WINDOW_SQL = """
            SELECT e.`name`,
                   d.`name` AS dept_name,
                   e.`salary`,
                   ROW_NUMBER() OVER (PARTITION BY e.`dept_id` ORDER BY e.`salary` DESC) AS rn,
                   RANK()       OVER (PARTITION BY e.`dept_id` ORDER BY e.`salary` DESC) AS rk,
                   SUM(e.`salary`) OVER (PARTITION BY e.`dept_id`) AS dept_salary_sum
            FROM `sql_lab_employee` e
            JOIN `sql_lab_dept` d ON d.`id` = e.`dept_id`
            ORDER BY d.`id`, e.`salary` DESC
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MysqlTopicVO> syntax() {
        return MysqlSyntaxCatalog.topics();
    }

    @Override
    public MysqlScenarioVO runScenario() {
        List<MysqlOperationStepVO> steps = new ArrayList<>();
        steps.add(step("create-table", "建表", createTable()));
        steps.add(step("add-column", "加列", addColumn()));
        steps.add(step("group-by", "分组", groupBy()));
        steps.add(step("window", "开窗", window()));
        return new MysqlScenarioVO(steps);
    }

    @Override
    public MysqlQueryResultVO createTable() {
        MysqlLabSchema.reset(jdbcTemplate);
        String sql = "SHOW COLUMNS FROM `sql_lab_employee`";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return new MysqlQueryResultVO(
                "create-table",
                sql,
                rows,
                rows.size(),
                "已重建 sql_lab_dept / sql_lab_employee 并写入 6 名员工。打开 sql/mysql-basics.sql 可对照 CREATE TABLE。");
    }

    @Override
    public MysqlQueryResultVO addColumn() {
        ensureEmployeeTable();
        if (MysqlLabSchema.hasColumn(jdbcTemplate, MysqlLabSchema.EMPLOYEE_TABLE, "title")) {
            jdbcTemplate.execute("ALTER TABLE `sql_lab_employee` DROP COLUMN `title`");
        }
        jdbcTemplate.execute(ADD_TITLE_SQL);
        jdbcTemplate.update("UPDATE `sql_lab_employee` SET `title` = '工程师' WHERE `dept_id` = 1");
        jdbcTemplate.update("UPDATE `sql_lab_employee` SET `title` = '运营' WHERE `dept_id` = 2");
        jdbcTemplate.update("UPDATE `sql_lab_employee` SET `title` = '策划' WHERE `dept_id` = 3");
        String sql = "SHOW COLUMNS FROM `sql_lab_employee`";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return new MysqlQueryResultVO(
                "add-column",
                ADD_TITLE_SQL.strip(),
                rows,
                rows.size(),
                "AFTER name 把 title 插到姓名后面。重复调用会先 DROP 再 ADD，保证可重复演示。");
    }

    @Override
    public MysqlQueryResultVO groupBy() {
        ensureEmployeeTable();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(GROUP_BY_SQL);
        return new MysqlQueryResultVO(
                "group-by",
                GROUP_BY_SQL.strip(),
                rows,
                rows.size(),
                "市场只有 1 人，被 HAVING COUNT(*) >= 2 滤掉。JOIN + GROUP BY 按部门聚合。");
    }

    @Override
    public MysqlQueryResultVO window() {
        ensureEmployeeTable();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(WINDOW_SQL);
        return new MysqlQueryResultVO(
                "window",
                WINDOW_SQL.strip(),
                rows,
                rows.size(),
                "PARTITION BY dept_id 按部门切窗口。研发 Alice/Carol 工资并列：ROW_NUMBER 仍 1/2，RANK 都是 1 然后跳到 3。");
    }

    private void ensureEmployeeTable() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?
                """,
                Integer.class,
                MysqlLabSchema.EMPLOYEE_TABLE);
        if (count == null || count == 0) {
            MysqlLabSchema.reset(jdbcTemplate);
        }
    }

    private static MysqlOperationStepVO step(String topic, String operation, MysqlQueryResultVO detail) {
        return new MysqlOperationStepVO(topic, operation, detail, detail.note());
    }
}
