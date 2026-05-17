package com.kuma.boot.sensitive.sensitivejson;

import com.kuma.boot.core.utils.bean.BeanUtils;
import java.util.Objects;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class SensitiveJsonSerializer extends ValueSerializer<String> {
   private SensitiveStrategy strategy;
   private Condition condition;

   public SensitiveJsonSerializer() {
   }

   public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty beanProperty) {
      Sensitive sensitive = (Sensitive)beanProperty.getAnnotation(Sensitive.class);
      if (Objects.nonNull(sensitive) && Objects.equals(String.class, beanProperty.getType().getRawClass())) {
         this.strategy = sensitive.value();
         this.condition = (Condition)BeanUtils.newInstance(sensitive.condition());
         return this;
      } else {
         return ctxt.findValueSerializer(beanProperty.getType());
      }
   }

   public void serialize(String value, JsonGenerator jsonGenerator, SerializationContext ctxt) throws JacksonException {
      if (this.condition.valid()) {
         jsonGenerator.writeString((String)this.strategy.getDesensitizer().apply(value));
      } else {
         jsonGenerator.writeString(value);
      }

   }
}
