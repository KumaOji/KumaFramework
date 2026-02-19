/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSON
 *  com.alibaba.fastjson2.JSONWriter$Feature
 */
package com.kuma.boot.web.request.altas.serializer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

import java.util.Map;

public class KeyValueArgumentFormatter
implements ArgumentFormatter {
    private final SensitiveDataMasker sensitiveDataMasker;
    private final String separator;
    private final String keyValueSeparator;
    private final boolean includeParameterIndex;

    public KeyValueArgumentFormatter(SensitiveDataMasker sensitiveDataMasker) {
        this(sensitiveDataMasker, "&", "=", true);
    }

    public KeyValueArgumentFormatter(SensitiveDataMasker sensitiveDataMasker, String separator, String keyValueSeparator, boolean includeParameterIndex) {
        this.sensitiveDataMasker = sensitiveDataMasker;
        this.separator = separator;
        this.keyValueSeparator = keyValueSeparator;
        this.includeParameterIndex = includeParameterIndex;
    }

    @Override
    public String formatArguments(Object[] args, ArgumentFormatter.FormatterContext context) {
        if (args == null || args.length == 0) {
            return "";
        }
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; ++i) {
                if (i > 0) {
                    sb.append(this.separator);
                }
                if (this.includeParameterIndex) {
                    sb.append("arg").append(i).append(this.keyValueSeparator);
                }
                Object maskedArg = this.sensitiveDataMasker.maskSensitiveData(args[i]);
                String value = this.formatValueForKeyValue(maskedArg);
                sb.append(value);
            }
            return this.truncateIfNecessary(sb.toString(), context.getMaxLength());
        }
        catch (Exception e) {
            return "[FORMATTING_FAILED: " + e.getMessage() + "]";
        }
    }

    @Override
    public String formatResult(Object result, ArgumentFormatter.FormatterContext context) {
        if (result == null) {
            return "null";
        }
        try {
            Object maskedResult = this.sensitiveDataMasker.maskSensitiveData(result);
            String value = this.formatValueForKeyValue(maskedResult);
            return this.truncateIfNecessary(value, context.getMaxLength());
        }
        catch (Exception e) {
            return "[FORMATTING_FAILED: " + e.getMessage() + "]";
        }
    }

    @Override
    public String formatHttpParameters(Map<String, String[]> parameters, ArgumentFormatter.FormatterContext context) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }
        try {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                if (!first) {
                    sb.append(this.separator);
                }
                first = false;
                sb.append(key).append(this.keyValueSeparator);
                if (values.length == 1) {
                    sb.append(values[0]);
                    continue;
                }
                sb.append("[");
                for (int i = 0; i < values.length; ++i) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(values[i]);
                }
                sb.append("]");
            }
            return this.truncateIfNecessary(sb.toString(), context.getMaxLength());
        }
        catch (Exception e) {
            return "[FORMATTING_FAILED: " + e.getMessage() + "]";
        }
    }

    @Override
    public String getName() {
        return "key-value";
    }

    private String formatValueForKeyValue(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (this.isPrimitiveOrWrapper(obj) || obj instanceof String) {
            return String.valueOf(obj);
        }
        try {
            return JSON.toJSONString((Object)obj, (JSONWriter.Feature[])new JSONWriter.Feature[]{JSONWriter.Feature.ReferenceDetection, JSONWriter.Feature.IgnoreNonFieldGetter});
        }
        catch (Exception e) {
            return "[" + obj.getClass().getSimpleName() + "@" + Integer.toHexString(obj.hashCode()) + "]";
        }
    }

    private boolean isPrimitiveOrWrapper(Object obj) {
        if (obj == null) {
            return false;
        }
        Class<?> clazz = obj.getClass();
        return clazz.isPrimitive() || clazz == Boolean.class || clazz == Byte.class || clazz == Character.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class || clazz == Float.class || clazz == Double.class;
    }

    private String truncateIfNecessary(String str, int maxLength) {
        if (maxLength > 0 && str.length() > maxLength) {
            return str.substring(0, maxLength) + "[TRUNCATED]";
        }
        return str;
    }
}

