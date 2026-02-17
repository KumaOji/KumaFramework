/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

/**
 * @author gengwei.zheng
 */
@Schema(title = "数据状态")
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DataItemStatusEnum implements BaseUiEnum<Integer> {

    /**
     * 数据条目已启用
     */
    ENABLE(0, "启用"),
    /**
     * 数据条目被启用
     */
    FORBIDDEN(1, "禁用"),
    /**
     * 数据条目被锁定
     */
    LOCKING(2, "锁定"),
    /**
     * 数据条目已过期
     */
    EXPIRED(3, "过期");

    @Schema(title = "枚举值")
    private final Integer value;

    @Schema(title = "文字")
    private final String description;

    private static final Map<Integer, DataItemStatusEnum> INDEX_MAP = new HashMap<>();

    private static final List<Map<String, Object>> JSON_STRUCTURE = new ArrayList<>();

    static {
        for (DataItemStatusEnum dataItemStatusEnum : DataItemStatusEnum.values()) {
            INDEX_MAP.put(dataItemStatusEnum.getValue(), dataItemStatusEnum);
            JSON_STRUCTURE.add(
                    dataItemStatusEnum.getValue(),
                    ImmutableMap.<String, Object>builder()
                            .put("value", dataItemStatusEnum.getValue())
                            .put("key", dataItemStatusEnum.name())
                            .put("text", dataItemStatusEnum.getDescription())
                            .build());
        }
    }

    DataItemStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 不加@JsonValue，转换的时候转换出完整的对象。 加了@JsonValue，只会显示相应的属性的值
     * <p>
     * 不使用@JsonValue @JsonDeserializer类里面要做相应的处理
     * @return Enum枚举值
     */
    @JsonValue
    @Override
    public Integer getValue() {
        return value;
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
}
