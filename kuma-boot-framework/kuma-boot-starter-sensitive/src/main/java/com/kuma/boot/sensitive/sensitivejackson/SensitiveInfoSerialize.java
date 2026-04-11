package com.kuma.boot.sensitive.sensitivejackson;

import java.util.Objects;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class SensitiveInfoSerialize extends ValueSerializer<String> {
   private SensitiveType type;

   public SensitiveInfoSerialize() {
   }

   public void serialize(String value, JsonGenerator jsonGenerator, SerializationContext ctxt) throws JacksonException {
      switch (this.type) {
         case CHINESE_NAME -> jsonGenerator.writeString(SensitiveInfoUtils.chineseName(value));
         case ID_CARD -> jsonGenerator.writeString(SensitiveInfoUtils.idCardNum(value));
         case FIXED_PHONE -> jsonGenerator.writeString(SensitiveInfoUtils.fixedPhone(value));
         case MOBILE_PHONE -> jsonGenerator.writeString(SensitiveInfoUtils.mobilePhone(value));
         case ADDRESS -> jsonGenerator.writeString(SensitiveInfoUtils.address(value, 4));
         case EMAIL -> jsonGenerator.writeString(SensitiveInfoUtils.email(value));
         case BANK_CARD -> jsonGenerator.writeString(SensitiveInfoUtils.bankCard(value));
         case CNAPS_CODE -> jsonGenerator.writeString(SensitiveInfoUtils.cnapsCode(value));
      }

   }

   public SensitiveInfoSerialize(final SensitiveType type) {
      this.type = type;
   }

   public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty beanProperty) {
      if (beanProperty != null) {
         if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
            SensitiveInfo sensitiveInfo = (SensitiveInfo)beanProperty.getAnnotation(SensitiveInfo.class);
            if (sensitiveInfo == null) {
               sensitiveInfo = (SensitiveInfo)beanProperty.getContextAnnotation(SensitiveInfo.class);
            }

            if (sensitiveInfo != null) {
               return new SensitiveInfoSerialize(sensitiveInfo.value());
            }
         }

         return ctxt.findValueSerializer(beanProperty.getType());
      } else {
         return ctxt.findNullValueSerializer(beanProperty);
      }
   }
}
