package com.kuma.cloud.lab.binlog.domain;

import java.time.Instant;

public record MysqlBinlogMonitorStatus(
        boolean enabled,
        boolean running,
        long capturedEvents,
        Instant lastEventAt,
        String lastError
) {
}
