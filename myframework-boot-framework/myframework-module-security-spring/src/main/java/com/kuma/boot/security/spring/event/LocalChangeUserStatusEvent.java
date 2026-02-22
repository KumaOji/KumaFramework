/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.event;

import com.kuma.boot.security.spring.event.domain.TtcUserStatus;
import java.time.Clock;

public class LocalChangeUserStatusEvent
extends LocalApplicationEvent<TtcUserStatus> {
    public LocalChangeUserStatusEvent(TtcUserStatus data) {
        super(data);
    }

    public LocalChangeUserStatusEvent(TtcUserStatus data, Clock clock) {
        super(data, clock);
    }
}

