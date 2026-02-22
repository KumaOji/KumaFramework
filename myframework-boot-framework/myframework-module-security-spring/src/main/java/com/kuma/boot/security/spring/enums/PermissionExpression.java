/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonFormat
 *  com.fasterxml.jackson.annotation.JsonFormat$Shape
 *  com.fasterxml.jackson.annotation.JsonValue
 *  com.google.common.collect.ImmutableMap
 *  io.swagger.v3.oas.annotations.media.Schema
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

@Schema(title="Security \u6743\u9650\u8868\u8fbe\u5f0f")
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum PermissionExpression {
    PERMIT_ALL("permitAll", "permitAll"),
    ANONYMOUS("anonymous", "anonymous"),
    REMEMBER_ME("rememberMe", "rememberMe"),
    DENY_ALL("denyAll", "denyAll"),
    AUTHENTICATED("authenticated", "authenticated"),
    FULLY_AUTHENTICATED("fullyAuthenticated", "fullyAuthenticated");

    private static final Map<String, PermissionExpression> INDEX_MAP;
    private static final List<Map<String, Object>> JSON_STRUCTURE;
    @Schema(title="\u7d22\u5f15")
    private final String value;
    @Schema(title="\u8bf4\u660e")
    private final String description;

    private PermissionExpression(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }

    public static PermissionExpression get(String value) {
        return INDEX_MAP.get(value);
    }

    public static List<Map<String, Object>> getPreprocessedJsonStructure() {
        return JSON_STRUCTURE;
    }

    static {
        INDEX_MAP = new HashMap<String, PermissionExpression>();
        JSON_STRUCTURE = new ArrayList<Map<String, Object>>();
        for (PermissionExpression permissionExpression : PermissionExpression.values()) {
            INDEX_MAP.put(permissionExpression.getValue(), permissionExpression);
            JSON_STRUCTURE.add(permissionExpression.ordinal(), (Map<String, Object>)ImmutableMap.builder().put((Object)"value", (Object)permissionExpression.getValue()).put((Object)"key", (Object)permissionExpression.name()).put((Object)"text", (Object)permissionExpression.getDescription()).put((Object)"index", (Object)permissionExpression.ordinal()).build());
        }
    }
}

