package com.kuma.boot.data.jpa.fenix.specification.handler.impl;

import com.kuma.boot.data.jpa.fenix.exception.BuildSpecificationException;
import com.kuma.boot.data.jpa.fenix.specification.annotation.LikeOrLike;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public class LikeOrLikePredicateHandler extends AbstractPredicateHandler {
   public LikeOrLikePredicateHandler() {
   }

   public Class<LikeOrLike> getAnnotation() {
      return LikeOrLike.class;
   }

   public <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String name, Object value, Annotation annotation) {
      value = value.getClass().isArray() ? Arrays.asList(value) : value;
      if (!(value instanceof List)) {
         throw new BuildSpecificationException("【Fenix 异常】对【" + name + "】使用【@LikeOrLike】时，属性类型不是数组或者 List 集合！");
      } else {
         String[] fields = ((LikeOrLike)annotation).fields();
         List<?> values = (List)value;
         if (fields.length != values.size()) {
            throw new BuildSpecificationException("【Fenix 异常】对【" + name + "】使用【@LikeOrLike】时，注解上【fields】长度和字段值的大小不同，fileds 长为:【" + fields.length + "】,字段值大小为：【" + values.size() + "】.");
         } else {
            return criteriaBuilder.and(new Predicate[]{criteriaBuilder.or((Predicate[])super.buildLikeOrLikePredicates(criteriaBuilder, from, fields, values).toArray(new Predicate[0]))});
         }
      }
   }

   public Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<?, ?> from, String fieldName, Object value) {
      throw new BuildSpecificationException("不支持本方法.");
   }
}
