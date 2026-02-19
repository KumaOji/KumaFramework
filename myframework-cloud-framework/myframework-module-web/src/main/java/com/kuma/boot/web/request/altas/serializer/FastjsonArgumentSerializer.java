/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSON
 *  com.alibaba.fastjson2.JSONWriter$Feature
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.web.request.altas.serializer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.altas.annotation.Log;

import java.util.ArrayList;
import java.util.List;

public class FastjsonArgumentSerializer
implements ArgumentSerializer {
    private final SensitiveDataMasker sensitiveDataMasker;
    private final ArgumentFormatConfig argumentFormatConfig;

    public FastjsonArgumentSerializer(SensitiveDataMasker sensitiveDataMasker, ArgumentFormatConfig argumentFormatConfig) {
        this.sensitiveDataMasker = sensitiveDataMasker;
        this.argumentFormatConfig = argumentFormatConfig != null ? argumentFormatConfig : new ArgumentFormatConfig();
    }

    public FastjsonArgumentSerializer(SensitiveDataMasker sensitiveDataMasker) {
        this.sensitiveDataMasker = sensitiveDataMasker;
        this.argumentFormatConfig = new ArgumentFormatConfig();
    }

    @Override
    public String serializeArgs(Object[] args, Log annotation) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        try {
            List<Object> filteredArgs = this.filterArguments(args, annotation);
            ArrayList<Object> maskedArgs = new ArrayList<Object>();
            for (Object arg : filteredArgs) {
                Object maskedArg = this.sensitiveDataMasker.maskSensitiveData(arg);
                maskedArgs.add(maskedArg);
            }
            String result = this.argumentFormatConfig.getType() == ArgumentFormatType.KEY_VALUE ? this.serializeArgsAsKeyValue(maskedArgs) : JSON.toJSONString(maskedArgs, (JSONWriter.Feature[])new JSONWriter.Feature[]{JSONWriter.Feature.ReferenceDetection, JSONWriter.Feature.IgnoreNonFieldGetter, JSONWriter.Feature.WriteNullListAsEmpty, JSONWriter.Feature.WriteNullStringAsEmpty});
            return this.truncateIfNecessary(result, annotation.maxArgLength());
        }
        catch (Exception e) {
            LogUtils.warn((String)"Fastjson parameter serialization failed", (Object[])new Object[]{e});
            return this.serializeArgsWithFallback(args, annotation, e);
        }
    }

    @Override
    public String serializeResult(Object result, Log annotation) {
        if (result == null) {
            return "null";
        }
        try {
            Object maskedResult = this.sensitiveDataMasker.maskSensitiveData(result);
            String serialized = JSON.toJSONString((Object)maskedResult, (JSONWriter.Feature[])new JSONWriter.Feature[]{JSONWriter.Feature.ReferenceDetection, JSONWriter.Feature.IgnoreNonFieldGetter, JSONWriter.Feature.WriteNullListAsEmpty, JSONWriter.Feature.WriteNullStringAsEmpty});
            return this.truncateIfNecessary(serialized, annotation.maxResultLength());
        }
        catch (Exception e) {
            LogUtils.warn((String)"Fastjson result serialization failed", (Object[])new Object[]{e});
            return this.serializeResultWithFallback(result, annotation, e);
        }
    }

    @Override
    public String serialize(Object obj, int maxLength) {
        if (obj == null) {
            return "null";
        }
        try {
            Object maskedObj = this.sensitiveDataMasker.maskSensitiveData(obj);
            String result = JSON.toJSONString((Object)maskedObj, (JSONWriter.Feature[])new JSONWriter.Feature[]{JSONWriter.Feature.ReferenceDetection, JSONWriter.Feature.IgnoreNonFieldGetter, JSONWriter.Feature.WriteNullListAsEmpty, JSONWriter.Feature.WriteNullStringAsEmpty});
            return this.truncateIfNecessary(result, maxLength);
        }
        catch (Exception e) {
            LogUtils.warn((String)"Fastjson object serialization failed: {}", (Object[])new Object[]{obj.getClass().getSimpleName(), e});
            return this.serializeObjectWithFallback(obj, maxLength, e);
        }
    }

    private List<Object> filterArguments(Object[] args, Log annotation) {
        ArrayList<Object> filteredArgs = new ArrayList<Object>();
        for (int i = 0; i < args.length; ++i) {
            if (!this.isParameterIgnored(args, i)) {
                filteredArgs.add(args[i]);
                continue;
            }
            filteredArgs.add("[IGNORED]");
        }
        return filteredArgs;
    }

    private boolean isParameterIgnored(Object[] args, int index) {
        return false;
    }

    private String truncateIfNecessary(String str, int maxLength) {
        if (maxLength > 0 && str.length() > maxLength) {
            return str.substring(0, maxLength) + "[TRUNCATED]";
        }
        return str;
    }

    private String serializeArgsWithFallback(Object[] args, Log annotation, Exception originalException) {
        try {
            ArrayList<Object> simplifiedArgs = new ArrayList<Object>();
            for (Object arg : args) {
                if (arg == null) {
                    simplifiedArgs.add(null);
                    continue;
                }
                if (this.isPrimitiveOrWrapper(arg) || arg instanceof String) {
                    simplifiedArgs.add(arg);
                    continue;
                }
                simplifiedArgs.add("[" + arg.getClass().getSimpleName() + "@" + Integer.toHexString(arg.hashCode()) + "]");
            }
            return JSON.toJSONString(simplifiedArgs);
        }
        catch (Exception e) {
            LogUtils.debug((String)"Fallback serialization also failed", (Object[])new Object[]{e});
            return "[SERIALIZATION_FAILED: " + originalException.getMessage() + "]";
        }
    }

    private String serializeResultWithFallback(Object result, Log annotation, Exception originalException) {
        try {
            if (result == null) {
                return "null";
            }
            if (this.isPrimitiveOrWrapper(result) || result instanceof String) {
                return JSON.toJSONString((Object)result);
            }
            return "[" + result.getClass().getSimpleName() + "@" + Integer.toHexString(result.hashCode()) + "]";
        }
        catch (Exception e) {
            LogUtils.debug((String)"Fallback result serialization also failed", (Object[])new Object[]{e});
            return "[SERIALIZATION_FAILED: " + originalException.getMessage() + "]";
        }
    }

    private String serializeObjectWithFallback(Object obj, int maxLength, Exception originalException) {
        try {
            if (obj == null) {
                return "null";
            }
            if (this.isPrimitiveOrWrapper(obj) || obj instanceof String) {
                String result = JSON.toJSONString((Object)obj);
                return this.truncateIfNecessary(result, maxLength);
            }
            String result = "[" + obj.getClass().getSimpleName() + "@" + Integer.toHexString(obj.hashCode()) + "]";
            return this.truncateIfNecessary(result, maxLength);
        }
        catch (Exception e) {
            LogUtils.debug((String)"Fallback object serialization also failed", (Object[])new Object[]{e});
            return "[SERIALIZATION_FAILED: " + originalException.getMessage() + "]";
        }
    }

    private String serializeArgsAsKeyValue(List<Object> args) {
        if (args == null || args.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.size(); ++i) {
            if (i > 0) {
                sb.append(this.argumentFormatConfig.getSeparator());
            }
            if (this.argumentFormatConfig.isIncludeParameterIndex()) {
                sb.append("arg").append(i).append(this.argumentFormatConfig.getKeyValueSeparator());
            }
            Object arg = args.get(i);
            String value = this.formatValueForKeyValue(arg);
            sb.append(value);
        }
        return sb.toString();
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
}

