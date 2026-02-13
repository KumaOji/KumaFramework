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
package com.kuma.boot.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;
import com.kuma.boot.common.enums.base.BaseUiEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Schema(title="\u6570\u636e\u72b6\u6001")
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum DataItemStatusEnum implements BaseUiEnum<Integer>
{
    ENABLE(0, "\u542f\u7528"),
    FORBIDDEN(1, "\u7981\u7528"),
    LOCKING(2, "\u9501\u5b9a"),
    EXPIRED(3, "\u8fc7\u671f");

    @Schema(title="\u679a\u4e3e\u503c")
    private final Integer value;
    @Schema(title="\u6587\u5b57")
    private final String description;
    private static final Map<Integer, DataItemStatusEnum> INDEX_MAP;
    private static final List<Map<String, Object>> JSON_STRUCTURE;

    private DataItemStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    @JsonValue
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public static DataItemStatusEnum get(Integer index) {
        return INDEX_MAP.get(index);
    }

    public static List<Map<String, Object>> getPreprocessedJsonStructure() {
        return JSON_STRUCTURE;
    }

    static {
        INDEX_MAP = new HashMap<Integer, DataItemStatusEnum>();
        JSON_STRUCTURE = new ArrayList<Map<String, Object>>();
        for (DataItemStatusEnum dataItemStatusEnum : DataItemStatusEnum.values()) {
            INDEX_MAP.put(dataItemStatusEnum.getValue(), dataItemStatusEnum);
            JSON_STRUCTURE.add(dataItemStatusEnum.getValue(), (Map<String, Object>)ImmutableMap.builder().put((Object)"value", (Object)dataItemStatusEnum.getValue()).put((Object)"key", (Object)dataItemStatusEnum.name()).put((Object)"text", (Object)dataItemStatusEnum.getDescription()).build());
        }
    }
}

