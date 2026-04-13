package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.specification.annotation.In;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;

public class InPredicateHandler extends AbstractPredicateHandler {
   public InPredicateHandler() {
   }

   public Class<In> getAnnotation() {
      return In.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value, Annotation annotation) {
      return criteriaBuilder.and(new Predicate[]{super.buildInPredicate(criteriaBuilder, from, fieldName, value, super.isAllowNull(annotation))});
   }

   public Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<?, ?> from, String fieldName, Object value) {
      return criteriaBuilder.and(new Predicate[]{super.buildInPredicate(criteriaBuilder, from, fieldName, value, false)});
   }
}
