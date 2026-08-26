package com.kuma.cloud.lab.snowflake.support;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 雪花 ID 解析工具，与 {@link com.kuma.boot.common.utils.common.SequenceUtils} 位布局保持一致。
 */
public final class SnowflakeIdParser {

    private static final long EPOCH = 1288834974657L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    private SnowflakeIdParser() {
    }

    public static ParsedSnowflakeId parse(long id) {
        long sequence = id & SEQUENCE_MASK;
        long workerId = (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
        long datacenterId = (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
        long timestamp = (id >> TIMESTAMP_SHIFT) + EPOCH;

        return new ParsedSnowflakeId(
                id,
                timestamp,
                FORMATTER.format(Instant.ofEpochMilli(timestamp)),
                datacenterId,
                workerId,
                sequence);
    }

    public record ParsedSnowflakeId(
            long id,
            long timestamp,
            String timestampText,
            long datacenterId,
            long workerId,
            long sequence
    ) {
    }

}
