/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package com.kuma.boot.common.utils.io;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import org.springframework.util.Assert;

public final class SuffixFileFilter
implements FileFilter,
Serializable {
    private static final long serialVersionUID = -3389157631240246157L;
    private final String[] suffixes;

    public SuffixFileFilter(String suffix) {
        Assert.notNull((Object)suffix, (String)"The suffix must not be null");
        this.suffixes = new String[]{suffix};
    }

    public SuffixFileFilter(String[] suffixes) {
        Assert.notNull((Object)suffixes, (String)"The suffix must not be null");
        this.suffixes = new String[suffixes.length];
        System.arraycopy(suffixes, 0, this.suffixes, 0, suffixes.length);
    }

    @Override
    public boolean accept(File pathname) {
        String name = pathname.getName();
        for (String suffix : this.suffixes) {
            if (!this.checkEndsWith(name, suffix)) continue;
            return true;
        }
        return false;
    }

    private boolean checkEndsWith(String str, String end) {
        int endLen = end.length();
        return str.regionMatches(true, str.length() - endLen, end, 0, endLen);
    }
}

