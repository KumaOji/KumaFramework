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
         throw new BuildSpecificationException("【Fenix 异常】使用【@Join】构建表连接时,【" + var10002 + ".getAnnotation()】获取到的值【" + this.getAnnotation().getName() + "】与字段使用的注解值【" + annotation.getClass().getName() + "】不同");
      } else {
         Join<X, ?> subJoin = from.join(name, ((com.kuma.boot.data.jpa.fenix.specification.annotation.Join)annotation).joinType());
         return criteriaBuilder.and((Predicate[])FenixSpecification.beanParamToPredicate(subJoin, criteriaBuilder, value).toArray(new Predicate[0]));
      }
   }

   public Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<?, ?> from, String fieldName, Object value) {
      throw new BuildSpecificationException("本方法暂不支持.");
   }
}
