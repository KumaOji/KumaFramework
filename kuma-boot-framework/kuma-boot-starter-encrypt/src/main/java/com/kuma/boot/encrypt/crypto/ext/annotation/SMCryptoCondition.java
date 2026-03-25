package com.kuma.boot.encrypt.crypto.ext.annotation;

import com.kuma.boot.common.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.encrypt.crypto.ext.CryptoStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class SMCryptoCondition implements Condition {
   private static final Logger log = LoggerFactory.getLogger(SMCryptoCondition.class);

   public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
      String property = (String)PropertyUtils.getProperty("kuma.boot.crypto.crypto-strategy", CryptoStrategy.SM.name());
      boolean result = StringUtils.isNotBlank(property) && StringUtils.equalsIgnoreCase(property, CryptoStrategy.SM.name());
      log.debug("Condition [SM Crypto Strategy] value is [{}]", result);
      return result;
   }
}
