package com.kuma.boot.sensitive.sensitivelog.core.util.strategy;

import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.core.utils.reflect.ClassUtils;
import com.kuma.boot.sensitive.sensitivelog.annotation.metadata.SensitiveStrategy;
import com.kuma.boot.sensitive.sensitivelog.annotation.strategy.SensitiveStrategyCardId;
import com.kuma.boot.sensitive.sensitivelog.annotation.strategy.SensitiveStrategyChineseName;
import com.kuma.boot.sensitive.sensitivelog.annotation.strategy.SensitiveStrategyEmail;
import com.kuma.boot.sensitive.sensitivelog.annotation.strategy.SensitiveStrategyPassword;
import com.kuma.boot.sensitive.sensitivelog.annotation.strategy.SensitiveStrategyPhone;
import com.kuma.boot.sensitive.sensitivelog.api.Strategy;
import com.kuma.boot.sensitive.sensitivelog.api.impl.SensitiveStrategyBuiltIn;
import com.kuma.boot.sensitive.sensitivelog.core.api.strategory.StrategyCardId;
import com.kuma.boot.sensitive.sensitivelog.core.api.strategory.StrategyChineseName;
import com.kuma.boot.sensitive.sensitivelog.core.api.strategory.StrategyEmail;
import com.kuma.boot.sensitive.sensitivelog.core.api.strategory.StrategyPassword;
import com.kuma.boot.sensitive.sensitivelog.core.api.strategory.StrategyPhone;
import com.kuma.boot.sensitive.sensitivelog.core.exception.SensitiveRuntimeException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class SensitiveStrategyBuiltInUtil {
   private static final Map<Class<? extends Annotation>, Strategy> MAP = new HashMap();

   private SensitiveStrategyBuiltInUtil() {
   }

   public static Strategy require(final Class<? extends Annotation> annotationClass) {
      Strategy strategy = (Strategy)MAP.get(annotationClass);
      if (ObjectUtils.isNull(strategy)) {
         throw new SensitiveRuntimeException("不支持的系统内置方法，用户请勿在自定义注解中使用[SensitiveStrategyBuiltIn]!");
      } else {
         return strategy;
      }
   }

   public static Optional<Strategy> getStrategyOpt(final Annotation[] annotations) {
      for(Annotation annotation : annotations) {
         SensitiveStrategy sensitiveStrategy = (SensitiveStrategy)annotation.annotationType().getAnnotation(SensitiveStrategy.class);
         if (ObjectUtils.isNotNull(sensitiveStrategy)) {
            Class<? extends Strategy> clazz = sensitiveStrategy.value();
            Strategy strategy = null;
            if (SensitiveStrategyBuiltIn.class.equals(clazz)) {
               strategy = require(annotation.annotationType());
            } else {
               strategy = (Strategy)ClassUtils.newInstance(clazz);
            }

            return Optional.ofNullable(strategy);
         }
      }

      return Optional.empty();
   }

   static {
      MAP.put(SensitiveStrategyCardId.class, new StrategyCardId());
      MAP.put(SensitiveStrategyPassword.class, new StrategyPassword());
      MAP.put(SensitiveStrategyPhone.class, new StrategyPhone());
      MAP.put(SensitiveStrategyChineseName.class, new StrategyChineseName());
      MAP.put(SensitiveStrategyEmail.class, new StrategyEmail());
   }
}
