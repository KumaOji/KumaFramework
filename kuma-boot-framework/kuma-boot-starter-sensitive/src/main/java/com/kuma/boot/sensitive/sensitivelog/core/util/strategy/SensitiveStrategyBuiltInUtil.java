package com.kuma.boot.sensitive.sensitivelog.core.util.strategy;

import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.reflect.ClassUtils;
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
         throw new SensitiveRuntimeException("\u4e0d\u652f\u6301\u7684\u7cfb\u7edf\u5185\u7f6e\u65b9\u6cd5\uff0c\u7528\u6237\u8bf7\u52ff\u5728\u81ea\u5b9a\u4e49\u6ce8\u89e3\u4e2d\u4f7f\u7528[SensitiveStrategyBuiltIn]!");
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
