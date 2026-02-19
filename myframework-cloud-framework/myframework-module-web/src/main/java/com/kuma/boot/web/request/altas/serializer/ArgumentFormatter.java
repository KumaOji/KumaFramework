/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.request.altas.serializer;

import java.util.Map;

public interface ArgumentFormatter {
    public String formatArguments(Object[] var1, FormatterContext var2);

    public String formatResult(Object var1, FormatterContext var2);

    public String formatHttpParameters(Map<String, String[]> var1, FormatterContext var2);

    public String getName();

    public static interface FormatterContext {
        public String getMethodName();

        public String getClassName();

        public int getMaxLength();

        public Object getAttribute(String var1);

        public void setAttribute(String var1, Object var2);

        public Map<String, Object> getAttributes();
    }
}

