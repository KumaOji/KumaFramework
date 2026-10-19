package com.kuma.cloud.reference.domain.vo;

public record SchemaVO(String scope, String dataSource, String database, String schema,
                       String databaseUser, long orderCount) {}
