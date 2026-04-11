package com.kuma.boot.sensitive.desensitize;

import com.kuma.boot.common.holder.UserContextHolder;
import com.kuma.boot.common.model.BaseSecurityUser;
import com.kuma.boot.sensitive.desensitize.enums.SensitiveStrategy;
import java.util.Objects;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class SensitiveJsonSerializer extends ValueSerializer<String> implements ApplicationContextAware {
   private SensitiveStrategy strategy;
   private DesensitizeProperties desensitizeProperties;

   public SensitiveJsonSerializer() {
   }

   public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property) {
      if (this.desensitization()) {
         Sensitive annotation = (Sensitive)property.getAnnotation(Sensitive.class);
         if (Objects.nonNull(annotation) && Objects.equals(String.class, property.getType().getRawClass())) {
            this.strategy = annotation.strategy();
            return this;
         }
      }

      return ctxt.findValueSerializer(property.getType());
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.desensitizeProperties = (DesensitizeProperties)applicationContext.getBean(DesensitizeProperties.class);
   }

   private boolean desensitization() {
      BaseSecurityUser baseSecurityUser = UserContextHolder.getUser();
      if (baseSecurityUser == null) {
         return true;
      } else if (baseSecurityUser.getType() == 2) {
         return this.desensitizeProperties.getSensitiveLevel() == 2;
      } else if (baseSecurityUser.getType() == 1) {
         return this.desensitizeProperties.getSensitiveLevel() >= 1;
      } else {
         return false;
      }
   }

   public void serialize(String value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
      gen.writeString((String)this.strategy.desensitizer().apply(value));
   }
}
