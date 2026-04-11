package com.kuma.boot.data.mongodb.helper.utils;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.data.mongodb.helper.reflection.ReflectionUtil;
import com.kuma.boot.data.mongodb.helper.reflection.SerializableFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.data.mongodb.core.query.Criteria;

public abstract class CriteriaWrapper {
   protected boolean andLink = true;
   protected Criteria criteria;
   protected List<Criteria> list = new ArrayList();

   public CriteriaWrapper() {
   }

   public Criteria build() {
      this.criteria = new Criteria();
      if (!this.list.isEmpty()) {
         if (this.andLink) {
            this.criteria.andOperator(this.listToArry(this.list));
         } else {
            this.criteria.orOperator(this.listToArry(this.list));
         }
      }

      return this.criteria;
   }

   public static String replaceRegExp(String str) {
      return StrUtil.isEmpty(str) ? str : str.replace("\\", "\\\\").replace("*", "\\*").replace("+", "\\+").replace("|", "\\|").replace("{", "\\{").replace("}", "\\}").replace("(", "\\(").replace(")", "\\)").replace("^", "\\^").replace("$", "\\$").replace("[", "\\[").replace("]", "\\]").replace("?", "\\?").replace(",", "\\,").replace(".", "\\.").replace("&", "\\&");
   }

   private Criteria[] listToArry(List<Criteria> list) {
      return (Criteria[])list.toArray(new Criteria[list.size()]);
   }

   public <E, R> CriteriaWrapper eq(SerializableFunction<E, R> column, Object params) {
      this.list.add(Criteria.where(ReflectionUtil.getFieldName(column)).is(params));
      return this;
   }

   public <E, R> CriteriaWrapper ne(SerializableFunction<E, R> column, Object params) {
      this.list.add(Criteria.where(ReflectionUtil.getFieldName(column)).ne(params));
      return this;
   }

   public <E, R> CriteriaWrapper lt(SerializableFunction<E, R> column, Object params) {
      this.list.add(Criteria.where(ReflectionUtil.getFieldName(column)).lt(params));
      return this;
   }

   public <E, R> CriteriaWrapper lte(SerializableFunction<E, R> column, Object params) {
      this.list.add(Criteria.where(ReflectionUtil.getFieldName(column)).lte(params));
      return this;
   }

   public <E, R> CriteriaWrapper gt(SerializableFunction<E, R> column, Object params) {
      this.list.add(Criteria.where(ReflectionUtil.getFieldName(column)).gt(params));
      return this;
   }

   public <E, R> CriteriaWrapper gte(SerializableFunction<E, R> column, Object params) {
      this.list.add(Criteria.where(ReflectionUtil.getFieldName(column)).gte(params));
      return this;
   }

   public <E, R> CriteriaWrapper contain(SerializableFunction<E, R> column, Object params) {
      this.list.add(Criteria.where(ReflectionUtil.getFieldName(column)).all(new Object[]{params}));
      return this;
   }

   public <E, R> CriteriaWrapper containOr(SerializableFunction<E, R> column, Collection<?> params) {
      CriteriaOrWrapper criteriaOrWrapper = new CriteriaOrWrapper();

      for(Object object : params) {
         criteriaOrWrapper.contain(column, object);
      }

      this.list.add(criteriaOrWrapper.build());
      return this;
   }

   public <E, R> CriteriaWrapper containOr(SerializableFunction<E, R> column, Object[] params) {
      return this.containOr(column, Arrays.asList(params));
   }

   public <E, R> CriteriaWrapper containAnd(SerializableFunction<E, R> column, Collection<?> params) {
      this.list.add(Criteria.where(ReflectionUtil.getFieldName(column)).all(params));
      return this;
   }

   public <E, R> CriteriaWrapper containAnd(SerializableFunction<E, R> column, Object[] params) {
      return this.containAnd(column, Arrays.asList(params));
   }

   public <E, R> CriteriaWrapper like(SerializableFunction<E, R> column, String params) {
      Pattern pattern = Pattern.compile("^.*" + replaceRegExp(params) + ".*$", 2);
      this.list.add(Criteria.where(ReflectionUtil.getFieldName(column)).regex(pattern));
      return this;
   }

   public <E, R> CriteriaWrapper in(SerializableFunction<E, R> column, Collection<?> params) {
      this.list.add(Criteria.where(ReflectionUtil.getFieldName(column)).in(params));
      return this;
   }

   public <E, R> CriteriaWrapper in(SerializableFunction<E, R> column, Object[] params) {
      return this.in(column, Arrays.asList(params));
   }

   public <E, R> CriteriaWrapper nin(SerializableFunction<E, R> column, Collection<?> params) {
      this.list.add(Criteria.where(ReflectionUtil.getFieldName(column)).nin(params));
      return this;
   }

   public <E, R> CriteriaWrapper nin(SerializableFunction<E, R> column, Object[] params) {
      return this.nin(column, Arrays.asList(params));
   }

   public <E, R> CriteriaWrapper isNull(SerializableFunction<E, R> column) {
      this.list.add(Criteria.where(ReflectionUtil.getFieldName(column)).is((Object)null));
      return this;
   }

   public <E, R> CriteriaWrapper isNotNull(SerializableFunction<E, R> column) {
      this.list.add(Criteria.where(ReflectionUtil.getFieldName(column)).ne((Object)null));
      return this;
   }

   public <E, R> CriteriaWrapper findArray(String arr, SerializableFunction<E, R> column, String param) {
      this.list.add(Criteria.where(arr).elemMatch(Criteria.where(ReflectionUtil.getFieldName(column)).is(param)));
      return this;
   }

   public <E, R> CriteriaWrapper findArrayLike(String arr, SerializableFunction<E, R> column, String param) {
      Pattern pattern = Pattern.compile("^.*" + replaceRegExp(param) + ".*$", 2);
      this.list.add(Criteria.where(arr).elemMatch(Criteria.where(ReflectionUtil.getFieldName(column)).regex(pattern)));
      return this;
   }
}
