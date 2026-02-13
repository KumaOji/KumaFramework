/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.google.common.collect.Lists
 *  jakarta.annotation.Nullable
 *  tools.jackson.core.JacksonException
 *  tools.jackson.core.JsonParser
 *  tools.jackson.core.TreeNode
 *  tools.jackson.core.json.JsonReadFeature
 *  tools.jackson.core.type.TypeReference
 *  tools.jackson.databind.DeserializationFeature
 *  tools.jackson.databind.JacksonModule
 *  tools.jackson.databind.JavaType
 *  tools.jackson.databind.JsonNode
 *  tools.jackson.databind.PropertyNamingStrategies
 *  tools.jackson.databind.SerializationFeature
 *  tools.jackson.databind.cfg.DatatypeFeature
 *  tools.jackson.databind.cfg.DateTimeFeature
 *  tools.jackson.databind.json.JsonMapper
 *  tools.jackson.databind.node.ArrayNode
 *  tools.jackson.databind.node.ObjectNode
 *  tools.jackson.databind.type.CollectionLikeType
 *  tools.jackson.databind.type.MapType
 */
package com.kuma.boot.common.utils.json;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
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
import java.text.DateFormat;
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
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.cfg.DatatypeFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.type.CollectionLikeType;
import tools.jackson.databind.type.MapType;

public final class JacksonUtils {
    public static final JsonMapper MAPPER = JacksonUtils.getInstance();

    private JacksonUtils() {
    }

    public static JsonMapper getInstance() {
        return JacksonHolder.INSTANCE;
    }

    public static String toJSONString(Object object) {
        return JacksonUtils.toJSONString(object, false);
    }

    public static String toJSONString(Object object, boolean format) {
        try {
            if (object == null) {
                return "";
            }
            if (object instanceof Number) {
                return object.toString();
            }
            if (object instanceof String) {
                return (String)object;
            }
            if (format) {
                return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            }
            return MAPPER.writeValueAsString(object);
        }
        catch (JacksonException e) {
            throw new BaseException(e.getMessage());
        }
    }

    public static <T> T toObject(String json, Class<T> cls) {
        if (StrUtil.isBlank((CharSequence)json) || cls == null) {
            return null;
        }
        try {
            return (T)MAPPER.readValue(json, cls);
        }
        catch (JacksonException e) {
            throw new BaseException(e);
        }
    }

    public static <T> T toObject(String json, Class<?> parametrized, Class<?> ... parameterClasses) {
        if (StrUtil.isBlank((CharSequence)json) || parametrized == null) {
            return null;
        }
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(parametrized, (Class[])parameterClasses);
            return (T)MAPPER.readValue(json, javaType);
        }
        catch (JacksonException e) {
            throw new BaseException(e);
        }
    }

    public static <T> T toObject(String json, TypeReference<T> typeReference) {
        if (StrUtil.isBlank((CharSequence)json) || typeReference == null) {
            return null;
        }
        try {
            return (T)MAPPER.readValue(json, typeReference);
        }
        catch (JacksonException e) {
            throw new BaseException(e);
        }
    }

    public static JsonNode parse(String json) {
        if (StrUtil.isBlank((CharSequence)json)) {
            return null;
        }
        try {
            return MAPPER.readTree(json);
        }
        catch (JacksonException e) {
            throw new BaseException(e);
        }
    }

    public static <K, V> Map<K, V> toMap(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof String) {
            return JacksonUtils.toObject((String)object, Map.class);
        }
        return (Map)MAPPER.convertValue(object, Map.class);
    }

    public static <T> List<T> toList(String json) {
        if (StrUtil.isNotBlank((CharSequence)json)) {
            try {
                return (List)MAPPER.readValue(json, List.class);
            }
            catch (JacksonException e) {
                throw new BaseException(e.getMessage());
            }
        }
        return new ArrayList();
    }

    public static <T> List<T> toList(String json, Class<T> cls) {
        if (StrUtil.isBlank((CharSequence)json)) {
            return new ArrayList();
        }
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, new Class[]{cls});
            return (List)MAPPER.readValue(json, javaType);
        }
        catch (JacksonException e) {
            throw new BaseException(e.getMessage());
        }
    }

    @Nullable
    public static String toJson(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(object);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static byte[] toJsonAsBytes(@Nullable Object object) {
        if (object == null) {
            return new byte[0];
        }
        try {
            return MAPPER.writeValueAsBytes(object);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static JsonNode readTree(String jsonString) {
        Objects.requireNonNull(jsonString, "jsonString is null");
        try {
            return MAPPER.readTree(jsonString);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static JsonNode readTree(InputStream in) {
        Objects.requireNonNull(in, "InputStream in is null");
        try {
            return MAPPER.readTree(in);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static JsonNode readTree(byte[] content) {
        Objects.requireNonNull(content, "byte[] content is null");
        try {
            return MAPPER.readTree(content);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static JsonNode readTree(JsonParser jsonParser) {
        Objects.requireNonNull(jsonParser, "jsonParser is null");
        try {
            return MAPPER.readTree(jsonParser);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    @Nullable
    public static <T> T readValue(@Nullable byte[] content, Class<T> valueType) {
        if (ObjectUtils.isEmpty(content)) {
            return null;
        }
        try {
            return (T)MAPPER.readValue(content, valueType);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    @Nullable
    public static <T> T readValue(@Nullable String jsonString, Class<T> valueType) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return (T)MAPPER.readValue(jsonString, valueType);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    @Nullable
    public static <T> T readValue(@Nullable InputStream in, Class<T> valueType) {
        if (in == null) {
            return null;
        }
        try {
            return (T)MAPPER.readValue(in, valueType);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    @Nullable
    public static <T> T readValue(@Nullable byte[] content, TypeReference<T> typeReference) {
        if (ObjectUtils.isEmpty(content)) {
            return null;
        }
        try {
            return (T)MAPPER.readValue(content, typeReference);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    @Nullable
    public static <T> T readValue(@Nullable String jsonString, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return (T)MAPPER.readValue(jsonString, typeReference);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    @Nullable
    public static <T> T readValue(@Nullable InputStream in, TypeReference<T> typeReference) {
        if (in == null) {
            return null;
        }
        try {
            return (T)MAPPER.readValue(in, typeReference);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    @Nullable
    public static <T> T readValue(@Nullable byte[] content, JavaType javaType) {
        if (ObjectUtils.isEmpty(content)) {
            return null;
        }
        try {
            return (T)MAPPER.readValue(content, javaType);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    @Nullable
    public static <T> T readValue(@Nullable String jsonString, JavaType javaType) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return (T)MAPPER.readValue(jsonString, javaType);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    @Nullable
    public static <T> T readValue(@Nullable InputStream in, JavaType javaType) {
        if (in == null) {
            return null;
        }
        try {
            return (T)MAPPER.readValue(in, javaType);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static MapType getMapType(Class<?> valueClass) {
        return JacksonUtils.getMapType(String.class, valueClass);
    }

    public static MapType getMapType(Class<?> keyClass, Class<?> valueClass) {
        return MAPPER.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
    }

    public static CollectionLikeType getListType(Class<?> elementClass) {
        return MAPPER.getTypeFactory().constructCollectionLikeType(List.class, elementClass);
    }

    public static JavaType getParametricType(Class<?> parametrized, Class<?> ... parameterClasses) {
        return MAPPER.getTypeFactory().constructParametricType(parametrized, (Class[])parameterClasses);
    }

    public static <T> List<T> readList(@Nullable byte[] content, Class<T> elementClass) {
        if (ObjectUtils.isEmpty(content)) {
            return Collections.emptyList();
        }
        try {
            return (List)MAPPER.readValue(content, (JavaType)JacksonUtils.getListType(elementClass));
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static <T> List<T> readList(@Nullable InputStream content, Class<T> elementClass) {
        if (content == null) {
            return Collections.emptyList();
        }
        try {
            return (List)MAPPER.readValue(content, (JavaType)JacksonUtils.getListType(elementClass));
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static <T> List<T> readList(@Nullable String content, Class<T> elementClass) {
        if (ObjectUtils.isEmpty(content)) {
            return Collections.emptyList();
        }
        try {
            return (List)MAPPER.readValue(content, (JavaType)JacksonUtils.getListType(elementClass));
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static Map<String, Object> readMap(@Nullable byte[] content) {
        return JacksonUtils.readMap(content, Object.class);
    }

    public static Map<String, Object> readMap(@Nullable InputStream content) {
        return JacksonUtils.readMap(content, Object.class);
    }

    public static Map<String, Object> readMap(@Nullable String content) {
        return JacksonUtils.readMap(content, Object.class);
    }

    public static <V> Map<String, V> readMap(@Nullable byte[] content, Class<?> valueClass) {
        return JacksonUtils.readMap(content, String.class, valueClass);
    }

    public static <V> Map<String, V> readMap(@Nullable InputStream content, Class<?> valueClass) {
        return JacksonUtils.readMap(content, String.class, valueClass);
    }

    public static <V> Map<String, V> readMap(@Nullable String content, Class<?> valueClass) {
        return JacksonUtils.readMap(content, String.class, valueClass);
    }

    public static <K, V> Map<K, V> readMap(@Nullable byte[] content, Class<?> keyClass, Class<?> valueClass) {
        if (ObjectUtils.isEmpty(content)) {
            return Collections.emptyMap();
        }
        try {
            return (Map)MAPPER.readValue(content, (JavaType)JacksonUtils.getMapType(keyClass, valueClass));
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static <K, V> Map<K, V> readMap(@Nullable InputStream content, Class<?> keyClass, Class<?> valueClass) {
        if (ObjectUtils.isEmpty(content)) {
            return Collections.emptyMap();
        }
        try {
            return (Map)MAPPER.readValue(content, (JavaType)JacksonUtils.getMapType(keyClass, valueClass));
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static <K, V> Map<K, V> readMap(@Nullable String content, Class<?> keyClass, Class<?> valueClass) {
        if (ObjectUtils.isEmpty(content)) {
            return Collections.emptyMap();
        }
        try {
            return (Map)MAPPER.readValue(content, (JavaType)JacksonUtils.getMapType(keyClass, valueClass));
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return (T)MAPPER.convertValue(fromValue, toValueType);
    }

    public static <T> T convertValue(Object fromValue, JavaType toValueType) {
        return (T)MAPPER.convertValue(fromValue, toValueType);
    }

    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
        return (T)MAPPER.convertValue(fromValue, toValueTypeRef);
    }

    public static <T> T treeToValue(TreeNode treeNode, Class<T> valueType) {
        try {
            return (T)MAPPER.treeToValue(treeNode, valueType);
        }
        catch (JacksonException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static <T extends JsonNode> T valueToTree(@Nullable Object fromValue) {
        return (T)MAPPER.valueToTree(fromValue);
    }

    public static boolean isValidJson(String jsonString) {
        return JacksonUtils.isValidJson((JsonMapper mapper) -> {
            if (mapper != null) {
                mapper.readTree(jsonString);
            }
        });
    }

    public static boolean isValidJson(byte[] content) {
        return JacksonUtils.isValidJson((JsonMapper mapper) -> {
            if (mapper != null) {
                mapper.readTree(content);
            }
        });
    }

    public static boolean isValidJson(InputStream input) {
        return JacksonUtils.isValidJson((JsonMapper mapper) -> {
            if (mapper != null) {
                mapper.readTree(input);
            }
        });
    }

    public static boolean isValidJson(JsonParser jsonParser) {
        return JacksonUtils.isValidJson((JsonMapper mapper) -> {
            if (mapper != null) {
                mapper.readTree(jsonParser);
            }
        });
    }

    public static boolean isValidJson(CheckedConsumer<JsonMapper> consumer) {
        JsonMapper.Builder mapper = MAPPER.rebuild();
        mapper.enable(new DeserializationFeature[]{DeserializationFeature.FAIL_ON_TRAILING_TOKENS});
        mapper.enable(new DeserializationFeature[]{DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY});
        try {
            consumer.accept(mapper.build());
            return true;
        }
        catch (Throwable e) {
            return false;
        }
    }

    public static ObjectNode createObjectNode() {
        return MAPPER.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return MAPPER.createArrayNode();
    }

    public static List<String> getIndexList(String compressJsonPath, int size) {
        String json = FileUtils.getFileContent(compressJsonPath);
        if (StringUtils.isEmptyTrim(json) || size <= 0) {
            return Collections.emptyList();
        }
        List<Integer> prefixList = CollectionUtils.fill(size);
        return JacksonUtils.getIndexList(compressJsonPath, prefixList);
    }

    public static List<String> getIndexList(String compressJsonPath, List<?> indexPrefixList) {
        int i;
        String json = FileUtils.getFileContent(compressJsonPath);
        if (StringUtils.isEmptyTrim(json) || CollectionUtils.isEmpty(indexPrefixList)) {
            return Collections.emptyList();
        }
        List<String> results = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        List<String> indexList = new ArrayList<>();
        for (i = 0; i < json.length(); ++i) {
            char ch = json.charAt(i);
            if ('{' == ch) {
                stack.push(i);
            }
            if ('}' != ch) continue;
            Integer startIndex = (Integer)stack.pop();
            int endIndex = i + 1;
            int byteStartIndex = json.substring(0, startIndex).getBytes().length;
            int byteEndIndex = byteStartIndex + json.substring(startIndex, endIndex).getBytes().length;
            String result = byteStartIndex + "," + byteEndIndex;
            indexList.add(result);
        }
        for (i = 0; i < indexPrefixList.size(); ++i) {
            String prefix = JacksonUtils.getPrefix(indexPrefixList.get(i));
            String result = prefix + indexList.get(i);
            results.add(result);
        }
        return results;
    }

    private static String getPrefix(Object object) {
        if (ObjectUtils.isNull(object)) {
            return "";
        }
        String string = object.toString();
        if (StringUtils.isEmptyTrim(string)) {
            return "";
        }
        return string + " ";
    }

    private static class JacksonHolder {
        private static final JsonMapper INSTANCE = JsonMapper.builder()
                .findAndAddModules()
                .defaultLocale(Locale.CHINA)
                .defaultTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
                .configure((DatatypeFeature) DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .enable(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES)
                .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
                .enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS)
                .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
                .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
                .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
                .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA))
                .addModule(new JacksonModule())
                .build();

        private JacksonHolder() {
        }
    }
}

