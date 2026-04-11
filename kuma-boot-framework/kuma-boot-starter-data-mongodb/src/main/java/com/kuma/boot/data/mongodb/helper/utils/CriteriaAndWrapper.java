package com.kuma.boot.data.mongodb.helper.utils;

import com.kuma.boot.data.mongodb.helper.reflection.ReflectionUtil;
import com.kuma.boot.data.mongodb.helper.reflection.SerializableFunction;
import java.util.Collection;
import org.springframework.data.mongodb.core.query.Criteria;

public class CriteriaAndWrapper extends CriteriaWrapper {
   public CriteriaAndWrapper() {
      this.andLink = true;
   }

   public CriteriaAndWrapper and(Criteria criteria) {
      this.list.add(criteria);
      return this;
   }

   public CriteriaAndWrapper and(CriteriaWrapper criteriaWrapper) {
      this.list.add(criteriaWrapper.build());
      return this;
   }

   public <E, R> CriteriaAndWrapper eq(SerializableFunction<E, R> column, Object params) {
      super.eq(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper ne(SerializableFunction<E, R> column, Object params) {
      super.ne(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper lt(SerializableFunction<E, R> column, Object params) {
      super.lt(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper lte(SerializableFunction<E, R> column, Object params) {
      super.lte(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper gt(SerializableFunction<E, R> column, Object params) {
      super.gt(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper gte(SerializableFunction<E, R> column, Object params) {
      super.gte(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper contain(SerializableFunction<E, R> column, Object params) {
      super.contain(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper containOr(SerializableFunction<E, R> column, Collection<?> params) {
      super.containOr(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper containOr(SerializableFunction<E, R> column, Object[] params) {
      super.containOr(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper containAnd(SerializableFunction<E, R> column, Collection<?> params) {
      super.containAnd(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper containAnd(SerializableFunction<E, R> column, Object[] params) {
      super.containAnd(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper like(SerializableFunction<E, R> column, String params) {
      super.like(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper in(SerializableFunction<E, R> column, Collection<?> params) {
      super.in(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper in(SerializableFunction<E, R> column, Object[] params) {
      super.in(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper nin(SerializableFunction<E, R> column, Collection<?> params) {
      super.nin(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper nin(SerializableFunction<E, R> column, Object[] params) {
      super.nin(column, params);
      return this;
   }

   public <E, R> CriteriaAndWrapper isNull(SerializableFunction<E, R> column) {
      super.isNull(column);
      return this;
   }

   public <E, R> CriteriaAndWrapper isNotNull(SerializableFunction<E, R> column) {
      super.isNotNull(column);
      return this;
   }

   public <E, R> CriteriaAndWrapper findArray(SerializableFunction<E, R> arr, SerializableFunction<E, R> column, String param) {
      super.findArray(ReflectionUtil.getFieldName(arr), column, param);
      return this;
   }

   public <E, R> CriteriaAndWrapper findArrayLike(SerializableFunction<E, R> arr, SerializableFunction<E, R> column, String param) {
      super.findArrayLike(ReflectionUtil.getFieldName(arr), column, param);
      return this;
   }
}
