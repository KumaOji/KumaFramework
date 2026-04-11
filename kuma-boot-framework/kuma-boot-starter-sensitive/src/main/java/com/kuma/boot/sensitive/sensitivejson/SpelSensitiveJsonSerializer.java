package com.kuma.boot.sensitive.sensitivejson;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Objects;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class SpelSensitiveJsonSerializer extends ValueSerializer<Object> {
   private String el;
   private String name;

   public SpelSensitiveJsonSerializer() {
   }

   public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty beanProperty) {
      SpelSensitive spelSensitive = (SpelSensitive)beanProperty.getAnnotation(SpelSensitive.class);
      if (Objects.nonNull(spelSensitive) && spelSensitive.value().trim().length() != 0) {
         this.el = spelSensitive.value();
         this.name = beanProperty.getName();
         return this;
      } else {
         return ctxt.findValueSerializer(beanProperty.getType());
      }
   }

   public void serialize(Object value, JsonGenerator jsonGenerator, SerializationContext ctxt) throws JacksonException {
      try {
         value = SpringElUtil.parse(this.el, SpringElUtil.getStandardEvaluationContext(this.name, value), Object.class);
      } catch (Exception e) {
         LogUtils.error(this.name + " el \u8868\u8fbe\u5f0f " + this.el + "\u9519\u8bef", new Object[]{e});
      }

      jsonGenerator.writeStartObject(value);
      jsonGenerator.writeEndObject();
   }
}
