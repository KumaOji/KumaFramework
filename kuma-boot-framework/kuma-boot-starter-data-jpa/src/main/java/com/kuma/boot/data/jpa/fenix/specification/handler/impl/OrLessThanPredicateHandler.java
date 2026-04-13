package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.specification.annotation.OrLessThan;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;

public class OrLessThanPredicateHandler extends AbstractPredicateHandler {
   public OrLessThanPredicateHandler() {
   }

   public Class<OrLessThan> getAnnotation() {
      return OrLessThan.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value, Annotation annotation) {
      return criteriaBuilder.or(new Predicate[]{super.buildLessThanPredicate(criteriaBuilder, from, fieldName, value)});
   }
}
