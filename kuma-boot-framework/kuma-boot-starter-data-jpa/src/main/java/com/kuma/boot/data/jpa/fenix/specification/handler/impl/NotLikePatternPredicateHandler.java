package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.specification.annotation.NotLikePattern;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;

public class NotLikePatternPredicateHandler extends AbstractPredicateHandler {
   public NotLikePatternPredicateHandler() {
   }

   public Class<NotLikePattern> getAnnotation() {
      return NotLikePattern.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value, Annotation annotation) {
      return criteriaBuilder.and(new Predicate[]{super.buildNotLikePatternPredicate(criteriaBuilder, from, fieldName, value)});
   }
}
