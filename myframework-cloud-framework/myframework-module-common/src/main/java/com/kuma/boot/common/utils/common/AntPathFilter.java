/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.AntPathMatcher
 *  org.springframework.util.PathMatcher
 */
package com.kuma.boot.common.utils.common;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class AntPathFilter
implements FileFilter,
Serializable {
    private static final long serialVersionUID = 812598009067554612L;
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();
    private final String pattern;

    public AntPathFilter(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean accept(File pathname) {
        String filePath = pathname.getAbsolutePath();
        return PATH_MATCHER.match(this.pattern, filePath);
    }
}

