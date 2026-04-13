package com.kuma.boot.data.jpa.builder;

import com.kuma.boot.common.constant.SymbolConstants;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.utils.bean.BeanUtils;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.common.StringUtils;
import com.kuma.boot.common.utils.convert.ConvertUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.NotNull;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class JpaCriteriaBuilder<T> {
   private Integer pageNumber;
   private Integer pageSize;
   private final List<Sort.Order> rawSortList = new ArrayList();
   private Specification<T> specification = Specification.unrestricted();
   public static final Integer FIFTEEN = 15;

   private JpaCriteriaBuilder() {
   }

   public static <T> JpaCriteriaBuilder<T> Builder() {
      return new JpaCriteriaBuilder<T>();
   }

   public <V> JpaCriteriaBuilder<T> autoBuilder(JpaCriteriaBuilder builder, @NotNull V entity) {
      if (Objects.isNull(entity)) {
         throw new BusinessException("\u5b9e\u4f53\u4e0d\u80fd\u4e3a\u7a7a");
      } else {
         String pageSize = "pageSize";
         String pageIndex = "pageIndex";
         Field[] fields = entity.getClass().getDeclaredFields();

         for(Field field : fields) {
            field.setAccessible(Boolean.TRUE);

            try {
               Object value = field.get(entity);
               if (StringUtils.isNullAndSpaceOrEmpty(value)) {
                  if (pageIndex.equals(field.getName())) {
                     builder = builder.page(ConvertUtils.convertInt(value));
                  } else if (pageSize.equals(field.getName())) {
                     builder = builder.size(ConvertUtils.convertInt(value));
                  } else {
                     builder = builder.equal(field.getName(), value);
                  }
               }
            } catch (IllegalAccessException e) {
               LogUtils.error(e);
            }
         }

         return builder;
      }
   }

   public <V> JpaCriteriaBuilder<T> autoBuilder(JpaCriteriaBuilder builder, @NotNull V entity, List<String> ignoreFields) {
      List[] groupFields = new List[0];
      return this.autoBuilder(builder, entity, ignoreFields, groupFields);
   }

   public <V> JpaCriteriaBuilder<T> autoBuilder(JpaCriteriaBuilder builder, @NotNull V entity, List<String>... groupFields) {
      return this.autoBuilder(builder, entity, CollectionUtils.toList(new String[0]), groupFields);
   }

   public <V> JpaCriteriaBuilder<T> autoBuilder(JpaCriteriaBuilder<T> builder, @NotNull V entity, List<String> ignoreFields, List<String>... groupFields) {
      if (Objects.isNull(entity)) {
         throw new BusinessException("\u5b9e\u4f53\u4e0d\u80fd\u4e3a\u7a7a");
      } else {
         if (ignoreFields == null) {
            ignoreFields = CollectionUtils.toList(new String[0]);
         }

         if (groupFields == null) {
            groupFields = new List[ConvertUtils.ZERO];
         }

         if (!CollectionUtils.isEmpty(groupFields) && !CollectionUtils.isEmpty(groupFields)) {
            String pageSize = "pageSize";
            String pageIndex = "pageIndex";
            Integer size = null;
            Integer index = null;
            Field[] fields = entity.getClass().getDeclaredFields();
            List<JpaCriteriaBuilder<T>> groupFieldsList = new ArrayList();

            for(int i = ConvertUtils.ZERO; i < groupFields.length; ++i) {
               List<String> groupField = groupFields[i];
               Stream var10000 = Arrays.stream(fields);
               Objects.requireNonNull(groupField);
               List<Field> fieldList = (List)var10000.filter(groupField::equals).collect(Collectors.toList());
               if (!CollectionUtils.isEmpty(fieldList)) {
                  JpaCriteriaBuilder criteria = (JpaCriteriaBuilder)BeanUtils.copyProperties(builder, JpaCriteriaBuilder.class);

                  for(Field field : fieldList) {
                     field.setAccessible(Boolean.TRUE);
                     String fieldName = field.getName();
                     if (!ignoreFields.contains(fieldName)) {
                        try {
                           Object value = field.get(entity);
                           if (StringUtils.isNullAndSpaceOrEmpty(value)) {
                              if (pageIndex.equals(field.getName())) {
                                 index = ConvertUtils.convertInt(value);
                              } else if (pageSize.equals(field.getName())) {
                                 size = ConvertUtils.convertInt(value);
                              } else {
                                 criteria = criteria.equal(field.getName(), value);
                              }
                           }
                        } catch (IllegalAccessException e) {
                           LogUtils.error(e);
                        }
                     }
                  }

                  groupFieldsList.add(criteria);
               }
            }

            JpaCriteriaBuilder<T> result = builder.or((JpaCriteriaBuilder[])groupFieldsList.toArray(new JpaCriteriaBuilder[ConvertUtils.ZERO]));
            result.page(index).size(size);
            return result;
         } else {
            return this.autoBuilder(builder, entity);
         }
      }
   }

   public <V> JpaCriteriaBuilder<T> equal(@NotNull String field, @NotNull V value) {
      if (this.isValidValue(value)) {
         this.specification = this.specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(field), value));
      }

      return this;
   }

   public <V> JpaCriteriaBuilder<T> like(@NotNull String field, @NotNull V value) {
      if (this.isValidValue(value)) {
         this.specification = this.specification.and((root, query, criteriaBuilder) -> {
            String var10000 = SymbolConstants.PERCENT;
            String pattern = var10000 + String.valueOf(value) + SymbolConstants.PERCENT;
            return criteriaBuilder.like(root.get(field), pattern);
         });
      }

      return this;
   }

   public <V> JpaCriteriaBuilder<T> in(@NotNull String field, @NotNull List<V> values) {
      if (CollectionUtils.isNotEmpty(values)) {
         this.specification = this.specification.and((root, query, criteriaBuilder) -> root.get(field).in(values));
      }

      return this;
   }

   public <V extends Comparable<? super V>> JpaCriteriaBuilder<T> greaterThan(@NotNull String field, @NotNull V value) {
      if (this.isValidValue(value)) {
         this.specification = this.specification.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(field), value));
      }

      return this;
   }

   public <V extends Comparable<? super V>> JpaCriteriaBuilder<T> greaterThanOrEqualTo(@NotNull String field, @NotNull V value) {
      if (this.isValidValue(value)) {
         this.specification = this.specification.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(field), value));
      }

      return this;
   }

   public <V extends Comparable<? super V>> JpaCriteriaBuilder<T> lessThan(@NotNull String field, @NotNull V value) {
      if (this.isValidValue(value)) {
         this.specification = this.specification.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(field), value));
      }

      return this;
   }

   public <V extends Comparable<? super V>> JpaCriteriaBuilder<T> between(@NotNull String field, @NotNull V from, V to) {
      if (from != null && to != null) {
         this.specification = this.specification.and((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(field), from, to));
      }

      return this;
   }

   public JpaCriteriaBuilder<T> isNull(String field) {
      this.specification = this.specification.and((root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(field)));
      return this;
   }

   public JpaCriteriaBuilder<T> isNotNull(String field) {
      this.specification = this.specification.and((root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get(field)));
      return this;
   }

   public <X> JpaCriteriaBuilder<T> joinEqual(String joinField, @NotNull String field, @NotNull Object value, JoinType joinType) {
      if (this.isValidValue(value)) {
         this.specification = this.specification.and((root, query, criteriaBuilder) -> {
            Join<T, X> join = root.join(joinField, joinType);
            return criteriaBuilder.equal(join.get(field), value);
         });
      }

      return this;
   }

   public JpaCriteriaBuilder<T> add(Function<Root<T>, Predicate> condition) {
      this.specification = this.specification.and((root, query, criteriaBuilder) -> {
         Predicate predicate = (Predicate)condition.apply(root);
         return criteriaBuilder.and(new Predicate[]{predicate});
      });
      return this;
   }

   public JpaCriteriaBuilder<T> or(JpaCriteriaBuilder<T>... otherBuilder) {
      Specification<T> combinedSpecification = Specification.where(this.specification);

      for(JpaCriteriaBuilder<T> builder : otherBuilder) {
         combinedSpecification = combinedSpecification.or(builder.build());
      }

      this.specification = combinedSpecification;
      return this;
   }

   public JpaCriteriaBuilder<T> page(int page) {
      this.pageNumber = page;
      return this;
   }

   public JpaCriteriaBuilder<T> size(int size) {
      this.pageSize = size;
      return this;
   }

   public JpaCriteriaBuilder<T> orderBy(@NotNull String field, Sort.@NotNull Direction direction) {
      if (direction != null) {
         this.rawSortList.add(org.springframework.data.domain.Sort.Order.by(field).with(direction));
      }

      return this;
   }

   public Pageable toPageable() {
      if (CollectionUtils.isEmpty(this.rawSortList)) {
         throw new BusinessException("\u5206\u9875\u67e5\u8be2\u5fc5\u987b\u6307\u5b9a\u6392\u5e8f\u6761\u4ef6");
      } else {
         return PageRequest.of((Integer)Objects.requireNonNullElse(this.pageNumber, ConvertUtils.ZERO), (Integer)Objects.requireNonNullElse(this.pageSize, FIFTEEN), Sort.by(this.rawSortList));
      }
   }

   public Specification<T> build() {
      return (root, query, cb) -> {
         if (!this.rawSortList.isEmpty()) {
            List<Order> orders = (List)this.rawSortList.stream().map((sortOrder) -> {
               Path<Object> path = root.get(sortOrder.getProperty());
               return sortOrder.getDirection().isAscending() ? cb.asc(path) : cb.desc(path);
            }).collect(Collectors.toList());
            query.orderBy(orders);
         }

         return this.specification.toPredicate(root, query, cb);
      };
   }

   private <V> boolean isValidValue(V value) {
      if (!(value instanceof String)) {
         return Objects.nonNull(value);
      } else {
         return Objects.nonNull(value) && !((String)value).trim().isEmpty();
      }
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "lambda$joinEqual$aa45917f$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/springframework/data/jpa/domain/Specification") && lambda.getFunctionalInterfaceMethodName().equals("toPredicate") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;") && lambda.getImplClass().equals("com/kuma/boot/data/jpa/builder/JpaCriteriaBuilder") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;Ljakarta/persistence/criteria/JoinType;Ljava/lang/String;Ljava/lang/Object;Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;")) {
               return (Specification)(root, query, criteriaBuilder) -> {
                  Join<T, X> join = root.join(joinField, joinType);
                  return criteriaBuilder.equal(join.get(field), value);
               };
            }
            break;
         case "lambda$between$8413e598$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/springframework/data/jpa/domain/Specification") && lambda.getFunctionalInterfaceMethodName().equals("toPredicate") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;") && lambda.getImplClass().equals("com/kuma/boot/data/jpa/builder/JpaCriteriaBuilder") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;Ljava/lang/Comparable;Ljava/lang/Comparable;Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;")) {
               return (Specification)(root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(field), from, to);
            }
            break;
         case "lambda$equal$cb47b6bb$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/springframework/data/jpa/domain/Specification") && lambda.getFunctionalInterfaceMethodName().equals("toPredicate") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;") && lambda.getImplClass().equals("com/kuma/boot/data/jpa/builder/JpaCriteriaBuilder") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;Ljava/lang/Object;Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;")) {
               return (Specification)(root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(field), value);
            }
            break;
         case "lambda$in$ca5b8040$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/springframework/data/jpa/domain/Specification") && lambda.getFunctionalInterfaceMethodName().equals("toPredicate") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;") && lambda.getImplClass().equals("com/kuma/boot/data/jpa/builder/JpaCriteriaBuilder") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;Ljava/util/List;Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;")) {
               return (Specification)(root, query, criteriaBuilder) -> root.get(field).in(values);
            }
            break;
         case "lambda$like$2766c937$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/springframework/data/jpa/domain/Specification") && lambda.getFunctionalInterfaceMethodName().equals("toPredicate") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;") && lambda.getImplClass().equals("com/kuma/boot/data/jpa/builder/JpaCriteriaBuilder") && lambda.getImplMethodSignature().equals("(Ljava/lang/Object;Ljava/lang/String;Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;")) {
               return (Specification)(root, query, criteriaBuilder) -> {
                  String var10000 = SymbolConstants.PERCENT;
                  String pattern = var10000 + String.valueOf(value) + SymbolConstants.PERCENT;
                  return criteriaBuilder.like(root.get(field), pattern);
               };
            }
            break;
         case "lambda$greaterThanOrEqualTo$ddd35ee4$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/springframework/data/jpa/domain/Specification") && lambda.getFunctionalInterfaceMethodName().equals("toPredicate") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;") && lambda.getImplClass().equals("com/kuma/boot/data/jpa/builder/JpaCriteriaBuilder") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;Ljava/lang/Comparable;Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;")) {
               return (Specification)(root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(field), value);
            }
            break;
         case "lambda$isNotNull$11739410$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/springframework/data/jpa/domain/Specification") && lambda.getFunctionalInterfaceMethodName().equals("toPredicate") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;") && lambda.getImplClass().equals("com/kuma/boot/data/jpa/builder/JpaCriteriaBuilder") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;")) {
               return (Specification)(root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get(field));
            }
            break;
         case "lambda$add$fe293daf$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/springframework/data/jpa/domain/Specification") && lambda.getFunctionalInterfaceMethodName().equals("toPredicate") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;") && lambda.getImplClass().equals("com/kuma/boot/data/jpa/builder/JpaCriteriaBuilder") && lambda.getImplMethodSignature().equals("(Ljava/util/function/Function;Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;")) {
               return (Specification)(root, query, criteriaBuilder) -> {
                  Predicate predicate = (Predicate)condition.apply(root);
                  return criteriaBuilder.and(new Predicate[]{predicate});
               };
            }
            break;
         case "lambda$build$bd7414b8$1":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("org/springframework/data/jpa/domain/Specification") && lambda.getFunctionalInterfaceMethodName().equals("toPredicate") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;") && lambda.getImplClass().equals("com/kuma/boot/data/jpa/builder/JpaCriteriaBuilder") && lambda.getImplMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;")) {
               return (Specification)(root, query, cb) -> {
                  if (!this.rawSortList.isEmpty()) {
                     List<Order> orders = (List)this.rawSortList.stream().map((sortOrder) -> {
                        Path<Object> path = root.get(sortOrder.getProperty());
                        return sortOrder.getDirection().isAscending() ? cb.asc(path) : cb.desc(path);
                     }).collect(Collectors.toList());
                     query.orderBy(orders);
                  }

                  return this.specification.toPredicate(root, query, cb);
               };
            }
            break;
         case "lambda$lessThan$ddd35ee4$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/springframework/data/jpa/domain/Specification") && lambda.getFunctionalInterfaceMethodName().equals("toPredicate") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;") && lambda.getImplClass().equals("com/kuma/boot/data/jpa/builder/JpaCriteriaBuilder") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;Ljava/lang/Comparable;Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;")) {
               return (Specification)(root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(field), value);
            }
            break;
         case "lambda$greaterThan$ddd35ee4$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/springframework/data/jpa/domain/Specification") && lambda.getFunctionalInterfaceMethodName().equals("toPredicate") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;") && lambda.getImplClass().equals("com/kuma/boot/data/jpa/builder/JpaCriteriaBuilder") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;Ljava/lang/Comparable;Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;")) {
               return (Specification)(root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(field), value);
            }
            break;
         case "lambda$isNull$11739410$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/springframework/data/jpa/domain/Specification") && lambda.getFunctionalInterfaceMethodName().equals("toPredicate") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;") && lambda.getImplClass().equals("com/kuma/boot/data/jpa/builder/JpaCriteriaBuilder") && lambda.getImplMethodSignature().equals("(Ljava/lang/String;Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;")) {
               return (Specification)(root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(field));
            }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }
}
