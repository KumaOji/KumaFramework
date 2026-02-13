/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.system.info;

import java.io.Serializable;

public class JvmInfo
implements Serializable {
    private final String JAVA_VM_NAME = System.getProperty("java.vm.name", null);
    private final String JAVA_VM_VERSION = System.getProperty("java.vm.version", null);
    private final String JAVA_VM_VENDOR = System.getProperty("java.vm.vendor", null);
    private final String JAVA_VM_INFO = System.getProperty("java.vm.info", null);

    public final String getName() {
        return this.JAVA_VM_NAME;
    }

    public final String getVersion() {
        return this.JAVA_VM_VERSION;
    }

    public final String getVendor() {
        return this.JAVA_VM_VENDOR;
    }

    public final String getInfo() {
        return this.JAVA_VM_INFO;
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("JavaVM Name:    ").append(this.getName()).append("\nJavaVM Version: ").append(this.getVersion()).append("\nJavaVM Vendor:  ").append(this.getVendor()).append("\nJavaVM Info:    ").append(this.getInfo());
        return builder.toString();
    }
}

