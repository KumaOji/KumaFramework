package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.specification.annotation.NotIn;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class NotInPredicateHandler extends AbstractPredicateHandler {
   public NotInPredicateHandler() {
   }

   public Class<NotIn> getAnnotation() {
      return NotIn.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value, Annotation annotation) {
      return this.buildPredicate(criteriaBuilder, from, fieldName, value);
   }

   public Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<?, ?> from, String fieldName, Object value) {
      Path<Object> path = from.get(fieldName);
      CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
      value = value.getClass().isArray() ? Arrays.asList(value) : value;
      if (value instanceof Collection<?> list) {
         if (list.isEmpty()) {
            return criteriaBuilder.conjunction();
         }

         Objects.requireNonNull(in);
         list.forEach(in::value);
      } else {
         in.value(value);
      }

      return criteriaBuilder.and(new Predicate[]{criteriaBuilder.not(in)});
   }
}
