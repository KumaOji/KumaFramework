package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.specification.annotation.EndsWith;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;

public class EndsWithPredicateHandler extends AbstractPredicateHandler {
   public EndsWithPredicateHandler() {
   }

   public Class<EndsWith> getAnnotation() {
      return EndsWith.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value, Annotation annotation) {
      return criteriaBuilder.and(new Predicate[]{super.buildEndsWithPredicate(criteriaBuilder, from, fieldName, value)});
   }
}
