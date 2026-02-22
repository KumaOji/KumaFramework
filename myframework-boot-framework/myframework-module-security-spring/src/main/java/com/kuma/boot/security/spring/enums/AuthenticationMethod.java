/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonFormat
 *  com.fasterxml.jackson.annotation.JsonFormat$Shape
 *  com.google.common.collect.ImmutableMap
 *  io.swagger.v3.oas.annotations.media.Schema
 *  org.springframework.security.oauth2.core.ClientAuthenticationMethod
 */
package com.kuma.boot.security.spring.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Schema(title="OAuth2 Client \u8ba4\u8bc1\u65b9\u5f0f")
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum AuthenticationMethod {
    CLIENT_SECRET_BASIC(ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue(), "\u57fa\u4e8eClient Secret\u7684Basic\u9a8c\u8bc1\u6a21\u5f0f"),
    CLIENT_SECRET_POST(ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue(), "\u57fa\u4e8eClient Secret\u7684Post\u9a8c\u8bc1\u6a21\u5f0f"),
    CLIENT_SECRET_JWT(ClientAuthenticationMethod.CLIENT_SECRET_JWT.getValue(), "\u57fa\u4e8eClient Secret\u7684JWT\u9a8c\u8bc1\u6a21\u5f0f"),
    PRIVATE_KEY_JWT(ClientAuthenticationMethod.PRIVATE_KEY_JWT.getValue(), "\u57fa\u4e8e\u79c1\u94a5\u7684JWT\u9a8c\u8bc1\u6a21\u5f0f"),
    NONE(ClientAuthenticationMethod.NONE.getValue(), "\u4e0d\u8bbe\u7f6e\u4efb\u4f55\u6a21\u5f0f");

    @Schema(title="\u8ba4\u8bc1\u65b9\u6cd5")
    private final String value;
    @Schema(title="\u6587\u5b57")
    private final String description;
    private static final Map<Integer, AuthenticationMethod> INDEX_MAP;
    private static final List<Map<String, Object>> JSON_STRUCTURE;

    private AuthenticationMethod(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public String getValue() {
        return this.value;
    }

    public static AuthenticationMethod get(Integer index) {
        return INDEX_MAP.get(index);
    }

    public static List<Map<String, Object>> getPreprocessedJsonStructure() {
        return JSON_STRUCTURE;
    }

    static {
        INDEX_MAP = new HashMap<Integer, AuthenticationMethod>();
        JSON_STRUCTURE = new ArrayList<Map<String, Object>>();
        for (AuthenticationMethod authenticationMethod : AuthenticationMethod.values()) {
            INDEX_MAP.put(authenticationMethod.ordinal(), authenticationMethod);
            JSON_STRUCTURE.add(authenticationMethod.ordinal(), (Map<String, Object>)ImmutableMap.builder().put((Object)"value", (Object)authenticationMethod.getValue()).put((Object)"key", (Object)authenticationMethod.name()).put((Object)"text", (Object)authenticationMethod.getDescription()).put((Object)"index", (Object)authenticationMethod.ordinal()).build());
        }
    }
}

