package com.kuma.boot.data.jpa.fenix.specification.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.jpa.fenix.exception.BuildSpecificationException;
import com.kuma.boot.data.jpa.fenix.specification.handler.bean.BetweenValue;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class AbstractPredicateHandler implements PredicateHandler {
   public AbstractPredicateHandler() {
   }

   public abstract Class<? extends Annotation> getAnnotation();

   public abstract <Z, X> Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value, Annotation annotation);

   public Predicate buildPredicate(CriteriaBuilder criteriaBuilder, From<?, ?> from, String fieldName, Object value) {
      return this.buildPredicate(criteriaBuilder, from, fieldName, value, (Annotation)null);
   }

   protected <Z, X> Predicate buildEqualsPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value) {
      return criteriaBuilder.equal(from.get(fieldName), value);
   }

   protected <Z, X> Predicate buildNotEqualsPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value) {
      return criteriaBuilder.notEqual(from.get(fieldName), value);
   }

   protected <Z, X> Predicate buildGreaterThanPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value) {
      this.isValueComparable(value);
      return criteriaBuilder.greaterThan(from.get(fieldName), (Comparable)value);
   }

   protected <Z, X> Predicate buildGreaterThanEqualPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value) {
      this.isValueComparable(value);
      return criteriaBuilder.greaterThanOrEqualTo(from.get(fieldName), (Comparable)value);
   }

   protected <Z, X> Predicate buildLessThanPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value) {
      this.isValueComparable(value);
      return criteriaBuilder.lessThan(from.get(fieldName), (Comparable)value);
   }

   protected <Z, X> Predicate buildLessThanEqualPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value) {
      this.isValueComparable(value);
      return criteriaBuilder.lessThanOrEqualTo(from.get(fieldName), (Comparable)value);
   }

   private void isValueComparable(Object value) {
      if (!(value instanceof Comparable)) {
         throw new BuildSpecificationException("\u3010Fenix \u5f02\u5e38\u3011\u8981\u6bd4\u8f83\u7684 value \u503c\u3010" + String.valueOf(value) + "\u3011\u4e0d\u662f\u53ef\u6bd4\u8f83\u7c7b\u578b\u7684\uff0c\u8be5\u503c\u7684\u7c7b\u578b\u5fc5\u987b\u5b9e\u73b0\u4e86 java.lang.Comparable \u63a5\u53e3\u624d\u80fd\u6b63\u5e38\u53c2\u4e0e\u6bd4\u8f83\uff0c\u624d\u80fd\u7528\u4e8e\u5927\u4e8e\u3001\u5927\u4e8e\u7b49\u4e8e\u3001\u5c0f\u4e8e\u3001\u5c0f\u4e8e\u7b49\u4e8e\u4e4b\u7c7b\u7684\u6bd4\u8f83\u573a\u666f.");
      }
   }

   protected <Z, X> Predicate buildIsNullPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, Object value) {
      return criteriaBuilder.isNull(from.get(String.valueOf(value)));
   }

   protected <Z, X> Predicate buildIsNotNullPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, Object value) {
      return criteriaBuilder.isNotNull(from.get(String.valueOf(value)));
   }

   protected <Z, X> Predicate buildInPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value, boolean allowNull) {
      value = value.getClass().isArray() ? Arrays.asList(value) : value;
      Path<Object> path = from.get(fieldName);
      CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
      if (value instanceof Collection<?> list) {
         if (list.isEmpty()) {
            return criteriaBuilder.conjunction();
         }

         Objects.requireNonNull(in);
         list.forEach(in::value);
      } else {
         in.value(value);
      }

      return (Predicate)(allowNull ? criteriaBuilder.or(in, criteriaBuilder.isNull(path)) : in);
   }

   protected boolean isAllowNull(Object annotation) {
      try {
         return (Boolean)this.getAnnotation().getMethod("allowNull").invoke(annotation);
      } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | IllegalAccessException e) {
         LogUtils.error("\u3010Fenix \u9519\u8bef\u63d0\u793a\u3011\u83b7\u53d6\u3010@In\u3011\u3001\u3010@OrIn\u3011\u3001\u3010@NotIn\u3011\u3001\u3010@OrNotIn\u3011\u76f8\u5173\u6ce8\u89e3\u4e2d\u7684\u3010allowNull\u3011\u7684\u503c\u5931\u8d25\uff0c\u5c06\u9ed8\u8ba4\u8fd4\u56de false \u7684\u503c.", new Object[]{e});
         return false;
      }
   }

   protected <Z, X> Predicate buildLikePredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value) {
      Path var10001 = from.get(fieldName);
      String var10002 = this.convertValue(value);
      return criteriaBuilder.like(var10001, "%" + var10002 + "%");
   }

   protected <Z, X> Predicate buildNotLikePredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value) {
      Path var10001 = from.get(fieldName);
      String var10002 = this.convertValue(value);
      return criteriaBuilder.notLike(var10001, "%" + var10002 + "%");
   }

   protected <Z, X> Predicate buildStartsWithPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value) {
      Path var10001 = from.get(fieldName);
      String var10002 = this.convertValue(value);
      return criteriaBuilder.like(var10001, var10002 + "%");
   }

   protected <Z, X> Predicate buildNotStartsWithPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value) {
      Path var10001 = from.get(fieldName);
      String var10002 = this.convertValue(value);
      return criteriaBuilder.notLike(var10001, var10002 + "%");
   }

   protected <Z, X> Predicate buildEndsWithPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value) {
      Path var10001 = from.get(fieldName);
      String var10002 = this.convertValue(value);
      return criteriaBuilder.like(var10001, "%" + var10002);
   }

   protected <Z, X> Predicate buildNotEndsWithPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value) {
      Path var10001 = from.get(fieldName);
      String var10002 = this.convertValue(value);
      return criteriaBuilder.notLike(var10001, "%" + var10002);
   }

   protected <Z, X> Predicate buildLikePatternPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value) {
      return criteriaBuilder.like(from.get(fieldName), value.toString());
   }

   protected <Z, X> Predicate buildNotLikePatternPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value) {
      return criteriaBuilder.notLike(from.get(fieldName), value.toString());
   }

   protected <Z, X> List<Predicate> buildLikeOrLikePredicates(CriteriaBuilder criteriaBuilder, From<Z, X> from, String[] fields, List<?> values) {
      int len = fields.length;
      List<Predicate> predicates = new ArrayList(len);

      for(int i = 0; i < len; ++i) {
         Path var10002 = from.get(fields[i]);
         String var10003 = this.convertValue(values.get(i));
         predicates.add(criteriaBuilder.like(var10002, "%" + var10003 + "%"));
      }

      return predicates;
   }

   private String convertValue(Object value) {
      return value.toString().replace("%", "\\%");
   }

   protected <Z, X> Predicate buildBetweenPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object value) {
      if (value.getClass().isArray()) {
         Object[] arr = value;
         return this.buildBetweenPredicate(criteriaBuilder, from, fieldName, arr[0], arr[1]);
      } else if (value instanceof List) {
         List<?> list = (List)value;
         return this.buildBetweenPredicate(criteriaBuilder, from, fieldName, list.get(0), list.get(1));
      } else if (value instanceof BetweenValue) {
         BetweenValue<?> bv = (BetweenValue)value;
         return this.buildBetweenPredicate(criteriaBuilder, from, fieldName, bv.getStart(), bv.getEnd());
      } else {
         throw new BuildSpecificationException("\u3010Fenix \u5f02\u5e38\u3011\u6784\u5efa\u3010@Between\u3011\u6ce8\u89e3\u533a\u95f4\u67e5\u8be2\u65f6\uff0c\u53c2\u6570\u503c\u7c7b\u578b\u4e0d\u662f\u6570\u7ec4\u6216 List \u7c7b\u578b\u7684\u96c6\u5408\uff0c\u65e0\u6cd5\u83b7\u53d6\u5230\u524d\u540e\u7684\u533a\u95f4\u503c\u3002");
      }
   }

   private <Z, X> Predicate buildBetweenPredicate(CriteriaBuilder criteriaBuilder, From<Z, X> from, String fieldName, Object startValue, Object endValue) {
      if (startValue != null && endValue != null) {
         this.isValueComparable(startValue);
         this.isValueComparable(endValue);
         return criteriaBuilder.between(from.get(fieldName), (Comparable)startValue, (Comparable)endValue);
      } else if (startValue != null) {
         this.isValueComparable(startValue);
         return criteriaBuilder.greaterThanOrEqualTo(from.get(fieldName), (Comparable)startValue);
      } else if (endValue != null) {
         this.isValueComparable(endValue);
         return criteriaBuilder.lessThanOrEqualTo(from.get(fieldName), (Comparable)endValue);
      } else {
         throw new BuildSpecificationException("\u3010Fenix \u5f02\u5e38\u3011\u6784\u5efa\u3010@Between\u3011\u6ce8\u89e3\u533a\u95f4\u67e5\u8be2\u65f6\uff0c\u5f00\u59cb\u548c\u7ed3\u675f\u7684\u533a\u95f4\u503c\u5747\u4e3a\u3010null\u3011\uff0c\u65e0\u6cd5\u6784\u9020\u533a\u95f4\u6216\u5927\u4e8e\u7b49\u4e8e\u3001\u5c0f\u4e8e\u7b49\u4e8e\u7684 Predicate\u6761\u4ef6\u3002");
      }
   }
}
