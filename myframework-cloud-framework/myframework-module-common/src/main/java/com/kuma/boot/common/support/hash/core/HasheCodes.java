/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.api.HashCode;
import com.kuma.boot.common.support.hash.core.HashCodeCRC;
import com.kuma.boot.common.support.hash.core.HashCodeFnv;
import com.kuma.boot.common.support.hash.core.HashCodeJdk;
import com.kuma.boot.common.support.hash.core.HashCodeKetama;
import com.kuma.boot.common.support.hash.core.HashCodeMurmur;
import com.kuma.boot.common.support.instance.Instances;

public final class HasheCodes {
    private HasheCodes() {
    }

    public static HashCode crc() {
        return Instances.singleton(HashCodeCRC.class);
    }

    public static HashCode fnv() {
        return Instances.singleton(HashCodeFnv.class);
    }

    public static HashCode jdk() {
        return Instances.singleton(HashCodeJdk.class);
    }

    public static HashCode ketama() {
        return Instances.singleton(HashCodeKetama.class);
    }

    public static HashCode murmur() {
        return Instances.singleton(HashCodeMurmur.class);
    }
}

