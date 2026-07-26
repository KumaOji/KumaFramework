package com.kuma.cloud.lab.binlog.domain;

import java.time.Instant;
import java.util.Map;

/**
 * Database-neutral view of one committed MySQL row change.
 */
public record MysqlBinlogChangeEvent(
        String id,
        Operation operation,
        String database,
        String table,
        Map<String, Object> before,
        Map<String, Object> after,
        String transactionId,
        Instant occurredAt,
        String binlogFile,
        Long binlogPosition,
        boolean snapshot
) {

    public enum Operation {
        CREATE, UPDATE, DELETE, READ
    }
}
