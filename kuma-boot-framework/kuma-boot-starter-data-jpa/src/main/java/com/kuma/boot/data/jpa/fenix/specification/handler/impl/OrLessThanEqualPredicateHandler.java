package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.specification.annotation.OrLessThanEqual;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;

public class OrLessThanEqualPredicateHandler extends AbstractPredicateHandler {
   public OrLessThanEqualPredicateHandler() {
   }

   public Class<OrLessThanEqual> getAnnotation() {
      return OrLessThanEqual.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value, Annotation annotation) {
      return criteriaBuilder.or(new Predicate[]{super.buildLessThanEqualPredicate(criteriaBuilder, from, fieldName, value)});
   }
}
