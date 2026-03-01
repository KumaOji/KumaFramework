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

package com.kuma.boot.security.spring.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.ImmutableMap;
import com.kuma.boot.security.spring.constants.BaseConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * <p>OAuth2 认证模式枚举
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:06:24
 */
@Schema(title = "OAuth2 认证模式")
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GrantType {

    /**
     * enum
     */
    AUTHORIZATION_CODE(
            AuthorizationGrantType.AUTHORIZATION_CODE.getValue(), "Authorization Code 模式"),
    CLIENT_CREDENTIALS(
            AuthorizationGrantType.CLIENT_CREDENTIALS.getValue(), "Client Credentials 模式"),
    DEVICE_CODE(AuthorizationGrantType.DEVICE_CODE.getValue(), "Device Code 模式"),
    REFRESH_TOKEN(AuthorizationGrantType.REFRESH_TOKEN.getValue(), "Refresh Token 模式"),
    JWT_BEARER(AuthorizationGrantType.JWT_BEARER.getValue(), "JWT Token 模式"),
    PASSWORD(BaseConstants.PASSWORD, "Password 模式"),
    SOCIAL_CREDENTIALS(BaseConstants.SOCIAL_CREDENTIALS, "Social Credentials 模式");

    @Schema(title = "认证模式")
    private final String value;

    @Schema(title = "文字")
    private final String description;

    private static final Map<Integer, GrantType> INDEX_MAP = new HashMap<>();
    private static final List<Map<String, Object>> JSON_STRUCTURE = new ArrayList<>();

    static {
        for (GrantType grantType : GrantType.values()) {
            INDEX_MAP.put(grantType.ordinal(), grantType);
            JSON_STRUCTURE.add(
                    grantType.ordinal(),
                    ImmutableMap.<String, Object>builder()
                            .put("value", grantType.getValue())
                            .put("key", grantType.name())
                            .put("text", grantType.getDescription())
                            .put("index", grantType.ordinal())
                            .build());
        }
    }

    /**
     * 格兰特类型
     *
     * @param value       价值
     * @param description 描述
     * @return
     * @since 2023-07-04 10:06:24
     */
    GrantType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获得价值
     *
     * @return {@link String }
     * @since 2023-07-04 10:06:24
     */
    //    @Override
    public String getValue() {
        return value;
    }

    /**
     * 得到描述
     *
     * @return {@link String }
     * @since 2023-07-04 10:06:24
     */
    //    @Override
    public String getDescription() {
        return description;
    }

    /**
     * 得到
     *
     * @param index 指数
     * @return {@link GrantType }
     * @since 2023-07-04 10:06:24
     */
    public static GrantType get(Integer index) {
        return INDEX_MAP.get(index);
    }

    /**
     * 得到预处理json结构
     *
     * @return {@link List }<{@link Map }<{@link String }, {@link Object }>>
     * @since 2023-07-04 10:06:24
     */
    public static List<Map<String, Object>> getPreprocessedJsonStructure() {
        return JSON_STRUCTURE;
    }
}
