package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.specification.annotation.OrNotEquals;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;

public class OrNotEqualsPredicateHandler extends AbstractPredicateHandler {
   public OrNotEqualsPredicateHandler() {
   }

   public Class<OrNotEquals> getAnnotation() {
      return OrNotEquals.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value, Annotation annotation) {
      return criteriaBuilder.or(new Predicate[]{super.buildNotEqualsPredicate(criteriaBuilder, from, fieldName, value)});
   }
}
