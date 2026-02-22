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

@Schema(title="\u5e94\u7528\u7c7b\u578b")
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum ApplicationType {
    WEB(0, "PC\u7f51\u9875\u5e94\u7528"),
    SERVICE(1, "\u670d\u52a1\u5e94\u7528"),
    APP(2, "\u624b\u673aAPP\u5e94\u7528"),
    WAP(3, "\u624b\u673a\u7f51\u9875\u5e94\u7528"),
    MINI(4, "\u5c0f\u7a0b\u5e8f\u5e94\u7528"),
    IOT(5, "\u7269\u8054\u7f51\u5e94\u7528");

    @Schema(title="\u679a\u4e3e\u503c")
    private final Integer value;
    @Schema(title="\u6587\u5b57")
    private final String description;
    private static final Map<Integer, ApplicationType> INDEX_MAP;
    private static final List<Map<String, Object>> JSON_STRUCT;

    private ApplicationType(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    @JsonValue
    public Integer getValue() {
        return this.value;
    }

    public static ApplicationType get(Integer index) {
        return INDEX_MAP.get(index);
    }

    public static List<Map<String, Object>> getPreprocessedJsonStructure() {
        return JSON_STRUCT;
    }

    static {
        INDEX_MAP = new HashMap<Integer, ApplicationType>();
        JSON_STRUCT = new ArrayList<Map<String, Object>>();
        for (ApplicationType applicationType : ApplicationType.values()) {
            INDEX_MAP.put(applicationType.getValue(), applicationType);
            JSON_STRUCT.add(applicationType.getValue(), (Map<String, Object>)ImmutableMap.builder().put((Object)"value", (Object)applicationType.getValue()).put((Object)"key", (Object)applicationType.name()).put((Object)"text", (Object)applicationType.getDescription()).put((Object)"index", (Object)applicationType.getValue()).build());
        }
    }
}

