/*
 * Copyright 2023-2024 the original author or authors.
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

package cn.kuma.blog.framework.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.SqlDateSerializer;
import io.micrometer.common.lang.Nullable;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Unified ObjectMapper instances holder and some helper functions
 * <p>
 * Jackson ObjectMappers are thread-safe and it's always a good practice to reuse them if
 * possible Ref:
 * https://github.com/FasterXML/jackson-docs/wiki/Presentation:-Jackson-Performance
 *
 * @author Zisen Sha
 */
public final class JsonUtil {

    // The ObjectMapper#findAndRegisterModules call is necessary to make sure they support
    // Java 8 features correctly.
    // Notable supports are:
    // - Support for javac -parameters compiler flag to avoid spamming @JsonProperty
    // annotation in constructors.
    // - Support for java.time.* types (JSR-310).
    // - Support for new Java 8 null-safe data-types: Optional, OptionalLong,
    // OptionalDouble, etc.

    /**
     * The default mapper should be used across all situations if no special handling are
     * required.
     */
    public static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().findAndRegisterModules();

    /**
     * This mapper is mainly made for JAX-RS scenario for backward compatibility reasons.
     *
     * @see JsonUtil#DEFAULT_MAPPER
     * @deprecated This shouldn't be used in the most of situations because the only
     * difference between this and the default one is how the {@link java.sql.Date} gets
     * serialized.
     */
    @Deprecated
    public static final ObjectMapper LEGACY_MAPPER = new ObjectMapper().findAndRegisterModules();

    /**
     * The alternate instance of default mapper with pretty printing enabled.
     */
    public static final ObjectMapper PRETTY_MAPPER = new ObjectMapper().findAndRegisterModules()
        .enable(SerializationFeature.INDENT_OUTPUT);

    public static final ObjectMapper INCLUDE_NONE_NULL_MAPPER = new ObjectMapper().findAndRegisterModules();

    static {
        SimpleModule customModule = new SimpleModule();
        customModule.addSerializer(java.sql.Date.class, new CustomSqlDateSerializer());
        LEGACY_MAPPER.registerModule(customModule);
        INCLUDE_NONE_NULL_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private JsonUtil() {
    }

    /**
     * Silent version of {@link ObjectMapper#writeValueAsString(Object)}. Function callers
     * should think twice whether this is a good practice.
     */
    public static String toJsonSilent(Object object) {
        try {
            return DEFAULT_MAPPER.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Nullable version of {@link ObjectMapper#writeValueAsString(Object)}. Function
     * callers should think twice whether this is a good practice.
     */
    @Nullable
    public static String toJsonOrNull(Object object) {
        try {
            return DEFAULT_MAPPER.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * Nullable version of {@link ObjectMapper#writeValueAsString(Object)} with pretty
     * printing. Function callers should think twice whether this is a good practice.
     */
    @Nullable
    public static String toJsonPrettyOrNull(Object object) {
        try {
            return PRETTY_MAPPER.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * Nullable version of {@link ObjectMapper#readValue(String, TypeReference)}. Function
     * callers should think twice whether this is a good practice.
     */
    @Nullable
    public static Map<String, Object> fromJsonAsMapOrNull(String json) {
        try {
            return DEFAULT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        }
        catch (IOException e) {
            return null;
        }
    }

    /**
     * Nullable version of {@link ObjectMapper#readValue(java.io.File, TypeReference)}
     * with UTF-8 as encoding. Function callers should think twice whether this is a good
     * practice.
     */
    @Nullable
    public static Map<String, Object> fromFileAsMapOrNull(String filePath) {
        try {
            byte[] content = Files.readAllBytes(Paths.get(filePath));
            return JsonUtil.fromJsonAsMapOrNull(new String(content, StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public static <T> T readValueOrNull(String json, Class<T> valueType) {
        try {
            return DEFAULT_MAPPER.readValue(json, valueType);
        }
        catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public static <T> T readValueOrThrow(String json, Class<T> valueType) throws JsonProcessingException {
        try {
            return DEFAULT_MAPPER.readValue(json, valueType);
        }
        catch (IOException e) {
            throw e;
        }
    }

    /**
     * Custom {@link java.sql.Date} serializer forces string output.
     */
    public static class CustomSqlDateSerializer extends SqlDateSerializer {

        public CustomSqlDateSerializer() {
            super(Boolean.FALSE, null);
        }

    }

    /**
     * Nullable version of {@link ObjectMapper#readValue(String, TypeReference)}. Function
     * callers should think twice whether this is a good practice.
     */
    @Nullable
    public static <T> T readValueOrNull(String json, TypeReference<T> valueTypeRef) {
        try {
            return DEFAULT_MAPPER.readValue(json, valueTypeRef);
        }
        catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public static String toJsonOrNull(Object object, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * 将对象转换为 JSON 字符串（常用方法）
     *
     * @param object 要转换的对象
     * @return JSON 字符串
     * @throws RuntimeException 如果转换失败
     */
    public static String toJson(Object object) {
        try {
            return DEFAULT_MAPPER.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("对象转JSON失败", e);
        }
    }

    /**
     * 将对象转换为格式化的 JSON 字符串（常用方法）
     *
     * @param object 要转换的对象
     * @return 格式化的 JSON 字符串
     * @throws RuntimeException 如果转换失败
     */
    public static String toJsonPretty(Object object) {
        try {
            return PRETTY_MAPPER.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("对象转JSON失败", e);
        }
    }

    /**
     * 将 JSON 字符串转换为对象（常用方法）
     *
     * @param json      JSON 字符串
     * @param valueType 目标类型
     * @param <T>       目标类型泛型
     * @return 转换后的对象
     * @throws RuntimeException 如果转换失败
     */
    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            return DEFAULT_MAPPER.readValue(json, valueType);
        }
        catch (IOException e) {
            throw new RuntimeException("JSON转对象失败: " + valueType.getName(), e);
        }
    }

    /**
     * 将 JSON 字符串转换为对象（支持泛型）
     *
     * @param json        JSON 字符串
     * @param valueTypeRef 类型引用
     * @param <T>         目标类型泛型
     * @return 转换后的对象
     * @throws RuntimeException 如果转换失败
     */
    public static <T> T fromJson(String json, TypeReference<T> valueTypeRef) {
        try {
            return DEFAULT_MAPPER.readValue(json, valueTypeRef);
        }
        catch (IOException e) {
            throw new RuntimeException("JSON转对象失败", e);
        }
    }

    /**
     * 将 JSON 字符串转换为 Map
     *
     * @param json JSON 字符串
     * @return Map对象
     * @throws RuntimeException 如果转换失败
     */
    public static Map<String, Object> fromJsonAsMap(String json) {
        try {
            return DEFAULT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        }
        catch (IOException e) {
            throw new RuntimeException("JSON转Map失败", e);
        }
    }

    /**
     * 将对象转换为 JSON 字符串（忽略 null 值）
     *
     * @param object 要转换的对象
     * @return JSON 字符串
     * @throws RuntimeException 如果转换失败
     */
    public static String toJsonIgnoreNull(Object object) {
        try {
            return INCLUDE_NONE_NULL_MAPPER.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("对象转JSON失败", e);
        }
    }

}
