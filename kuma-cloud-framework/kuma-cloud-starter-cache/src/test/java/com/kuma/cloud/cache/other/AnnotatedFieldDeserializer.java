package com.kuma.cloud.cache.other;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;

public class AnnotatedFieldDeserializer extends StdDeserializer<Object> {

    private final ValueDeserializer<?> defaultDeserializer;

    public AnnotatedFieldDeserializer(ValueDeserializer<?> defaultDeserializer) {
        super(defaultDeserializer.handledType());
        this.defaultDeserializer = defaultDeserializer;
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) {
        // 先使用默认反序列化器获取值
        Object value = defaultDeserializer.deserialize(p, ctxt);
        // 检查当前字段是否有我们的注解
        return value;
    }

    private Object processValue(Object value, String processor) {
        // 这里实现你的业务逻辑
        if (value instanceof String) {
            return ((String) value).toUpperCase(); // 示例：转为大写
        }
        return value;
    }
}
