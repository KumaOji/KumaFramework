package com.kuma.cloud.cache.simple;

import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.introspect.BeanPropertyDefinition;
import java.util.List;

public class CustomBeanDeserializerModifier extends ValueDeserializerModifier {

    @Override
    public ValueDeserializer<?> modifyDeserializer(DeserializationConfig config,
                                                  BeanDescription.Supplier beanDescRef,
                                                  ValueDeserializer<?> deserializer) {
        
        // 获取类中所有字段定义
        List<BeanPropertyDefinition> properties = beanDescRef.get().findProperties();
        
        for (BeanPropertyDefinition prop : properties) {
            // 检查字段是否带有自定义注解
            if (prop.getField().getAnnotation(SpecialField.class) != null) {
                // 只处理标量类型（String、Number等），确保原始反序列化器类型正确
                if (deserializer instanceof StdScalarDeserializer) {
                    // 强制转换为标量反序列化器（Jackson 2.19+ 类型检查更严格）
                    StdScalarDeserializer<?> scalarDeserializer = (StdScalarDeserializer<?>) deserializer;
                    // 创建包装器，传递原始反序列化器（关键修复点）
//                    return new CustomFieldDeserializer<>(prop.getRawPrimaryType(), scalarDeserializer);
                }
            }
        }
        
        // 非注解字段使用默认反序列化器
        return super.modifyDeserializer(config, beanDescRef, deserializer);
    }
}
