package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.specification.annotation.StartsWith;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;

public class StartsWithPredicateHandler extends AbstractPredicateHandler {
   public StartsWithPredicateHandler() {
   }

   public Class<StartsWith> getAnnotation() {
      return StartsWith.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value, Annotation annotation) {
      return criteriaBuilder.and(new Predicate[]{super.buildStartsWithPredicate(criteriaBuilder, from, fieldName, value)});
   }
}
