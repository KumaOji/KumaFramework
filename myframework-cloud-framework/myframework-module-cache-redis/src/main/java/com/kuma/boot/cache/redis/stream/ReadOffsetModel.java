/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.data.redis.connection.stream.ReadOffset
 */
package com.kuma.boot.cache.redis.stream;

import org.springframework.data.redis.connection.stream.ReadOffset;

public enum ReadOffsetModel {
    START(ReadOffset.from((String)"0-0")),
    LATEST(ReadOffset.latest()),
    LAST_CONSUMED(ReadOffset.lastConsumed());

    private final ReadOffset readOffset;

    private ReadOffsetModel(ReadOffset readOffset) {
        this.readOffset = readOffset;
    }

    public ReadOffset getReadOffset() {
        return this.readOffset;
    }
}

