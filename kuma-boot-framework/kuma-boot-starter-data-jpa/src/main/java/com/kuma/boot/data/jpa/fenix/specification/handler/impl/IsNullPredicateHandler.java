package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.specification.annotation.IsNull;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;

public class IsNullPredicateHandler extends AbstractPredicateHandler {
   public IsNullPredicateHandler() {
   }

   public Class<IsNull> getAnnotation() {
      return IsNull.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String name, Object value, Annotation annotation) {
      return criteriaBuilder.and(new Predicate[]{super.buildIsNullPredicate(criteriaBuilder, from, value)});
   }
}
