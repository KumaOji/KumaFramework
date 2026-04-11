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

public abstract class AbstractFastJsonSensitiveValueFilter {
   public AbstractFastJsonSensitiveValueFilter() {
   }

   protected Object process(Object object, String fieldName, Object fieldValue) {
      if (!ObjectUtils.isEmpty(fieldValue) && String.class.isAssignableFrom(fieldValue.getClass())) {
         HandlerMethodResolver methodResolver = (HandlerMethodResolver)SpringUtil.getBean(HandlerMethodResolver.class);
         HandlerMethod handlerMethod = methodResolver.resolve();
         if (Objects.isNull(handlerMethod)) {
            return fieldValue;
         } else {
            IgnoreSensitive ignoreSensitive = (IgnoreSensitive)AnnotationUtils.getAnnotation(handlerMethod, IgnoreSensitive.class);
            Optional<IgnoreSensitive> ignSensitiveOpt = Optional.ofNullable(ignoreSensitive);
            Optional<String[]> ignFieldNamesOpt = ignSensitiveOpt.map(IgnoreSensitive::value);
            if ((!ignSensitiveOpt.isPresent() || ignFieldNamesOpt.filter(ArrayUtil::isNotEmpty).isPresent()) && !ignFieldNamesOpt.filter((names) -> Arrays.asList(names).contains(fieldName)).isPresent()) {
               Field field = ReflectUtil.getField(object.getClass(), fieldName);
               Sensitive sensitive = (Sensitive)field.getAnnotation(Sensitive.class);
               if (Objects.isNull(sensitive)) {
                  return fieldValue;
               } else {
                  SensitiveStrategy strategy = sensitive.strategy();
                  LogUtils.debug("Sensitive for {} with {} strategy, replacer={}", new Object[]{fieldName, strategy.name(), sensitive.replacer()});
                  return strategy.apply(new SensitiveWrapper(object, fieldName, (String)fieldValue, sensitive.replacer()));
               }
            } else {
               LogUtils.debug("Skip sensitive for {}, because @IgnoreSensitive is null or not contains it.", new Object[]{fieldName});
               return fieldValue;
            }
         }
      } else {
         return fieldValue;
      }
   }
}
