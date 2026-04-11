package com.kuma.boot.sensitive.sensitivelog.core.util.condition;

import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.reflect.ClassUtils;
import com.kuma.boot.sensitive.sensitivelog.annotation.metadata.SensitiveCondition;
import com.kuma.boot.sensitive.sensitivelog.api.Condition;
import java.lang.annotation.Annotation;
import java.util.Optional;

public final class SensitiveConditions {
   private SensitiveConditions() {
   }

   public static Optional<Condition> getConditionOpt(final Annotation[] annotations) {
      for(Annotation annotation : annotations) {
         SensitiveCondition sensitiveCondition = (SensitiveCondition)annotation.annotationType().getAnnotation(SensitiveCondition.class);
         if (ObjectUtils.isNotNull(sensitiveCondition)) {
            Class<? extends Condition> customClass = sensitiveCondition.value();
            Condition condition = (Condition)ClassUtils.newInstance(customClass);
            return Optional.ofNullable(condition);
         }
      }

      return Optional.empty();
   }
}
