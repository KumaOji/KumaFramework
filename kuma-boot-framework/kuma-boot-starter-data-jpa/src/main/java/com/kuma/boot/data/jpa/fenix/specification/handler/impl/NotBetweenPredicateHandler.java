package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.specification.annotation.NotBetween;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;

public class NotBetweenPredicateHandler extends AbstractPredicateHandler {
   public NotBetweenPredicateHandler() {
   }

   public Class<NotBetween> getAnnotation() {
      return NotBetween.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value, Annotation annotation) {
      return criteriaBuilder.and(new Predicate[]{criteriaBuilder.not(super.buildBetweenPredicate(criteriaBuilder, from, fieldName, value))});
   }
}
