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
 * <p>令牌格式
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:06:46
 */
@Schema(name = "令牌格式")
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TokenFormat {

    /**
     * enum
     */
    SELF_CONTAINED(0, "self-contained", "自包含格式令牌"),
    REFERENCE(1, "reference", "引用（不透明）令牌");

    @Schema(title = "枚举值")
    private final Integer value;

    @Schema(title = "格式")
    private final String format;

    @Schema(title = "文字")
    private final String description;

    private static final Map<String, TokenFormat> INDEX_MAP = new HashMap<>();
    private static final List<Map<String, Object>> JSON_STRUCTURE = new ArrayList<>();

    static {
        for (TokenFormat tokenFormat : TokenFormat.values()) {
            INDEX_MAP.put(tokenFormat.getFormat(), tokenFormat);
            JSON_STRUCTURE.add(
                    tokenFormat.getValue(),
                    ImmutableMap.<String, Object>builder()
                            // 使用数字作为 value, 适用于单选，同时数据库只存 value值即可
                            // 使用具体的字符串值作为value, 适用于多选，同时数据库存储以逗号分隔拼接的字符串
                            .put("value", tokenFormat.getValue())
                            .put("key", tokenFormat.name())
                            .put("text", tokenFormat.getDescription())
                            .put("format", tokenFormat.getFormat())
                            .put("index", tokenFormat.ordinal())
                            .build());
        }
    }

    /**
     * 令牌格式
     *
     * @param value       价值
     * @param method      方法
     * @param description 描述
     * @return
     * @since 2023-07-04 10:06:46
     */
    TokenFormat(Integer value, String method, String description) {
        this.value = value;
        this.format = method;
        this.description = description;
    }

    /**
     * 不加@JsonValue，转换的时候转换出完整的对象。 加了@JsonValue，只会显示相应的属性的值
     * <p>
     * 不使用@JsonValue @JsonDeserializer类里面要做相应的处理
     *
     * @return {@link Integer }
     * @since 2023-07-04 10:06:46
     */
    @JsonValue
    //    @Override
    public Integer getValue() {
        return value;
    }

    /**
     * 得到格式
     *
     * @return {@link String }
     * @since 2023-07-04 10:06:46
     */
    public String getFormat() {
        return format;
    }

    /**
     * 得到描述
     *
     * @return {@link String }
     * @since 2023-07-04 10:06:46
     */
    //    @Override
    public String getDescription() {
        return description;
    }

    /**
     * 得到
     *
     * @param format 格式
     * @return {@link TokenFormat }
     * @since 2023-07-04 10:06:46
     */
    public static TokenFormat get(String format) {
        return INDEX_MAP.get(format);
    }

    /**
     * 得到预处理json结构
     *
     * @return {@link List }<{@link Map }<{@link String }, {@link Object }>>
     * @since 2023-07-04 10:06:46
     */
    public static List<Map<String, Object>> getPreprocessedJsonStructure() {
        return JSON_STRUCTURE;
    }
}
