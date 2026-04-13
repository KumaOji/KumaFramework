package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.exception.BuildSpecificationException;
import com.kuma.boot.data.jpa.fenix.specification.annotation.OrLikeOrLike;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public class OrLikeOrLikePredicateHandler extends AbstractPredicateHandler {
   public OrLikeOrLikePredicateHandler() {
   }

   public Class<OrLikeOrLike> getAnnotation() {
      return OrLikeOrLike.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String name, Object value, Annotation annotation) {
      value = value.getClass().isArray() ? Arrays.asList(value) : value;
      if (!(value instanceof List)) {
         throw new BuildSpecificationException("\u3010Fenix \u5f02\u5e38\u3011\u5bf9\u3010" + name + "\u3011\u4f7f\u7528\u3010@OrLikeOrLike\u3011\u65f6\uff0c\u5c5e\u6027\u7c7b\u578b\u4e0d\u662f\u6570\u7ec4\u6216\u8005 List \u96c6\u5408\uff01");
      } else {
         String[] fields = ((OrLikeOrLike)annotation).fields();
         List<?> values = (List)value;
         if (fields.length != values.size()) {
            throw new BuildSpecificationException("\u3010Fenix \u5f02\u5e38\u3011\u5bf9\u3010" + name + "\u3011\u4f7f\u7528\u3010@OrLikeOrLike\u3011\u65f6\uff0c\u6ce8\u89e3\u4e0a\u3010fields\u3011\u957f\u5ea6\u548c\u5b57\u6bb5\u503c\u7684\u5927\u5c0f\u4e0d\u540c\uff0cfileds\u957f\u4e3a:\u3010" + fields.length + "\u3011,\u5b57\u6bb5\u503c\u5927\u5c0f\u4e3a:\u3010" + values.size() + "\u3011.");
         } else {
            return criteriaBuilder.or(new Predicate[]{criteriaBuilder.or((Predicate[])super.buildLikeOrLikePredicates(criteriaBuilder, from, fields, values).toArray(new Predicate[0]))});
         }
      }
   }

   public Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<?, ?> from, String fieldName, Object value) {
      throw new BuildSpecificationException("\u4e0d\u652f\u6301\u672c\u65b9\u6cd5.");
   }
}
