/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.system.info;

import java.io.Serializable;

public class JavaInfo
implements Serializable {
    private final String JAVA_VERSION = System.getProperty("java.version", null);
    private final String JAVA_VENDOR = System.getProperty("java.vendor", null);
    private final String JAVA_VENDOR_URL = System.getProperty("java.vendor.url", null);

    public final String getVersion() {
        return this.JAVA_VERSION;
    }

    public final String getVendor() {
        return this.JAVA_VENDOR;
    }

    public final String getVendorURL() {
        return this.JAVA_VENDOR_URL;
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Java Version:    ").append(this.getVersion()).append("\nJava Vendor:     ").append(this.getVendor()).append("\nJava Vendor URL: ").append(this.getVendorURL());
        return builder.toString();
    }
}

