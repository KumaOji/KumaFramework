/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.web.request.altas.serializer;

import com.kuma.boot.common.utils.log.LogUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ArgumentFormatterManager {
    private final Map<String, ArgumentFormatter> formatters = new ConcurrentHashMap<String, ArgumentFormatter>();
    private final ArgumentFormatter defaultFormatter;
    private final String defaultFormatterName;

    public ArgumentFormatterManager(ArgumentFormatter defaultFormatter, String defaultFormatterName) {
        this.defaultFormatter = defaultFormatter;
        this.defaultFormatterName = defaultFormatterName;
        this.registerFormatter(defaultFormatterName, defaultFormatter);
        LogUtils.info((String)"ArgumentFormatterManager initialized with default formatter: {}", (Object[])new Object[]{defaultFormatterName});
    }

    public void registerFormatter(String name, ArgumentFormatter formatter) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Formatter name cannot be null or empty");
        }
        if (formatter == null) {
            throw new IllegalArgumentException("Formatter cannot be null");
        }
        this.formatters.put(name.toLowerCase(), formatter);
        LogUtils.debug((String)"Registered formatter: {} -> {}", (Object[])new Object[]{name, formatter.getClass().getSimpleName()});
    }

    public ArgumentFormatter getFormatter(String name) {
        if (name == null || name.trim().isEmpty()) {
            return this.defaultFormatter;
        }
        ArgumentFormatter formatter = this.formatters.get(name.toLowerCase());
        if (formatter == null) {
            LogUtils.warn((String)"Formatter '{}' not found, using default formatter '{}'", (Object[])new Object[]{name, this.defaultFormatterName});
            return this.defaultFormatter;
        }
        return formatter;
    }

    public ArgumentFormatter getDefaultFormatter() {
        return this.defaultFormatter;
    }

    public String getDefaultFormatterName() {
        return this.defaultFormatterName;
    }

    public boolean hasFormatter(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return this.formatters.containsKey(name.toLowerCase());
    }

    public Set<String> getFormatterNames() {
        return new HashSet<String>(this.formatters.keySet());
    }

    public boolean removeFormatter(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (this.defaultFormatterName.equalsIgnoreCase(name)) {
            LogUtils.warn((String)"Cannot remove default formatter: {}", (Object[])new Object[]{name});
            return false;
        }
        ArgumentFormatter removed = this.formatters.remove(name.toLowerCase());
        if (removed != null) {
            LogUtils.debug((String)"Removed formatter: {}", (Object[])new Object[]{name});
            return true;
        }
        return false;
    }

    public String formatArguments(String formatterName, Object[] args, ArgumentFormatter.FormatterContext context) {
        ArgumentFormatter formatter = this.getFormatter(formatterName);
        return formatter.formatArguments(args, context);
    }

    public String formatResult(String formatterName, Object result, ArgumentFormatter.FormatterContext context) {
        ArgumentFormatter formatter = this.getFormatter(formatterName);
        return formatter.formatResult(result, context);
    }

    public String formatHttpParameters(String formatterName, Map<String, String[]> parameters, ArgumentFormatter.FormatterContext context) {
        ArgumentFormatter formatter = this.getFormatter(formatterName);
        return formatter.formatHttpParameters(parameters, context);
    }
}

