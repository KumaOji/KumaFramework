/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.totp.time;

import com.kuma.boot.totp.exceptions.TimeProviderException;

public interface TimeProvider {
    public long getTime() throws TimeProviderException;
}

