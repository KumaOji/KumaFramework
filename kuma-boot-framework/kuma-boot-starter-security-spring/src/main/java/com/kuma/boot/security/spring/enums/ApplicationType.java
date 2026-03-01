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
 * <p>应用类别
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:06:06
 */
@Schema(title = "应用类型")
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApplicationType {

    /**
     * 应用类型
     */
    WEB(0, "PC网页应用"),
    SERVICE(1, "服务应用"),
    APP(2, "手机APP应用"),
    WAP(3, "手机网页应用"),
    MINI(4, "小程序应用"),
    IOT(5, "物联网应用");

    @Schema(title = "枚举值")
    private final Integer value;

    @Schema(title = "文字")
    private final String description;

    private static final Map<Integer, ApplicationType> INDEX_MAP = new HashMap<>();
    private static final List<Map<String, Object>> JSON_STRUCT = new ArrayList<>();

    static {
        for (ApplicationType applicationType : ApplicationType.values()) {
            INDEX_MAP.put(applicationType.getValue(), applicationType);
            JSON_STRUCT.add(
                    applicationType.getValue(),
                    ImmutableMap.<String, Object>builder()
                            .put("value", applicationType.getValue())
                            .put("key", applicationType.name())
                            .put("text", applicationType.getDescription())
                            .put("index", applicationType.getValue())
                            .build());
        }
    }

    /**
     * 应用程序类型
     *
     * @param value       价值
     * @param description 描述
     * @return
     * @since 2023-07-04 10:06:06
     */
    ApplicationType(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 得到描述
     *
     * @return {@link String }
     * @since 2023-07-04 10:06:06
     */
    //    @Override
    public String getDescription() {
        return description;
    }

    /**
     * 不加@JsonValue，转换的时候转换出完整的对象。 加了@JsonValue，只会显示相应的属性的值
     * <p>
     * 不使用@JsonValue @JsonDeserializer类里面要做相应的处理
     *
     * @return {@link Integer }
     * @since 2023-07-04 10:06:06
     */
    @JsonValue
    //    @Override
    public Integer getValue() {
        return value;
    }

    /**
     * 得到
     *
     * @param index 指数
     * @return {@link ApplicationType }
     * @since 2023-07-04 10:06:06
     */
    public static ApplicationType get(Integer index) {
        return INDEX_MAP.get(index);
    }

    /**
     * 得到预处理json结构
     *
     * @return {@link List }<{@link Map }<{@link String }, {@link Object }>>
     * @since 2023-07-04 10:06:06
     */
    public static List<Map<String, Object>> getPreprocessedJsonStructure() {
        return JSON_STRUCT;
    }
}
