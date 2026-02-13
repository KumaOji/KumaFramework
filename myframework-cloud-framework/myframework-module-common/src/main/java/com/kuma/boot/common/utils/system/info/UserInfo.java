/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.system.info;

import java.io.Serializable;

public class UserInfo
implements Serializable {
    private final String USER_NAME = System.getProperty("user.name", null);
    private final String USER_HOME = System.getProperty("user.home", null);
    private final String USER_DIR = System.getProperty("user.dir", null);
    private final String USER_LANGUAGE = System.getProperty("user.language", null);
    private final String USER_COUNTRY = System.getProperty("user.country", null) == null ? System.getProperty("user.region", null) : System.getProperty("user.country", null);
    private final String JAVA_IO_TMPDIR = System.getProperty("java.io.tmpdir", null);

    public final String getName() {
        return this.USER_NAME;
    }

    public final String getHomeDir() {
        return this.USER_HOME;
    }

    public final String getCurrentDir() {
        return this.USER_DIR;
    }

    public final String getTempDir() {
        return this.JAVA_IO_TMPDIR;
    }

    public final String getLanguage() {
        return this.USER_LANGUAGE;
    }

    public final String getCountry() {
        return this.USER_COUNTRY;
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User Name:           ").append(this.getName()).append("\nUser Home Dir:       ").append(this.getHomeDir()).append("\nUser Current Dir:    ").append(this.getCurrentDir()).append("\nUser Temp Dir:       ").append(this.getTempDir()).append("\nUser Language:       ").append(this.getLanguage()).append("\nUser Country:        ").append(this.getCountry());
        return builder.toString();
    }
}

