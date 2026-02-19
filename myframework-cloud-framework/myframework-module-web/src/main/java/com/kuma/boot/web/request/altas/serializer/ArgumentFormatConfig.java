/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.serializer;

public class ArgumentFormatConfig {
    private ArgumentFormatType type = ArgumentFormatType.JSON;
    private String separator = "&";
    private String keyValueSeparator = "=";
    private boolean includeParameterIndex = true;

    public ArgumentFormatConfig() {
    }

    public ArgumentFormatConfig(ArgumentFormatType type, String separator, String keyValueSeparator, boolean includeParameterIndex) {
        this.type = type;
        this.separator = separator;
        this.keyValueSeparator = keyValueSeparator;
        this.includeParameterIndex = includeParameterIndex;
    }

    public ArgumentFormatType getType() {
        return this.type;
    }

    public void setType(ArgumentFormatType type) {
        this.type = type;
    }

    public String getSeparator() {
        return this.separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getKeyValueSeparator() {
        return this.keyValueSeparator;
    }

    public void setKeyValueSeparator(String keyValueSeparator) {
        this.keyValueSeparator = keyValueSeparator;
    }

    public boolean isIncludeParameterIndex() {
        return this.includeParameterIndex;
    }

    public void setIncludeParameterIndex(boolean includeParameterIndex) {
        this.includeParameterIndex = includeParameterIndex;
    }
}

