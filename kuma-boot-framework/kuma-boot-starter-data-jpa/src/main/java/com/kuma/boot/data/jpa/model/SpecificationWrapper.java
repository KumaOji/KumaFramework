package com.kuma.boot.data.jpa.model;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

public class SpecificationWrapper implements Specification {
   private SpecificationBuilder specificationBuilder = new SpecificationBuilder();
   private Map<QueryOperator, List<SpecificationWrapper>> specificationWrapperMap = new HashMap(1);
   private String joinTable;

   public SpecificationWrapper() {
   }

   protected <T> SpecificationWrapper(QueryOperator operator, SFunction<T, ?> func, Object obj) {
      this.addQueryCondition(operator, func, obj);
   }

   private <T> String getKey(SFunction<T, ?> func) {
      return FieldUtils.propertyName(func);
   }

   private <T> SpecificationWrapper addQueryCondition(QueryOperator operator, SFunction<T, ?> func, Object obj) {
      this.specificationBuilder.getQueryConditions().add(new QueryCondition(operator, this.getKey(func), obj));
      return this;
   }

   private <T> void addOrderCondition(SFunction<T, ?> func, Sort.Direction direction) {
      QueryCondition queryCondition = new QueryCondition((QueryOperator)null, this.getKey(func), (Object)null);
      queryCondition.direction = direction;
      this.specificationBuilder.getOrderConditions().add(queryCondition);
   }

   public static SpecificationWrapper where() {
      return new SpecificationWrapper();
   }

   public <T> SpecificationWrapper eq(SFunction<T, ?> func, Object obj) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.EQ, func, obj);
      return this;
   }

   public <T> SpecificationWrapper eq(Boolean bool, SFunction<T, ?> func, Object obj) {
      return bool ? this.addQueryCondition(SpecificationWrapper.QueryOperator.EQ, func, obj) : this;
   }

   public <T> SpecificationWrapper ne(SFunction<T, ?> func, Object obj) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.NE, func, obj);
      return this;
   }

   public <T> SpecificationWrapper ne(Boolean bool, SFunction<T, ?> func, Object obj) {
      return bool ? this.addQueryCondition(SpecificationWrapper.QueryOperator.NE, func, obj) : this;
   }

   public <T> SpecificationWrapper gt(SFunction<T, ?> func, Object obj) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.GT, func, obj);
      return this;
   }

   public <T> SpecificationWrapper gt(Boolean bool, SFunction<T, ?> func, Object obj) {
      return bool ? this.addQueryCondition(SpecificationWrapper.QueryOperator.GT, func, obj) : this;
   }

   public <T> SpecificationWrapper gte(SFunction<T, ?> func, Object obj) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.GE, func, obj);
      return this;
   }

   public <T> SpecificationWrapper gte(Boolean bool, SFunction<T, ?> func, Object obj) {
      return bool ? this.addQueryCondition(SpecificationWrapper.QueryOperator.GE, func, obj) : this;
   }

   public <T> SpecificationWrapper lt(SFunction<T, ?> func, Object obj) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.LT, func, obj);
      return this;
   }

   public <T> SpecificationWrapper lt(Boolean bool, SFunction<T, ?> func, Object obj) {
      return bool ? this.addQueryCondition(SpecificationWrapper.QueryOperator.LT, func, obj) : this;
   }

   public <T> SpecificationWrapper lte(SFunction<T, ?> func, Object obj) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.LE, func, obj);
      return this;
   }

   public <T> SpecificationWrapper lte(Boolean bool, SFunction<T, ?> func, Object obj) {
      return bool ? this.addQueryCondition(SpecificationWrapper.QueryOperator.LE, func, obj) : this;
   }

   public <T> SpecificationWrapper like(SFunction<T, ?> func, Object obj) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.LIKE, func, "%" + String.valueOf(obj) + "%");
      return this;
   }

   public <T> SpecificationWrapper like(Boolean bool, SFunction<T, ?> func, Object obj) {
      return bool ? this.addQueryCondition(SpecificationWrapper.QueryOperator.LIKE, func, "%" + String.valueOf(obj) + "%") : this;
   }

   public <T> SpecificationWrapper leftLike(SFunction<T, ?> func, Object obj) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.LIKE, func, "%" + String.valueOf(obj));
      return this;
   }

   public <T> SpecificationWrapper leftLike(Boolean bool, SFunction<T, ?> func, Object obj) {
      return bool ? this.addQueryCondition(SpecificationWrapper.QueryOperator.LIKE, func, "%" + String.valueOf(obj)) : this;
   }

   public <T> SpecificationWrapper rightLike(SFunction<T, ?> func, Object obj) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.LIKE, func, String.valueOf(obj) + "%");
      return this;
   }

   public <T> SpecificationWrapper rightLike(Boolean bool, SFunction<T, ?> func, Object obj) {
      return bool ? this.addQueryCondition(SpecificationWrapper.QueryOperator.LIKE, func, String.valueOf(obj) + "%") : this;
   }

   public <T> SpecificationWrapper notLike(SFunction<T, ?> func, Object obj) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.NOT_LIKE, func, "%" + String.valueOf(obj) + "%");
      return this;
   }

   public <T> SpecificationWrapper notLike(Boolean bool, SFunction<T, ?> func, Object obj) {
      return bool ? this.addQueryCondition(SpecificationWrapper.QueryOperator.NOT_LIKE, func, "%" + String.valueOf(obj) + "%") : this;
   }

   public <T> SpecificationWrapper in(SFunction<T, ?> func, Object obj) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.IN, func, obj);
      return this;
   }

   public <T> SpecificationWrapper in(Boolean bool, SFunction<T, ?> func, Object obj) {
      return bool ? this.addQueryCondition(SpecificationWrapper.QueryOperator.IN, func, obj) : this;
   }

   public <T> SpecificationWrapper notIn(SFunction<T, ?> func, Object obj) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.NOT_IN, func, obj);
      return this;
   }

   public <T> SpecificationWrapper notIn(Boolean bool, SFunction<T, ?> func, Object obj) {
      return bool ? this.addQueryCondition(SpecificationWrapper.QueryOperator.NOT_IN, func, obj) : this;
   }

   public <T> SpecificationWrapper isNull(SFunction<T, ?> func) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.IS_NULL, func, (Object)null);
      return this;
   }

   public <T> SpecificationWrapper isNotNull(SFunction<T, ?> func) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.IS_NOT_NULL, func, (Object)null);
      return this;
   }

   public <T> SpecificationWrapper between(SFunction<T, ?> func, Object min, Object max) {
      QueryCondition queryCondition = new QueryCondition(SpecificationWrapper.QueryOperator.BETWEEN, this.getKey(func), (Object)null);
      queryCondition.minObj = min;
      queryCondition.maxObj = max;
      this.specificationBuilder.getQueryConditions().add(queryCondition);
      return this;
   }

   public <T> SpecificationWrapper notBetween(SFunction<T, ?> func, Object min, Object max) {
      QueryCondition queryCondition = new QueryCondition(SpecificationWrapper.QueryOperator.NOT_BETWEEN, this.getKey(func), (Object)null);
      queryCondition.minObj = min;
      queryCondition.maxObj = max;
      this.specificationBuilder.getQueryConditions().add(queryCondition);
      return this;
   }

   public <T> SpecificationWrapper groupBy(SFunction<T, ?>... funcs) {
      this.specificationBuilder.getGroupByKeys().addAll((Collection)Arrays.stream(funcs).map(this::getKey).collect(Collectors.toList()));
      return this;
   }

   public <T> SpecificationWrapper orderBy(Sort.Direction direction, SFunction<T, ?>... funcs) {
      for(SFunction<T, ?> func : funcs) {
         this.addOrderCondition(func, direction);
      }

      return this;
   }

   public <T> SpecificationWrapper orderAsc(SFunction<T, ?> func) {
      this.orderBy(Direction.ASC, func);
      return this;
   }

   public <T> SpecificationWrapper orderDesc(SFunction<T, ?> func) {
      this.orderBy(Direction.DESC, func);
      return this;
   }

   public <T> SpecificationWrapper exists(SFunction<T, ?> func, Object obj) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.EXISTS, func, obj);
      return this;
   }

   public <T> SpecificationWrapper notExists(SFunction<T, ?> func, Object obj) {
      this.addQueryCondition(SpecificationWrapper.QueryOperator.NOT_EXISTS, func, obj);
      return this;
   }

   public <T> SpecificationWrapper or(SpecificationWrapper specificationWrapper) {
      List<SpecificationWrapper> orDefault = (List)this.specificationWrapperMap.getOrDefault(SpecificationWrapper.QueryOperator.OR, new LinkedList());
      orDefault.add(specificationWrapper);
      this.specificationWrapperMap.put(SpecificationWrapper.QueryOperator.OR, orDefault);
      return this;
   }

   private <T> SpecificationWrapper putJoinCondition(QueryOperator operator, SFunction<T, ?> func, SpecificationWrapper specificationWrapper) {
      specificationWrapper.joinTable = this.getKey(func);
      List<SpecificationWrapper> orDefault = (List)this.specificationWrapperMap.getOrDefault(SpecificationWrapper.QueryOperator.OR, new LinkedList());
      orDefault.add(specificationWrapper);
      this.specificationWrapperMap.put(operator, orDefault);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public <T> SpecificationWrapper join(SFunction<T, ?> func, SpecificationWrapper specificationWrapper) {
      return this.putJoinCondition(SpecificationWrapper.QueryOperator.JOIN, func, specificationWrapper);
   }

   /** @deprecated */
   @Deprecated
   public <T> SpecificationWrapper leftJoin(SFunction<T, ?> func, SpecificationWrapper specificationWrapper) {
      return this.putJoinCondition(SpecificationWrapper.QueryOperator.LEFT_JOIN, func, specificationWrapper);
   }

   /** @deprecated */
   @Deprecated
   public <T> SpecificationWrapper rightJoin(SFunction<T, ?> func, SpecificationWrapper specificationWrapper) {
      return this.putJoinCondition(SpecificationWrapper.QueryOperator.RIGHT_JOIN, func, specificationWrapper);
   }

   public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
      this.buildQueryPredicate(root, query, cb);
      this.buildGroupByPredicate(root, query, cb);
      this.buildOrderByPredicate(root, query, cb);
      return query.getRestriction();
   }

   private List<Predicate> buildBaseQueryPredicate(From root, CriteriaQuery query, CriteriaBuilder cb) {
      return (List)this.specificationBuilder.getQueryConditions().stream().map((queryCondition) -> {
         switch (queryCondition.operator.ordinal()) {
            case 0:
               return cb.equal(root.get(queryCondition.key), queryCondition.value);
            case 1:
               return cb.notEqual(root.get(queryCondition.key), queryCondition.value);
            case 2:
               return cb.gt(root.get(queryCondition.key).as(Number.class), (Number)queryCondition.value);
            case 3:
               return cb.ge(root.get(queryCondition.key).as(Number.class), (Number)queryCondition.value);
            case 4:
               return cb.lt(root.get(queryCondition.key).as(Number.class), (Number)queryCondition.value);
            case 5:
               return cb.le(root.get(queryCondition.key).as(Number.class), (Number)queryCondition.value);
            case 6:
               return cb.like(root.get(queryCondition.key).as(String.class), (String)queryCondition.value);
            case 7:
               return cb.notLike(root.get(queryCondition.key).as(String.class), (String)queryCondition.value);
            case 8:
               return root.get(queryCondition.key).in(new Object[]{queryCondition.value});
            case 9:
               return cb.not(root.get(queryCondition.key).in(new Object[]{queryCondition.value}));
            case 10:
               return cb.isNull(root.get(queryCondition.key));
            case 11:
               return cb.isNotNull(root.get(queryCondition.key));
            case 12:
               return cb.between(root.get(queryCondition.key), (Comparable)queryCondition.minObj, (Comparable)queryCondition.maxObj);
            case 13:
               return cb.between(root.get(queryCondition.key), (Comparable)queryCondition.minObj, (Comparable)queryCondition.maxObj).not();
            case 14:
            case 15:
            case 16:
            case 17:
            default:
               return null;
            case 18:
               return null;
            case 19:
               return null;
         }
      }).filter(Objects::nonNull).collect(Collectors.toList());
   }

   private void buildQueryPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
      List<Predicate> queryPredicates = this.buildBaseQueryPredicate(root, query, cb);
      List<Predicate> predicates = new ArrayList();
      if (!CollectionUtils.isEmpty(queryPredicates)) {
         predicates.add(cb.and((Predicate[])queryPredicates.toArray(new Predicate[queryPredicates.size()])));
      }

      for(Map.Entry<QueryOperator, List<SpecificationWrapper>> entry : this.specificationWrapperMap.entrySet()) {
         switch (((QueryOperator)entry.getKey()).ordinal()) {
            case 14:
               ((List)Optional.ofNullable((List)this.specificationWrapperMap.get(SpecificationWrapper.QueryOperator.JOIN)).orElse(new LinkedList())).stream().forEach((sw) -> {
                  Join join = root.join(sw.joinTable, JoinType.INNER);
                  List<Predicate> joinPredicates = sw.buildBaseQueryPredicate(join, query, cb);
                  join.on((Predicate[])joinPredicates.toArray(new Predicate[joinPredicates.size()]));
               });
               break;
            case 15:
               ((List)Optional.ofNullable((List)this.specificationWrapperMap.get(SpecificationWrapper.QueryOperator.JOIN)).orElse(new LinkedList())).stream().forEach((sw) -> {
                  Join join = root.join(sw.joinTable, JoinType.LEFT);
                  List<Predicate> joinPredicates = sw.buildBaseQueryPredicate(join, query, cb);
                  join.on((Predicate[])joinPredicates.toArray(new Predicate[joinPredicates.size()]));
               });
               break;
            case 16:
               ((List)Optional.ofNullable((List)this.specificationWrapperMap.get(SpecificationWrapper.QueryOperator.JOIN)).orElse(new LinkedList())).stream().forEach((sw) -> {
                  Join join = root.join(sw.joinTable, JoinType.RIGHT);
                  List<Predicate> joinPredicates = sw.buildBaseQueryPredicate(join, query, cb);
                  join.on((Predicate[])joinPredicates.toArray(new Predicate[joinPredicates.size()]));
               });
            case 17:
            case 18:
            case 19:
            default:
               break;
            case 20:
               List<Predicate> orPredicates = (List)((List)Optional.ofNullable((List)this.specificationWrapperMap.get(SpecificationWrapper.QueryOperator.OR)).orElse(new LinkedList())).stream().map((sw) -> cb.and((Predicate[])sw.buildBaseQueryPredicate(root, query, cb).toArray(new Predicate[0]))).collect(Collectors.toList());
               if (!CollectionUtils.isEmpty(orPredicates)) {
                  predicates.add(cb.or((Predicate[])orPredicates.toArray(new Predicate[orPredicates.size()])));
               }
         }
      }

      if (!CollectionUtils.isEmpty(predicates)) {
         query.where((Predicate[])predicates.toArray(new Predicate[predicates.size()]));
      }

   }

   private void buildGroupByPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
      if (!CollectionUtils.isEmpty(this.specificationBuilder.getGroupByKeys())) {
         Stream var10001 = this.specificationBuilder.getGroupByKeys().stream();
         Objects.requireNonNull(root);
         query.groupBy((List)var10001.map(root::get).collect(Collectors.toList()));
      }
   }

   private void buildOrderByPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
      if (!CollectionUtils.isEmpty(this.specificationBuilder.getOrderConditions())) {
         query.orderBy((List)this.specificationBuilder.getOrderConditions().stream().map((queryCondition) -> Direction.ASC == queryCondition.direction ? cb.asc(root.get(queryCondition.key)) : cb.desc(root.get(queryCondition.key))).collect(Collectors.toList()));
      }
   }

   private static enum QueryOperator {
      EQ,
      NE,
      GT,
      GE,
      LT,
      LE,
      LIKE,
      NOT_LIKE,
      IN,
      NOT_IN,
      IS_NULL,
      IS_NOT_NULL,
      BETWEEN,
      NOT_BETWEEN,
      JOIN,
      LEFT_JOIN,
      RIGHT_JOIN,
      GROUP_BY,
      EXISTS,
      NOT_EXISTS,
      OR;

      private QueryOperator() {
      }

      // $FF: synthetic method
      private static QueryOperator[] $values() {
         return new QueryOperator[]{EQ, NE, GT, GE, LT, LE, LIKE, NOT_LIKE, IN, NOT_IN, IS_NULL, IS_NOT_NULL, BETWEEN, NOT_BETWEEN, JOIN, LEFT_JOIN, RIGHT_JOIN, GROUP_BY, EXISTS, NOT_EXISTS, OR};
      }
   }

   private class QueryCondition {
      QueryOperator operator;
      String key;
      Object value;
      Object minObj;
      Object maxObj;
      Sort.Direction direction;

      public QueryCondition(QueryOperator operator, String key, Object value) {
         Objects.requireNonNull(SpecificationWrapper.this);
         super();
         this.operator = operator;
         this.key = key;
         this.value = value;
      }
   }

   private class SpecificationBuilder {
      private LinkedList<QueryCondition> queryConditions;
      private LinkedList<QueryCondition> orderConditions;
      private LinkedList<String> groupByKeys;

      public SpecificationBuilder() {
         Objects.requireNonNull(SpecificationWrapper.this);
         super();
      }

      public LinkedList<QueryCondition> getQueryConditions() {
         if (CollectionUtils.isEmpty(this.queryConditions)) {
            this.queryConditions = new LinkedList();
         }

         return this.queryConditions;
      }

      public LinkedList<QueryCondition> getOrderConditions() {
         if (CollectionUtils.isEmpty(this.orderConditions)) {
            this.orderConditions = new LinkedList();
         }

         return this.orderConditions;
      }

      public LinkedList<String> getGroupByKeys() {
         if (CollectionUtils.isEmpty(this.groupByKeys)) {
            this.groupByKeys = new LinkedList();
         }

         return this.groupByKeys;
      }
   }
}
