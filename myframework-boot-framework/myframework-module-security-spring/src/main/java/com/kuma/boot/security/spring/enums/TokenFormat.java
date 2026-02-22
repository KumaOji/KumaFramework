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

@Schema(name="\u4ee4\u724c\u683c\u5f0f")
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum TokenFormat {
    SELF_CONTAINED(0, "self-contained", "\u81ea\u5305\u542b\u683c\u5f0f\u4ee4\u724c"),
    REFERENCE(1, "reference", "\u5f15\u7528\uff08\u4e0d\u900f\u660e\uff09\u4ee4\u724c");

    @Schema(title="\u679a\u4e3e\u503c")
    private final Integer value;
    @Schema(title="\u683c\u5f0f")
    private final String format;
    @Schema(title="\u6587\u5b57")
    private final String description;
    private static final Map<String, TokenFormat> INDEX_MAP;
    private static final List<Map<String, Object>> JSON_STRUCTURE;

    private TokenFormat(Integer value, String method, String description) {
        this.value = value;
        this.format = method;
        this.description = description;
    }

    @JsonValue
    public Integer getValue() {
        return this.value;
    }

    public String getFormat() {
        return this.format;
    }

    public String getDescription() {
        return this.description;
    }

    public static TokenFormat get(String format) {
        return INDEX_MAP.get(format);
    }

    public static List<Map<String, Object>> getPreprocessedJsonStructure() {
        return JSON_STRUCTURE;
    }

    static {
        INDEX_MAP = new HashMap<String, TokenFormat>();
        JSON_STRUCTURE = new ArrayList<Map<String, Object>>();
        for (TokenFormat tokenFormat : TokenFormat.values()) {
            INDEX_MAP.put(tokenFormat.getFormat(), tokenFormat);
            JSON_STRUCTURE.add(tokenFormat.getValue(), (Map<String, Object>)ImmutableMap.builder().put((Object)"value", (Object)tokenFormat.getValue()).put((Object)"key", (Object)tokenFormat.name()).put((Object)"text", (Object)tokenFormat.getDescription()).put((Object)"format", (Object)tokenFormat.getFormat()).put((Object)"index", (Object)tokenFormat.ordinal()).build());
        }
    }
}

