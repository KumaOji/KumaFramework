package com.kuma.cloud.lab.mysql.domain.vo;

import java.util.List;

/**
 * MySQL 语法综合场景结果。
 */
public record MysqlScenarioVO(
        List<MysqlOperationStepVO> steps
) {
}
