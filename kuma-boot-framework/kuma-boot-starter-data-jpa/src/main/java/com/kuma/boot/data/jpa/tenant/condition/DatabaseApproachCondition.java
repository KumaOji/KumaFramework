package com.kuma.boot.data.jpa.tenant.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class DatabaseApproachCondition implements Condition {
   private static final Logger log = LoggerFactory.getLogger(DatabaseApproachCondition.class);

   public DatabaseApproachCondition() {
   }

   public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
      return true;
   }
}
