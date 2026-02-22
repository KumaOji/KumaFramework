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

@Schema(name="\u7b7e\u540d\u7b97\u6cd5")
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum Signature {
    RS256(0, "RS256"),
    RS384(1, "RS384"),
    RS512(2, "RS512"),
    ES256(3, "ES256"),
    ES384(4, "ES384"),
    ES512(5, "ES512"),
    PS256(6, "PS256"),
    PS384(7, "PS384"),
    PS512(8, "PS512");

    @Schema(title="\u679a\u4e3e\u503c")
    private final Integer value;
    @Schema(name="\u6587\u5b57")
    private final String description;
    private static final Map<Integer, Signature> INDEX_MAP;
    private static final List<Map<String, Object>> JSON_STRUCTURE;

    private Signature(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public Integer getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }

    public static Signature get(Integer index) {
        return INDEX_MAP.get(index);
    }

    public static List<Map<String, Object>> getPreprocessedJsonStructure() {
        return JSON_STRUCTURE;
    }

    static {
        INDEX_MAP = new HashMap<Integer, Signature>();
        JSON_STRUCTURE = new ArrayList<Map<String, Object>>();
        for (Signature signature : Signature.values()) {
            INDEX_MAP.put(signature.getValue(), signature);
            JSON_STRUCTURE.add(signature.getValue(), (Map<String, Object>)ImmutableMap.builder().put((Object)"value", (Object)signature.getValue()).put((Object)"key", (Object)signature.name()).put((Object)"text", (Object)signature.getDescription()).put((Object)"index", (Object)signature.getValue()).build());
        }
    }
}

