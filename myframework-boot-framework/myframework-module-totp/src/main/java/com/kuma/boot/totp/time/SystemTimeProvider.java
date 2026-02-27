/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.totp.time;

import com.kuma.boot.totp.exceptions.TimeProviderException;

import java.time.Instant;

public class SystemTimeProvider
implements TimeProvider {
    @Override
    public long getTime() throws TimeProviderException {
        return Instant.now().getEpochSecond();
    }
}

