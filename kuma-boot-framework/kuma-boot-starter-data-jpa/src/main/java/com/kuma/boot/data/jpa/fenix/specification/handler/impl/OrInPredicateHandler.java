package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.specification.annotation.OrIn;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;

public class OrInPredicateHandler extends AbstractPredicateHandler {
   public OrInPredicateHandler() {
   }

   public Class<OrIn> getAnnotation() {
      return OrIn.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value, Annotation annotation) {
      return criteriaBuilder.or(new Predicate[]{super.buildInPredicate(criteriaBuilder, from, fieldName, value, super.isAllowNull(annotation))});
   }

   public Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<?, ?> from, String fieldName, Object value) {
      return criteriaBuilder.or(new Predicate[]{super.buildInPredicate(criteriaBuilder, from, fieldName, value, false)});
   }
}
