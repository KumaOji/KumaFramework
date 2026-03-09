package com.kuma.cloud.cache.other1;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.*;
import tools.jackson.databind.deser.*;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.introspect.BeanPropertyDefinition;

import java.io.IOException;

public class CustomBeanDeserializerModifier extends ValueDeserializerModifier {

    @Override
    public ValueDeserializer<?> modifyDeserializer(DeserializationConfig config,
                                                   BeanDescription.Supplier beanDescRef,
                                                   ValueDeserializer<?> deserializer) {
        return new CustomBeanDeserializer(deserializer, config);
    }

    static class CustomBeanDeserializer extends ValueDeserializer<Object> {
        private final ValueDeserializer<?> delegate;
        private final DeserializationConfig config;

        public CustomBeanDeserializer(ValueDeserializer<?> delegate, DeserializationConfig config) {
            super(delegate.handledType());
            this.delegate = delegate;
            this.config = config;
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            Object bean = delegate.deserialize(p, ctxt);
            if (bean != null) {
                try {
                    processAnnotatedFields(bean, ctxt);
                } catch (IOException e) {
                    throw new JacksonException(e);
                }
            }
            return bean;
        }

        private void processAnnotatedFields(Object bean, DeserializationContext ctxt) throws IOException {
            Class<?> beanClass = bean.getClass();
            for (BeanPropertyDefinition propDef : config.classIntrospectorInstance()
                    .introspectForDeserialization(ctxt.getTypeFactory().constructType(beanClass), null).findProperties()) {
                if (propDef.hasField() && propDef.getField().hasAnnotation(CustomProcess.class)) {
                    processField(bean, propDef, ctxt);
                }
            }
        }

        private void processField(Object bean, BeanPropertyDefinition propDef, DeserializationContext ctxt) throws IOException {
            try {
                AnnotatedField field = propDef.getField();
                field.fixAccess(true);
                Object value = field.getValue(bean);
                if (value != null) {
                    CustomProcess annotation = field.getAnnotation(CustomProcess.class);
                    Object processedValue = processValue(value, annotation.processor());
                    field.setValue(bean, processedValue);
                }
            } catch (Exception e) {
                ctxt.handleInstantiationProblem(handledType(), bean, e);
            }
        }

        private Object processValue(Object value, String processor) {
            if (value instanceof String) {
                return ((String) value).toUpperCase();
            }
            return value;
        }
    }
}
