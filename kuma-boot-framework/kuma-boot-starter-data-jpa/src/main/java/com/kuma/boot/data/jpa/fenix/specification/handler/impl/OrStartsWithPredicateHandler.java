package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.specification.annotation.OrStartsWith;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;

public class OrStartsWithPredicateHandler extends AbstractPredicateHandler {
   public OrStartsWithPredicateHandler() {
   }

   public Class<OrStartsWith> getAnnotation() {
      return OrStartsWith.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value, Annotation annotation) {
      return criteriaBuilder.or(new Predicate[]{super.buildStartsWithPredicate(criteriaBuilder, from, fieldName, value)});
   }
}
