/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.event.metadata.event;

import com.kuma.boot.security.spring.event.LocalApplicationEvent;
import com.kuma.boot.security.spring.event.domain.TtcAttribute;
import java.time.Clock;

public class SysAttributeChangeEvent
extends LocalApplicationEvent<TtcAttribute> {
    public SysAttributeChangeEvent(TtcAttribute data) {
        super(data);
    }

    public SysAttributeChangeEvent(TtcAttribute data, Clock clock) {
        super(data, clock);
    }
}

