/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.office.convert.config;

public class Constants {
    public static String SYSTEM_SEPARATOR = "/";
    public static String PROJECT_ROOT_DIRECTORY = System.getProperty("user.dir").replaceAll("\\\\", SYSTEM_SEPARATOR);
    public static final String DEFAULT_FOLDER_TMP = PROJECT_ROOT_DIRECTORY + "/tmp";
    public static final String DEFAULT_FOLDER_TMP_GENERATE = PROJECT_ROOT_DIRECTORY + "/tmp-generate";
}

