package com.kuma.boot.data.mongodb.helper.utils;

import com.kuma.boot.data.mongodb.helper.reflection.ReflectionUtil;
import com.kuma.boot.data.mongodb.helper.reflection.SerializableFunction;
import java.util.Collection;
import org.springframework.data.mongodb.core.query.Criteria;

public class CriteriaOrWrapper extends CriteriaWrapper {
   public CriteriaOrWrapper() {
      this.andLink = false;
   }

   public CriteriaOrWrapper or(Criteria criteria) {
      this.list.add(criteria);
      return this;
   }

   public CriteriaOrWrapper or(CriteriaWrapper criteriaWrapper) {
      this.list.add(criteriaWrapper.build());
      return this;
   }

   public <E, R> CriteriaOrWrapper eq(SerializableFunction<E, R> column, Object params) {
      super.eq(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper ne(SerializableFunction<E, R> column, Object params) {
      super.ne(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper lt(SerializableFunction<E, R> column, Object params) {
      super.lt(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper lte(SerializableFunction<E, R> column, Object params) {
      super.lte(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper gt(SerializableFunction<E, R> column, Object params) {
      super.gt(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper gte(SerializableFunction<E, R> column, Object params) {
      super.gte(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper contain(SerializableFunction<E, R> column, Object params) {
      super.contain(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper containOr(SerializableFunction<E, R> column, Collection<?> params) {
      super.containOr(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper containOr(SerializableFunction<E, R> column, Object[] params) {
      super.containOr(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper containAnd(SerializableFunction<E, R> column, Collection<?> params) {
      super.containAnd(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper containAnd(SerializableFunction<E, R> column, Object[] params) {
      super.containAnd(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper like(SerializableFunction<E, R> column, String params) {
      super.like(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper in(SerializableFunction<E, R> column, Collection<?> params) {
      super.in(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper in(SerializableFunction<E, R> column, Object[] params) {
      super.in(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper nin(SerializableFunction<E, R> column, Collection<?> params) {
      super.nin(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper nin(SerializableFunction<E, R> column, Object[] params) {
      super.nin(column, params);
      return this;
   }

   public <E, R> CriteriaOrWrapper isNull(SerializableFunction<E, R> column) {
      super.isNull(column);
      return this;
   }

   public <E, R> CriteriaOrWrapper isNotNull(SerializableFunction<E, R> column) {
      super.isNotNull(column);
      return this;
   }

   public <E, R> CriteriaOrWrapper findArray(SerializableFunction<E, R> arr, SerializableFunction<E, R> column, String param) {
      super.findArray(ReflectionUtil.getFieldName(arr), column, param);
      return this;
   }

   public <E, R> CriteriaOrWrapper findArrayLike(SerializableFunction<E, R> arr, SerializableFunction<E, R> column, String param) {
      super.findArrayLike(ReflectionUtil.getFieldName(arr), column, param);
      return this;
   }
}
