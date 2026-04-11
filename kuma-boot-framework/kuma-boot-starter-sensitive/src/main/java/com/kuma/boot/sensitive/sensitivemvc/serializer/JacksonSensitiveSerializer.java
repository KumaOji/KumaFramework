package com.kuma.boot.sensitive.sensitivemvc.serializer;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sensitive.sensitivemvc.SensitiveStrategy;
import com.kuma.boot.sensitive.sensitivemvc.SensitiveWrapper;
import com.kuma.boot.sensitive.sensitivemvc.annocation.IgnoreSensitive;
import com.kuma.boot.sensitive.sensitivemvc.annocation.Sensitive;
import com.kuma.boot.sensitive.sensitivemvc.resolve.HandlerMethodResolver;
import com.kuma.boot.sensitive.sensitivemvc.util.AnnotationUtils;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class JacksonSensitiveSerializer extends ValueSerializer<String> {
   public JacksonSensitiveSerializer() {
   }

   public void serialize(String fieldValue, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
      if (Objects.isNull(fieldValue)) {
         gen.writeNull();
      } else {
         HandlerMethodResolver handlerMethodResolver = (HandlerMethodResolver)SpringUtil.getBean(HandlerMethodResolver.class);
         HandlerMethod handlerMethod = handlerMethodResolver.resolve();
         if (ObjectUtils.isEmpty(handlerMethod)) {
            gen.writeString(fieldValue);
         } else {
            String fieldName = "";
            IgnoreSensitive ignSensitive = (IgnoreSensitive)AnnotationUtils.getAnnotation(handlerMethod, IgnoreSensitive.class);
            Optional<IgnoreSensitive> ignSensitiveOpt = Optional.ofNullable(ignSensitive);
            Optional<String[]> ignFieldNamesOpt = ignSensitiveOpt.map(IgnoreSensitive::value);
            if ((!ignSensitiveOpt.isPresent() || ignFieldNamesOpt.filter(ArrayUtil::isNotEmpty).isPresent()) && !ignFieldNamesOpt.filter((names) -> Arrays.asList(names).contains(fieldName)).isPresent()) {
               Object object = gen.currentValue();
               Field field = ReflectUtil.getField(object.getClass(), fieldName);
               Sensitive sensitive = (Sensitive)field.getAnnotation(Sensitive.class);
               if (Objects.isNull(sensitive)) {
                  gen.writeString(fieldValue);
               } else {
                  SensitiveStrategy strategy = sensitive.strategy();
                  String finalValue = strategy.apply(new SensitiveWrapper(object, fieldName, fieldValue, sensitive.replacer()));
                  LogUtils.debug("Sensitive for {} with {} strategy, replacer={}", new Object[]{fieldName, strategy.name(), sensitive.replacer()});
                  gen.writeString(finalValue);
               }
            } else {
               gen.writeString(fieldValue);
               LogUtils.debug("Skip sensitive for {}, because @IgnoreSensitive is null or not contains it.", new Object[]{fieldName});
            }
         }
      }
   }

   public Class<String> handledType() {
      return String.class;
   }
}
