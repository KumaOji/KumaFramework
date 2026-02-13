/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension.common;

import java.io.Serializable;
import java.util.Map;

public class URL
implements Serializable {
    private final Map<String, String> parameters;

    public URL(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String key) {
        return this.parameters.get(key);
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }
}

