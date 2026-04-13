package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.exception.BuildSpecificationException;
import com.kuma.boot.data.jpa.fenix.specification.FenixSpecification;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;

public class JoinPredicateHandler extends AbstractPredicateHandler {
   public JoinPredicateHandler() {
   }

   public Class<com.kuma.boot.data.jpa.fenix.specification.annotation.Join> getAnnotation() {
      return com.kuma.boot.data.jpa.fenix.specification.annotation.Join.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String name, Object value, Annotation annotation) {
      if (!(annotation instanceof com.kuma.boot.data.jpa.fenix.specification.annotation.Join)) {
         String var10002 = this.getClass().getName();
         throw new BuildSpecificationException("\u3010Fenix \u5f02\u5e38\u3011\u4f7f\u7528\u3010@Join\u3011\u6784\u5efa\u8868\u8fde\u63a5\u65f6,\u3010" + var10002 + ".getAnnotation()\u3011\u83b7\u53d6\u5230\u7684\u503c\u3010" + this.getAnnotation().getName() + "\u3011\u4e0e\u5b57\u6bb5\u4f7f\u7528\u7684\u6ce8\u89e3\u503c\u3010" + annotation.getClass().getName() + "\u3011\u4e0d\u540c");
      } else {
         Join<X, ?> subJoin = from.join(name, ((com.kuma.boot.data.jpa.fenix.specification.annotation.Join)annotation).joinType());
         return criteriaBuilder.and((Predicate[])FenixSpecification.beanParamToPredicate(subJoin, criteriaBuilder, value).toArray(new Predicate[0]));
      }
   }

   public Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<?, ?> from, String fieldName, Object value) {
      throw new BuildSpecificationException("\u672c\u65b9\u6cd5\u6682\u4e0d\u652f\u6301.");
   }
}
