/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.common.model;

import org.jspecify.annotations.Nullable;

public class Version {
    private static final String DELIMITER = "\\.";
    private @Nullable String version;
    private boolean complete = true;

    private Version() {
    }

    private Version(@Nullable String version) {
        this.version = version;
    }

    public Version incomplete() {
        this.complete = false;
        return this;
    }

    public static Version of(@Nullable String version) {
        return new Version(version);
    }

    public boolean eq(@Nullable String version) {
        return this.compare(version) == 0;
    }

    public boolean ne(@Nullable String version) {
        return this.compare(version) != 0;
    }

    public boolean gt(@Nullable String version) {
        return this.compare(version) > 0;
    }

    public boolean gte(@Nullable String version) {
        return this.compare(version) >= 0;
    }

    public boolean lt(@Nullable String version) {
        return this.compare(version) < 0;
    }

    public boolean lte(@Nullable String version) {
        return this.compare(version) <= 0;
    }

    private int compare(@Nullable String version) {
        return Version.compare(this.version, version, this.complete);
    }

    private static int compare(@Nullable String v1, @Nullable String v2, boolean complete) {
        if (v1 == v2) {
            return 0;
        }
        if (v1 == null) {
            return -1;
        }
        if (v2 == null) {
            return 1;
        }
        if ((v1 = v1.trim()).equals(v2 = v2.trim())) {
            return 0;
        }
        String[] v1s = v1.split(DELIMITER);
        String[] v2s = v2.split(DELIMITER);
        int v1sLen = v1s.length;
        int v2sLen = v2s.length;
        int len = complete ? Math.max(v1sLen, v2sLen) : Math.min(v1sLen, v2sLen);
        for (int i = 0; i < len; ++i) {
            String c2;
            String c1 = len > v1sLen || null == v1s[i] ? "" : v1s[i];
            int result = c1.compareTo(c2 = len > v2sLen || null == v2s[i] ? "" : v2s[i]);
            if (result == 0) continue;
            return result;
        }
        return 0;
    }
}

