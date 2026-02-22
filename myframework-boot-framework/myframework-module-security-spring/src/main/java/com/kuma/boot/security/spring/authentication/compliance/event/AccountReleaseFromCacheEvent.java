/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.compliance.event;

import com.kuma.boot.security.spring.event.LocalApplicationEvent;
import java.time.Clock;

public class AccountReleaseFromCacheEvent
extends LocalApplicationEvent<String> {
    public AccountReleaseFromCacheEvent(String data) {
        super(data);
    }

    public AccountReleaseFromCacheEvent(String data, Clock clock) {
        super(data, clock);
    }
}

