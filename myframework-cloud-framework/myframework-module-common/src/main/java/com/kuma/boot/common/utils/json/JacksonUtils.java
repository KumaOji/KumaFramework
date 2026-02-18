/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.utils.json;


import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.TreeNode;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.type.CollectionLikeType;
import tools.jackson.databind.type.MapType;
import com.google.common.collect.Lists;
import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.constant.PunctuationConstants;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.support.function.CheckedConsumer;
import com.kuma.boot.common.support.jackson.JacksonModule;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.exception.ExceptionUtils;
import com.kuma.boot.common.utils.io.FileUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import jakarta.annotation.Nullable;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.TimeZone;
import cn.hutool.core.util.StrUtil;

/**
 * 基于 Jackson 的 json 工具类
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 16:37:15
 */
public final class JacksonUtils {

    private JacksonUtils() {}

    /** MAPPER */
    public static final JsonMapper MAPPER  = getInstance();

    /**
     * 获取 JsonMapper 实例
     *
     * @return JsonMapper
     */
    public static JsonMapper getInstance() {
        return JacksonHolder.INSTANCE;
    }

    private static class JacksonHolder {
//		private static final JsonMapper INSTANCE = JsonMapper.builder()
//			// 可解析反斜杠引用的所有字符
//			.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true)
//			// 允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）
//			.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true)
//			// 单引号
//			.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES, true)
//			// 忽略json字符串中不识别的属性
//			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//			// 忽略json字符串中不识别的属性
//			.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
//			.defaultLocale(Locale.CHINA)
//			.defaultTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
//			.defaultDateFormat(new SimpleDateFormat(DateUtil.PATTERN_DATETIME, Locale.CHINA))
//			.findAndAddModules()
//			.build();

        private static final JsonMapper INSTANCE  = JsonMapper.builder()

                .findAndAddModules()
                .defaultLocale(Locale.CHINA)
                // 时区
                .defaultTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
                // 去掉默认的时间戳格式
                .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                // 忽略在json字符串中存在，但是在java对象中不存在对应属性的情况
                // 忽略未知字段 忽略json字符串中不识别的属性
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // 忽略空Bean转json的错误
                // 在使用spring boot +
                // jpa/hibernate，如果实体字段上加有FetchType.LAZY，并使用jackson序列化为json串时，会遇到SerializationFeature.FAIL_ON_EMPTY_BEANS异常
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                // 允许不带引号的字段名称
                .configure(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES, true)
                // 允许单引号
                .configure(JsonReadFeature.ALLOW_SINGLE_QUOTES, true)
                // allow int startWith 0
                .configure(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS, true)
                // 允许字符串存在转义字符：\r \n \t
                // 该特性决定parser是否允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）。
                // 如果该属性关闭，则如果遇到这些字符，则会抛出异常。JSON标准说明书要求所有控制符必须使用引号，因此这是一个非标准的特性
                .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true)
                // 忽略不能转义的字符 可解析反斜杠引用的所有字符
                .configure( JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true)
                // 包含null
//		builder.setDefaultPropertyInclusion(Include.ALWAYS);
                // 使用驼峰式
                .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
                // 使用bean名称
                // MAPPER.enable(MapperFeature.USE_STD_BEAN_NAMING);
                // 所有日期格式都统一为固定格式
                .defaultDateFormat(new SimpleDateFormat(CommonConstants.DATETIME_FORMAT, Locale.CHINA))

//		builder.setSerializerFactory(
//			builder.getSerializerFactory()
//                        .withSerializerModifier(new MyBeanSerializerModifier()));

                // 注册自定义模块
                //builder.addModule(new Jdk8Module());
                .addModule(new JacksonModule())

                .build();
    }

//    static {
//		JsonMapper.Builder builder = JsonMapper.builder();
//
//		builder.findAndAddModules();
//		builder.defaultLocale(Locale.CHINA);
//        // 时区
//		builder.defaultTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
//        // 去掉默认的时间戳格式
//		builder.configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        // 忽略在json字符串中存在，但是在java对象中不存在对应属性的情况
//        // 忽略未知字段 忽略json字符串中不识别的属性
//		builder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        // 忽略空Bean转json的错误
//        // 在使用spring boot +
//        // jpa/hibernate，如果实体字段上加有FetchType.LAZY，并使用jackson序列化为json串时，会遇到SerializationFeature.FAIL_ON_EMPTY_BEANS异常
//		builder.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        // 允许不带引号的字段名称
//		builder.configure(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES, true);
//        // 允许单引号
//		builder.configure(JsonReadFeature.ALLOW_SINGLE_QUOTES, true);
//        // allow int startWith 0
//		builder.configure(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS, true);
//        // 允许字符串存在转义字符：\r \n \t
//        // 该特性决定parser是否允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）。
//        // 如果该属性关闭，则如果遇到这些字符，则会抛出异常。JSON标准说明书要求所有控制符必须使用引号，因此这是一个非标准的特性
//		builder.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true);
//        // 忽略不能转义的字符 可解析反斜杠引用的所有字符
//		builder.configure(
//                JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
//        // 包含null
////		builder.setDefaultPropertyInclusion(Include.ALWAYS);
//        // 使用驼峰式
//		builder.propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
//        // 使用bean名称
//        // MAPPER.enable(MapperFeature.USE_STD_BEAN_NAMING);
//        // 所有日期格式都统一为固定格式
//		builder.defaultDateFormat(new SimpleDateFormat(CommonConstants.DATETIME_FORMAT, Locale.CHINA));
//
////		builder.setSerializerFactory(
////			builder.getSerializerFactory()
////                        .withSerializerModifier(new MyBeanSerializerModifier()));
//
//        // 注册自定义模块
//		//builder.addModule(new Jdk8Module());
//		builder.addModule(new JacksonModule());
//
//		MAPPER = builder.build();
//    }

    /**
     * 对象转换为json字符串
     * @param object 要转换的对象
     * @return {@link String }
     * @since 2021-09-02 16:37:25
     */
    public static String toJSONString(Object object) {
        return toJSONString(object, false);
    }

    /**
     * 对象转换为json字符串
     * @param object 要转换的对象
     * @param format 是否格式化json
     * @return {@link String }
     * @since 2021-09-02 16:37:57
     */
    public static String toJSONString(Object object, boolean format) {
        try {
            if (object == null) {
                return "";
            }
            if (object instanceof Number) {
                return object.toString();
            }
            if (object instanceof String) {
                return (String) object;
            }
            if (format) {
                return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            }
            return MAPPER.writeValueAsString(object);
        } catch (JacksonException e) {
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 字符串转换为指定对象
     * @param json json字符串
     * @param cls 目标对象
     * @return T
     * @since 2021-09-02 16:38:13
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(String json, Class<T> cls) {
        if (StrUtil.isBlank(json) || cls == null) {
            return null;
        }
        try {
            return MAPPER.readValue(json, cls);
        } catch (JacksonException e) {
            throw new BaseException(e);
        }
    }

    /**
     * 字符串转换为指定对象，并增加泛型转义 例如：List<Integer> test = toObject(jsonStr, List.class,
     * Integer.class);
     * @param json json字符串
     * @param parametrized 目标对象
     * @param parameterClasses 泛型对象
     * @return T
     * @since 2021-09-02 16:38:36
     */
    public static <T> T toObject(String json, Class<?> parametrized, Class<?>... parameterClasses) {
        if (StrUtil.isBlank(json) || parametrized == null) {
            return null;
        }
        try {
            JavaType javaType =
                    MAPPER.getTypeFactory().constructParametricType(parametrized, parameterClasses);
            return MAPPER.readValue(json, javaType);
        } catch (JacksonException e) {
            throw new BaseException(e);
        }
    }

    /**
     * 字符串转换为指定对象
     * @param json json字符串
     * @param typeReference 目标对象类型
     * @return T
     * @since 2021-09-02 16:38:49
     */
    public static <T> T toObject(String json, TypeReference<T> typeReference) {
        if (StrUtil.isBlank(json) || typeReference == null) {
            return null;
        }
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (JacksonException e) {
            throw new BaseException(e);
        }
    }

    /**
     * 字符串转换为JsonNode对象
     * @param json json字符串
     * @return {@link JsonNode }
     * @since 2021-09-02 16:39:01
     */
    public static JsonNode parse(String json) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        try {
            return MAPPER.readTree(json);
        } catch (JacksonException e) {
            throw new BaseException(e);
        }
    }

    /**
     * 对象转换为map对象
     * @param object 要转换的对象
     * @return {@link Map }
     * @since 2021-09-02 16:39:09
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> toMap(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof String) {
            return toObject((String) object, Map.class);
        }
        return MAPPER.convertValue(object, Map.class);
    }

    /**
     * json字符串转换为list对象
     * @param json json字符串
     * @return {@link List }
     * @since 2021-09-02 16:39:18
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(String json) {
        if (StrUtil.isNotBlank(json)) {
            try {
                return MAPPER.readValue(json, List.class);
            } catch (JacksonException e) {
                throw new BaseException(e.getMessage());
            }
        }
        return new ArrayList<>();
    }

    /**
     * json字符串转换为list对象，并指定元素类型
     * @param json json字符串
     * @param cls list的元素类型
     * @return {@link List }
     * @since 2021-09-02 16:39:29
     */
    public static <T> List<T> toList(String json, Class<T> cls) {
        if (StrUtil.isBlank(json)) {
            return new ArrayList<>();
        }
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, cls);
            return MAPPER.readValue(json, javaType);
        } catch (JacksonException e) {
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 将对象序列化成json字符串
     * @param object javaBean
     * @return jsonString json字符串
     */
    @Nullable
    public static String toJson(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 将对象序列化成 json byte 数组
     * @param object javaBean
     * @return jsonString json字符串
     */
    public static byte[] toJsonAsBytes(@Nullable Object object) {
        if (object == null) {
            return new byte[0];
        }
        try {
            return MAPPER.writeValueAsBytes(object);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 将json字符串转成 JsonNode
     * @param jsonString jsonString
     * @return jsonString json字符串
     */
    public static JsonNode readTree(String jsonString) {
        Objects.requireNonNull(jsonString, "jsonString is null");
        try {
            return MAPPER.readTree(jsonString);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 将json字符串转成 JsonNode
     * @param in InputStream
     * @return jsonString json字符串
     */
    public static JsonNode readTree(InputStream in) {
        Objects.requireNonNull(in, "InputStream in is null");
        try {
            return MAPPER.readTree(in);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 将json字符串转成 JsonNode
     * @param content content
     * @return jsonString json字符串
     */
    public static JsonNode readTree(byte[] content) {
        Objects.requireNonNull(content, "byte[] content is null");
        try {
            return MAPPER.readTree(content);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 将json字符串转成 JsonNode
     * @param jsonParser JsonParser
     * @return jsonString json字符串
     */
    public static JsonNode readTree(JsonParser jsonParser) {
        Objects.requireNonNull(jsonParser, "jsonParser is null");
        try {
            return MAPPER.readTree(jsonParser);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 将json byte 数组反序列化成对象
     * @param content json bytes
     * @param valueType class
     * @param <T> T 泛型标记
     * @return Bean
     */
    @Nullable
    public static <T> T readValue(@Nullable byte[] content, Class<T> valueType) {
        if (ObjectUtils.isEmpty(content)) {
            return null;
        }
        try {
            return MAPPER.readValue(content, valueType);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 将json反序列化成对象
     * @param jsonString jsonString
     * @param valueType class
     * @param <T> T 泛型标记
     * @return Bean
     */
    @Nullable
    public static <T> T readValue(@Nullable String jsonString, Class<T> valueType) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return MAPPER.readValue(jsonString, valueType);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 将json反序列化成对象
     * @param in InputStream
     * @param valueType class
     * @param <T> T 泛型标记
     * @return Bean
     */
    @Nullable
    public static <T> T readValue(@Nullable InputStream in, Class<T> valueType) {
        if (in == null) {
            return null;
        }
        try {
            return MAPPER.readValue(in, valueType);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 将json反序列化成对象
     * @param content bytes
     * @param typeReference 泛型类型
     * @param <T> T 泛型标记
     * @return Bean
     */
    @Nullable
    public static <T> T readValue(@Nullable byte[] content, TypeReference<T> typeReference) {
        if (ObjectUtils.isEmpty(content)) {
            return null;
        }
        try {
            return MAPPER.readValue(content, typeReference);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 将json反序列化成对象
     * @param jsonString jsonString
     * @param typeReference 泛型类型
     * @param <T> T 泛型标记
     * @return Bean
     */
    @Nullable
    public static <T> T readValue(@Nullable String jsonString, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return MAPPER.readValue(jsonString, typeReference);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 将json反序列化成对象
     * @param in InputStream
     * @param typeReference 泛型类型
     * @param <T> T 泛型标记
     * @return Bean
     */
    @Nullable
    public static <T> T readValue(@Nullable InputStream in, TypeReference<T> typeReference) {
        if (in == null) {
            return null;
        }
        try {
            return MAPPER.readValue(in, typeReference);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 将json反序列化成对象
     * @param content bytes
     * @param javaType JavaType
     * @param <T> T 泛型标记
     * @return Bean
     */
    @Nullable
    public static <T> T readValue(@Nullable byte[] content, JavaType javaType) {
        if (ObjectUtils.isEmpty(content)) {
            return null;
        }
        try {
            return MAPPER.readValue(content, javaType);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 将json反序列化成对象
     * @param jsonString jsonString
     * @param javaType JavaType
     * @param <T> T 泛型标记
     * @return Bean
     */
    @Nullable
    public static <T> T readValue(@Nullable String jsonString, JavaType javaType) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return MAPPER.readValue(jsonString, javaType);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 将json反序列化成对象
     * @param in InputStream
     * @param javaType JavaType
     * @param <T> T 泛型标记
     * @return Bean
     */
    @Nullable
    public static <T> T readValue(@Nullable InputStream in, JavaType javaType) {
        if (in == null) {
            return null;
        }
        try {
            return MAPPER.readValue(in, javaType);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 封装 map type，keyClass String
     * @param valueClass value 类型
     * @return MapType
     */
    public static MapType getMapType(Class<?> valueClass) {
        return getMapType(String.class, valueClass);
    }

    /**
     * 封装 map type
     * @param keyClass key 类型
     * @param valueClass value 类型
     * @return MapType
     */
    public static MapType getMapType(Class<?> keyClass, Class<?> valueClass) {
        return MAPPER.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
    }

    /**
     * 封装 map type
     * @param elementClass 集合值类型
     * @return CollectionLikeType
     */
    public static CollectionLikeType getListType(Class<?> elementClass) {
        return MAPPER.getTypeFactory().constructCollectionLikeType(List.class, elementClass);
    }

    /**
     * 封装参数化类型
     *
     * <p>
     * 例如： Map.class, String.class, String.class 对应 Map[String, String]
     * @param parametrized 泛型参数化
     * @param parameterClasses 泛型参数类型
     * @return JavaType
     */
    public static JavaType getParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
        return MAPPER.getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }

    /**
     * 读取集合
     * @param content bytes
     * @param elementClass elementClass
     * @param <T> 泛型
     * @return 集合
     */
    public static <T> List<T> readList(@Nullable byte[] content, Class<T> elementClass) {
        if (ObjectUtils.isEmpty(content)) {
            return Collections.emptyList();
        }
        try {
            return MAPPER.readValue(content, getListType(elementClass));
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 读取集合
     * @param content InputStream
     * @param elementClass elementClass
     * @param <T> 泛型
     * @return 集合
     */
    public static <T> List<T> readList(@Nullable InputStream content, Class<T> elementClass) {
        if (content == null) {
            return Collections.emptyList();
        }
        try {
            return MAPPER.readValue(content, getListType(elementClass));
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 读取集合
     * @param content bytes
     * @param elementClass elementClass
     * @param <T> 泛型
     * @return 集合
     */
    public static <T> List<T> readList(@Nullable String content, Class<T> elementClass) {
        if (ObjectUtils.isEmpty(content)) {
            return Collections.emptyList();
        }
        try {
            return MAPPER.readValue(content, getListType(elementClass));
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 读取集合
     * @param content bytes
     * @return 集合
     */
    public static Map<String, Object> readMap(@Nullable byte[] content) {
        return readMap(content, Object.class);
    }

    /**
     * 读取集合
     * @param content InputStream
     * @return 集合
     */
    public static Map<String, Object> readMap(@Nullable InputStream content) {
        return readMap(content, Object.class);
    }

    /**
     * 读取集合
     * @param content bytes
     * @return 集合
     */
    public static Map<String, Object> readMap(@Nullable String content) {
        return readMap(content, Object.class);
    }

    /**
     * 读取集合
     * @param content bytes
     * @param valueClass 值类型
     * @param <V> 泛型
     * @return 集合
     */
    public static <V> Map<String, V> readMap(@Nullable byte[] content, Class<?> valueClass) {
        return readMap(content, String.class, valueClass);
    }

    /**
     * 读取集合
     * @param content InputStream
     * @param valueClass 值类型
     * @param <V> 泛型
     * @return 集合
     */
    public static <V> Map<String, V> readMap(@Nullable InputStream content, Class<?> valueClass) {
        return readMap(content, String.class, valueClass);
    }

    /**
     * 读取集合
     * @param content bytes
     * @param valueClass 值类型
     * @param <V> 泛型
     * @return 集合
     */
    public static <V> Map<String, V> readMap(@Nullable String content, Class<?> valueClass) {
        return readMap(content, String.class, valueClass);
    }

    /**
     * 读取集合
     * @param content bytes
     * @param keyClass key类型
     * @param valueClass 值类型
     * @param <K> 泛型
     * @param <V> 泛型
     * @return 集合
     */
    public static <K, V> Map<K, V> readMap(
            @Nullable byte[] content, Class<?> keyClass, Class<?> valueClass) {
        if (ObjectUtils.isEmpty(content)) {
            return Collections.emptyMap();
        }
        try {
            return MAPPER.readValue(content, getMapType(keyClass, valueClass));
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 读取集合
     * @param content InputStream
     * @param keyClass key类型
     * @param valueClass 值类型
     * @param <K> 泛型
     * @param <V> 泛型
     * @return 集合
     */
    public static <K, V> Map<K, V> readMap(
            @Nullable InputStream content, Class<?> keyClass, Class<?> valueClass) {
        if (ObjectUtils.isEmpty(content)) {
            return Collections.emptyMap();
        }
        try {
            return MAPPER.readValue(content, getMapType(keyClass, valueClass));
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 读取集合
     * @param content bytes
     * @param keyClass key类型
     * @param valueClass 值类型
     * @param <K> 泛型
     * @param <V> 泛型
     * @return 集合
     */
    public static <K, V> Map<K, V> readMap(
            @Nullable String content, Class<?> keyClass, Class<?> valueClass) {
        if (ObjectUtils.isEmpty(content)) {
            return Collections.emptyMap();
        }
        try {
            return MAPPER.readValue(content, getMapType(keyClass, valueClass));
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * jackson 的类型转换
     * @param fromValue 来源对象
     * @param toValueType 转换的类型
     * @param <T> 泛型标记
     * @return 转换结果
     */
    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return MAPPER.convertValue(fromValue, toValueType);
    }

    /**
     * jackson 的类型转换
     * @param fromValue 来源对象
     * @param toValueType 转换的类型
     * @param <T> 泛型标记
     * @return 转换结果
     */
    public static <T> T convertValue(Object fromValue, JavaType toValueType) {
        return MAPPER.convertValue(fromValue, toValueType);
    }

    /**
     * jackson 的类型转换
     * @param fromValue 来源对象
     * @param toValueTypeRef 泛型类型
     * @param <T> 泛型标记
     * @return 转换结果
     */
    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
        return MAPPER.convertValue(fromValue, toValueTypeRef);
    }

    /**
     * tree 转对象
     * @param treeNode TreeNode
     * @param valueType valueType
     * @param <T> 泛型标记
     * @return 转换结果
     */
    public static <T> T treeToValue(TreeNode treeNode, Class<T> valueType) {
        try {
            return MAPPER.treeToValue(treeNode, valueType);
        } catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 对象转 tree
     * @param fromValue fromValue
     * @param <T> 泛型标记
     * @return 转换结果
     */
    public static <T extends JsonNode> T valueToTree(@Nullable Object fromValue) {
        return MAPPER.valueToTree(fromValue);
    }

    /**
     * 判断是否可以序列化
     * @param value 对象
     * @return 是否可以序列化
     */
//    public static boolean canSerialize(@Nullable Object value) {
//        if (value == null) {
//            return true;
//        }
//        return MAPPER.canSerialize(value.getClass());
//    }

    /**
     * 判断是否可以反序列化
     * @param type JavaType
     * @return 是否可以反序列化
     */
//    public static boolean canDeserialize(JavaType type) {
//        return MAPPER.canDeserialize(type);
//    }

    /**
     * 检验 json 格式
     * @param jsonString json 字符串
     * @return 是否成功
     */
    public static boolean isValidJson(String jsonString) {

        return isValidJson(mapper -> {
            if (mapper != null) {
                mapper.readTree(jsonString);
            }
        });
    }

    /**
     * 检验 json 格式
     * @param content json byte array
     * @return 是否成功
     */
    public static boolean isValidJson(byte[] content) {
        return isValidJson(mapper -> {
            if (mapper != null) {
                mapper.readTree(content);
            }
        });
    }

    /**
     * 检验 json 格式
     * @param input json input stream
     * @return 是否成功
     */
    public static boolean isValidJson(InputStream input) {
        return isValidJson(mapper -> {
            if (mapper != null) {
                mapper.readTree(input);
            }
        });
    }

    /**
     * 检验 json 格式
     * @param jsonParser json parser
     * @return 是否成功
     */
    public static boolean isValidJson(JsonParser jsonParser) {
        return isValidJson(mapper -> {
            if (mapper != null) {
                mapper.readTree(jsonParser);
            }
        });
    }

    /**
     * 检验 json 格式
     * @param consumer JsonMapper consumer
     * @return 是否成功
     */
    public static boolean isValidJson(CheckedConsumer<JsonMapper> consumer) {
        JsonMapper.Builder mapper = MAPPER.rebuild();
        mapper.enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        try {
            consumer.accept(mapper.build());
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * 创建 ObjectNode
     * @return ObjectNode
     */
    public static ObjectNode createObjectNode() {
        return MAPPER.createObjectNode();
    }

    /**
     * 创建 ArrayNode
     * @return ArrayNode
     */
    public static ArrayNode createArrayNode() {
        return MAPPER.createArrayNode();
    }

    /**
     * 获取索引列表
     * @param compressJsonPath 压缩的 json 路径
     * @param size 大下
     * @return 结果列表
     */
    public static List<String> getIndexList(final String compressJsonPath, final int size) {
        final String json = FileUtils.getFileContent(compressJsonPath);
        if (StringUtils.isEmptyTrim(json) || size <= 0) {
            return Collections.emptyList();
        }

        List<Integer> prefixList = CollectionUtils.fill(size);
        return getIndexList(compressJsonPath, prefixList);
    }

    /**
     * 获取索引列表
     * @param compressJsonPath 压缩的 json 路径
     * @param indexPrefixList 索引前缀列表
     * @return 结果列表
     */
    public static List<String> getIndexList(
            final String compressJsonPath, final List<?> indexPrefixList) {
        final String json = FileUtils.getFileContent(compressJsonPath);
        if (StringUtils.isEmptyTrim(json) || CollectionUtils.isEmpty(indexPrefixList)) {
            return Collections.emptyList();
        }

        List<String> results = Lists.newArrayList();
        Stack<Integer> stack = new Stack<>();
        List<String> indexList = Lists.newArrayList();

        for (int i = 0; i < json.length(); i++) {
            final char ch = json.charAt(i);

            if ('{' == ch) {
                stack.push(i);
            }
            if ('}' == ch) {
                Integer startIndex = stack.pop();
                int endIndex = i + 1;

                final int byteStartIndex = json.substring(0, startIndex).getBytes().length;
                final int byteEndIndex =
                        byteStartIndex + json.substring(startIndex, endIndex).getBytes().length;

                String result = byteStartIndex + PunctuationConstants.COMMA + byteEndIndex;
                indexList.add(result);
            }
        }

        for (int i = 0; i < indexPrefixList.size(); i++) {
            final String prefix = getPrefix(indexPrefixList.get(i));
            String result = prefix + indexList.get(i);
            results.add(result);
        }

        return results;
    }

    /**
     * 获取前缀
     * @param object 对象
     * @return 结果
     */
    private static String getPrefix(Object object) {
        if (ObjectUtils.isNull(object)) {
            return StringUtils.EMPTY;
        }
        String string = object.toString();
        if (StringUtils.isEmptyTrim(string)) {
            return StringUtils.EMPTY;
        }

        return string + StringUtils.BLANK;
    }
}
