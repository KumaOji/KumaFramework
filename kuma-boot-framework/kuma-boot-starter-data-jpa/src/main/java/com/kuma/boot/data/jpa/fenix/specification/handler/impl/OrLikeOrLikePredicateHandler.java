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
         throw new BuildSpecificationException("【Fenix 异常】对【" + name + "】使用【@OrLikeOrLike】时，属性类型不是数组或者 List 集合！");
      } else {
         String[] fields = ((OrLikeOrLike)annotation).fields();
         List<?> values = (List)value;
         if (fields.length != values.size()) {
            throw new BuildSpecificationException("【Fenix 异常】对【" + name + "】使用【@OrLikeOrLike】时，注解上【fields】长度和字段值的大小不同，fileds长为:【" + fields.length + "】,字段值大小为:【" + values.size() + "】.");
         } else {
            return criteriaBuilder.or(new Predicate[]{criteriaBuilder.or((Predicate[])super.buildLikeOrLikePredicates(criteriaBuilder, from, fields, values).toArray(new Predicate[0]))});
         }
      }
   }

   public Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<?, ?> from, String fieldName, Object value) {
      throw new BuildSpecificationException("不支持本方法.");
   }
}
