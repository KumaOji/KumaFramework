package com.kuma.cloud.lab.mysql.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.lab.mysql.domain.vo.MysqlQueryResultVO;
import com.kuma.cloud.lab.mysql.domain.vo.MysqlScenarioVO;
import com.kuma.cloud.lab.mysql.domain.vo.MysqlTopicVO;
import com.kuma.cloud.lab.mysql.service.MysqlLabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "MySQL 基础语法")
@RestController
@RequestMapping("/lab/mysql")
@RequiredArgsConstructor
public class MysqlLabController {

    private final MysqlLabService mysqlLabService;

    @Operation(summary = "语法目录：建表 / 加列 / 查询 / 分组 / 开窗")
    @GetMapping("/syntax")
    public Result<List<MysqlTopicVO>> syntax() {
        return Result.success(mysqlLabService.syntax());
    }

    @Operation(summary = "综合场景：重建实验表后依次演示建表、加列、分组、开窗")
    @PostMapping("/scenario")
    public Result<MysqlScenarioVO> scenario() {
        return Result.success(mysqlLabService.runScenario());
    }

    @Operation(summary = "建表：重建 sql_lab_dept / sql_lab_employee 并返回列信息")
    @PostMapping("/ddl/create-table")
    public Result<MysqlQueryResultVO> createTable() {
        return Result.success(mysqlLabService.createTable());
    }

    @Operation(summary = "加列：ALTER TABLE ADD COLUMN title")
    @PostMapping("/ddl/add-column")
    public Result<MysqlQueryResultVO> addColumn() {
        return Result.success(mysqlLabService.addColumn());
    }

    @Operation(summary = "分组：GROUP BY + HAVING")
    @GetMapping("/group-by")
    public Result<MysqlQueryResultVO> groupBy() {
        return Result.success(mysqlLabService.groupBy());
    }

    @Operation(summary = "开窗：ROW_NUMBER / RANK / SUM() OVER (PARTITION BY)")
    @GetMapping("/window")
    public Result<MysqlQueryResultVO> window() {
        return Result.success(mysqlLabService.window());
    }
}
