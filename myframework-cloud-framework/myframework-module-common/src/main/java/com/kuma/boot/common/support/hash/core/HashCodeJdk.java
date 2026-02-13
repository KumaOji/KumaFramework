/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.core.AbstractHashCode;

public class HashCodeJdk
extends AbstractHashCode {
    @Override
    public int doHash(String text) {
        return text.hashCode();
    }
}

