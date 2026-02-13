/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.io;

import com.kuma.boot.common.utils.collection.MapUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class UrlBuilder {
    private final Map<String, String> params = new LinkedHashMap<String, String>(7);
    private String baseUrl;

    private UrlBuilder() {
    }

    public static UrlBuilder fromBaseUrl(String baseUrl) {
        UrlBuilder builder = new UrlBuilder();
        builder.setBaseUrl(baseUrl);
        return builder;
    }

    public Map<String, Object> getReadOnlyParams() {
        return Collections.unmodifiableMap(this.params);
    }

    public UrlBuilder queryParam(String key, Object value) {
        if (StringUtils.isEmpty(key)) {
            throw new RuntimeException("\u53c2\u6570\u540d\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String valueAsString = value != null ? value.toString() : null;
        this.params.put(key, valueAsString);
        return this;
    }

    public UrlBuilder pathAppend(String value) {
        if (StringUtils.isEmpty(value)) {
            throw new RuntimeException("\u53c2\u6570\u4e0d\u80fd\u4e3a\u7a7a");
        }
        this.baseUrl = this.baseUrl + value;
        this.setBaseUrl((String)this.baseUrl);
        return this;
    }

    public String build() {
        return this.build(false);
    }

    public String build(boolean encode) {
        if (MapUtils.isEmpty(this.params)) {
            return this.baseUrl;
        }
        String baseUrl = StringUtils.appendIfNotContain(this.baseUrl, "?", "&");
        String paramString = MapUtils.parseMapToString(this.params, encode);
        return baseUrl + paramString;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}

