/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonFormat
 *  com.fasterxml.jackson.annotation.JsonFormat$Shape
 *  com.google.common.collect.ImmutableMap
 *  io.swagger.v3.oas.annotations.media.Schema
 *  org.springframework.security.oauth2.core.AuthorizationGrantType
 */
package com.kuma.boot.security.spring.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@Schema(title="OAuth2 \u8ba4\u8bc1\u6a21\u5f0f")
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum GrantType {
    AUTHORIZATION_CODE(AuthorizationGrantType.AUTHORIZATION_CODE.getValue(), "Authorization Code \u6a21\u5f0f"),
    CLIENT_CREDENTIALS(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue(), "Client Credentials \u6a21\u5f0f"),
    DEVICE_CODE(AuthorizationGrantType.DEVICE_CODE.getValue(), "Device Code \u6a21\u5f0f"),
    REFRESH_TOKEN(AuthorizationGrantType.REFRESH_TOKEN.getValue(), "Refresh Token \u6a21\u5f0f"),
    JWT_BEARER(AuthorizationGrantType.JWT_BEARER.getValue(), "JWT Token \u6a21\u5f0f"),
    PASSWORD("password", "Password \u6a21\u5f0f"),
    SOCIAL_CREDENTIALS("social_credentials", "Social Credentials \u6a21\u5f0f");

    @Schema(title="\u8ba4\u8bc1\u6a21\u5f0f")
    private final String value;
    @Schema(title="\u6587\u5b57")
    private final String description;
    private static final Map<Integer, GrantType> INDEX_MAP;
    private static final List<Map<String, Object>> JSON_STRUCTURE;

    private GrantType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }

    public static GrantType get(Integer index) {
        return INDEX_MAP.get(index);
    }

    public static List<Map<String, Object>> getPreprocessedJsonStructure() {
        return JSON_STRUCTURE;
    }

    static {
        INDEX_MAP = new HashMap<Integer, GrantType>();
        JSON_STRUCTURE = new ArrayList<Map<String, Object>>();
        for (GrantType grantType : GrantType.values()) {
            INDEX_MAP.put(grantType.ordinal(), grantType);
            JSON_STRUCTURE.add(grantType.ordinal(), (Map<String, Object>)ImmutableMap.builder().put((Object)"value", (Object)grantType.getValue()).put((Object)"key", (Object)grantType.name()).put((Object)"text", (Object)grantType.getDescription()).put((Object)"index", (Object)grantType.ordinal()).build());
        }
    }
}

