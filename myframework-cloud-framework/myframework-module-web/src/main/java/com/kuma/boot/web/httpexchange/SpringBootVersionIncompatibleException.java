/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.httpexchange;

class SpringBootVersionIncompatibleException
extends RuntimeException {
    private final String currentVersion;
    private final String requiredVersion;

    public SpringBootVersionIncompatibleException(String currentVersion, String requiredVersion) {
        super("Spring Boot version " + currentVersion + " is incompatible with httpexchange-spring-boot-starter. Minimum required version is " + requiredVersion);
        this.currentVersion = currentVersion;
        this.requiredVersion = requiredVersion;
    }

    public String getCurrentVersion() {
        return this.currentVersion;
    }

    public String getRequiredVersion() {
        return this.requiredVersion;
    }
}

