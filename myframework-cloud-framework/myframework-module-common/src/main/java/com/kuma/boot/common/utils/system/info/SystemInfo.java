/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.system.info;

import com.kuma.boot.common.utils.system.info.JavaInfo;
import com.kuma.boot.common.utils.system.info.JvmInfo;
import com.kuma.boot.common.utils.system.info.OsInfo;
import com.kuma.boot.common.utils.system.info.RuntimeInfo;
import com.kuma.boot.common.utils.system.info.UserInfo;
import java.io.Serializable;

public class SystemInfo
implements Serializable {
    public static final UserInfo USER_INFO = new UserInfo();
    public static final OsInfo OS_INFO = new OsInfo();
    public static final JavaInfo JAVA_INFO = new JavaInfo();
    public static final JvmInfo JVM_INFO = new JvmInfo();
    public static final RuntimeInfo RUNTIME_INFO = new RuntimeInfo();

    private SystemInfo() {
    }

    public static String info() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[UserInfo]\n").append(USER_INFO.toString()).append("\n").append("\n[OsInfo]\n").append(OS_INFO.toString()).append("\n").append("\n[JavaInfo]\n").append(JAVA_INFO.toString()).append("\n").append("\n[JvmInfo]\n").append(JVM_INFO.toString()).append("\n").append("\n[RuntimeInfo]\n").append(RUNTIME_INFO.toString()).append("\n");
        return stringBuilder.toString();
    }
}

