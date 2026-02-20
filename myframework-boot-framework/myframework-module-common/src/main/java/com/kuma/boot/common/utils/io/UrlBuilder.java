/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.utils.io;

import com.kuma.boot.common.utils.collection.MapUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** 构造URL */
public class UrlBuilder {

    private final Map<String, String> params = new LinkedHashMap<>(7);

    private String baseUrl;

    private UrlBuilder() {}

    /**
     * @param baseUrl 基础路径
     * @return the new {@code UrlBuilder}
     */
    public static UrlBuilder fromBaseUrl(String baseUrl) {
        UrlBuilder builder = new UrlBuilder();
        builder.setBaseUrl(baseUrl);
        return builder;
    }

    /**
     * 只读的参数Map
     * @return unmodifiable Map
     */
    public Map<String, Object> getReadOnlyParams() {
        return Collections.unmodifiableMap(params);
    }

    /**
     * 添加参数
     * @param key 参数名称
     * @param value 参数值
     * @return this UrlBuilder
     */
    public UrlBuilder queryParam(String key, Object value) {
        if (StringUtils.isEmpty(key)) {
            throw new RuntimeException("参数名不能为空");
        }
        String valueAsString = (value != null ? value.toString() : null);
        this.params.put(key, valueAsString);

        return this;
    }

    /**
     * 添加参数
     * @param value 参数值
     * @return this UrlBuilder
     */
    public UrlBuilder pathAppend(String value) {
        if (StringUtils.isEmpty(value)) {
            throw new RuntimeException("参数不能为空");
        }
        this.setBaseUrl(this.baseUrl += value);
        return this;
    }

    /**
     * 构造url
     * @return url
     */
    public String build() {
        return this.build(false);
    }

    /**
     * 构造url
     * @param encode 转码
     * @return url
     */
    public String build(boolean encode) {
        if (MapUtils.isEmpty(this.params)) {
            return this.baseUrl;
        }
        String baseUrl = StringUtils.appendIfNotContain(this.baseUrl, "?", "&");
        String paramString = MapUtils.parseMapToString(this.params, encode);
        return baseUrl + paramString;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
