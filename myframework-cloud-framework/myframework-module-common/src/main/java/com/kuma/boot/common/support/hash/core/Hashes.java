/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.api.Hash;
import com.kuma.boot.common.support.hash.core.EmptyHash;
import com.kuma.boot.common.support.hash.core.Md2Hash;
import com.kuma.boot.common.support.hash.core.Md5Hash;
import com.kuma.boot.common.support.hash.core.Sha1Hash;
import com.kuma.boot.common.support.hash.core.Sha256Hash;
import com.kuma.boot.common.support.hash.core.Sha384Hash;
import com.kuma.boot.common.support.hash.core.Sha512Hash;

public final class Hashes {
    private Hashes() {
    }

    public static Hash md2() {
        return new Md2Hash();
    }

    public static Hash md5() {
        return new Md5Hash();
    }

    public static Hash sha1() {
        return new Sha1Hash();
    }

    public static Hash sha256() {
        return new Sha256Hash();
    }

    public static Hash sha384() {
        return new Sha384Hash();
    }

    public static Hash sha512() {
        return new Sha512Hash();
    }

    public static Hash empty() {
        return new EmptyHash();
    }
}

