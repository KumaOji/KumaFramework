package com.kuma.cloud.lab.mysql.domain.vo;

/**
 * SQL 基础语法条目，用于复习目录。
 */
public record MysqlTopicVO(
        String id,
        String title,
        String summary,
        String syntax,
        String note
) {
}
