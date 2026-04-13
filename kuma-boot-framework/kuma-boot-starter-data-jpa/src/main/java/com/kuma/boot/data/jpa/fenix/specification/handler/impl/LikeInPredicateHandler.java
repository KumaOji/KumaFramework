package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.helper.CollectionHelper;
import com.kuma.boot.data.jpa.fenix.specification.annotation.LikeIn;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

public class LikeInPredicateHandler extends AbstractPredicateHandler {
   public LikeInPredicateHandler() {
   }

   public Class<LikeIn> getAnnotation() {
      return LikeIn.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value, Annotation annotation) {
      return this.buildPredicate(criteriaBuilder, from, fieldName, value);
   }

   public Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<?, ?> from, String fieldName, Object value) {
      Predicate p = null;
      List<Object> list = (List)value;
      if (CollectionHelper.isNotEmpty((Collection)list)) {
         int i = 0;

         for(int len = list.size(); i < len; ++i) {
            Object v = list.get(i);
            String var10000 = String.valueOf(v);
            String pattern = "%" + var10000.replace("%", "\\%") + "%";
            if (i == 0) {
               p = criteriaBuilder.like(from.get(fieldName), pattern);
            } else {
               p = criteriaBuilder.or(criteriaBuilder.like(from.get(fieldName), pattern), p);
            }
         }
      }

      return criteriaBuilder.and(new Predicate[]{p});
   }
}
