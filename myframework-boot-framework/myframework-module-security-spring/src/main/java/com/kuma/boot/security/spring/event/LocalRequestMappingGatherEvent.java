/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.event;

import com.kuma.boot.security.spring.event.domain.RequestMapping;
import java.time.Clock;
import java.util.List;

public class LocalRequestMappingGatherEvent
extends LocalApplicationEvent<List<RequestMapping>> {
    public LocalRequestMappingGatherEvent(List<RequestMapping> data) {
        super(data);
    }

    public LocalRequestMappingGatherEvent(List<RequestMapping> data, Clock clock) {
        super(data, clock);
    }
}

