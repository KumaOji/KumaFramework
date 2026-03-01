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
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>安全表达式
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:06:29
 */
@Schema(title = "Security 权限表达式")
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PermissionExpression {
    /**
     * 权限表达式
     */
    PERMIT_ALL("permitAll", "permitAll"),
    ANONYMOUS("anonymous", "anonymous"),
    REMEMBER_ME("rememberMe", "rememberMe"),
    DENY_ALL("denyAll", "denyAll"),
    AUTHENTICATED("authenticated", "authenticated"),
    FULLY_AUTHENTICATED("fullyAuthenticated", "fullyAuthenticated");

    private static final Map<String, PermissionExpression> INDEX_MAP = new HashMap<>();
    private static final List<Map<String, Object>> JSON_STRUCTURE = new ArrayList<>();

    @Schema(title = "索引")
    private final String value;

    @Schema(title = "说明")
    private final String description;

    static {
        for (PermissionExpression permissionExpression : PermissionExpression.values()) {
            INDEX_MAP.put(permissionExpression.getValue(), permissionExpression);
            JSON_STRUCTURE.add(
                    permissionExpression.ordinal(),
                    ImmutableMap.<String, Object>builder()
                            .put("value", permissionExpression.getValue())
                            .put("key", permissionExpression.name())
                            .put("text", permissionExpression.getDescription())
                            .put("index", permissionExpression.ordinal())
                            .build());
        }
    }

    /**
     * 允许表达式
     *
     * @param value       价值
     * @param description 描述
     * @return
     * @since 2023-07-04 10:06:29
     */
    PermissionExpression(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获得价值
     *
     * @return {@link String }
     * @since 2023-07-04 10:06:29
     */
    @JsonValue
    //    @Override
    public String getValue() {
        return value;
    }

    /**
     * 得到描述
     *
     * @return {@link String }
     * @since 2023-07-04 10:06:29
     */
    //    @Override
    public String getDescription() {
        return description;
    }

    /**
     * 得到
     *
     * @param value 价值
     * @return {@link PermissionExpression }
     * @since 2023-07-04 10:06:29
     */
    public static PermissionExpression get(String value) {
        return INDEX_MAP.get(value);
    }

    /**
     * 得到预处理json结构
     *
     * @return {@link List }<{@link Map }<{@link String }, {@link Object }>>
     * @since 2023-07-04 10:06:29
     */
    public static List<Map<String, Object>> getPreprocessedJsonStructure() {
        return JSON_STRUCTURE;
    }
}
