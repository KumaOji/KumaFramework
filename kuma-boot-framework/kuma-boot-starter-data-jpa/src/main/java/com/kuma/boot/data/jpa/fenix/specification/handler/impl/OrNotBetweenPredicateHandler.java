package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.specification.annotation.OrNotBetween;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;

public class OrNotBetweenPredicateHandler extends AbstractPredicateHandler {
   public OrNotBetweenPredicateHandler() {
   }

   public Class<OrNotBetween> getAnnotation() {
      return OrNotBetween.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value, Annotation annotation) {
      return criteriaBuilder.or(new Predicate[]{criteriaBuilder.not(super.buildBetweenPredicate(criteriaBuilder, from, fieldName, value))});
   }
}
