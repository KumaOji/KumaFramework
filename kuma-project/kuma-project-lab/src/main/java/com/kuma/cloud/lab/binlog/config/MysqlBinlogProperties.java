package com.kuma.cloud.lab.binlog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Embedded Debezium configuration for the lab MySQL instance.
 */
@ConfigurationProperties(prefix = "kuma.lab.mysql-binlog")
public record MysqlBinlogProperties(
        boolean enabled,
        String jdbcUrl,
        String username,
        String password,
        long serverId,
        String topicPrefix,
        String tableIncludeList,
        String snapshotMode,
        String offsetFile,
        String schemaHistoryFile,
        int bufferCapacity
) {

    public MysqlBinlogProperties {
        serverId = serverId > 0 ? serverId : 184054L;
        topicPrefix = hasText(topicPrefix) ? topicPrefix : "kuma-lab-mysql";
        snapshotMode = hasText(snapshotMode) ? snapshotMode : "no_data";
        offsetFile = hasText(offsetFile) ? offsetFile : "data/lab-binlog/offsets.dat";
        schemaHistoryFile = hasText(schemaHistoryFile)
                ? schemaHistoryFile
                : "data/lab-binlog/schema-history.dat";
        bufferCapacity = bufferCapacity > 0 ? bufferCapacity : 1000;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
