package com.kuma.boot.data.jpa.fenix.specification;

import com.kuma.boot.data.jpa.fenix.config.FenixConfig;
import com.kuma.boot.data.jpa.fenix.exception.BuildSpecificationException;
import com.kuma.boot.data.jpa.fenix.helper.CollectionHelper;
import com.kuma.boot.data.jpa.fenix.helper.FieldHelper;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import com.kuma.boot.data.jpa.fenix.specification.handler.AbstractPredicateHandler;
import com.kuma.boot.data.jpa.fenix.specification.handler.bean.Pair;
import com.kuma.boot.data.jpa.fenix.specification.predicate.FenixPredicate;
import com.kuma.boot.data.jpa.fenix.specification.predicate.FenixPredicateBuilder;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Predicate.BooleanOperator;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;

public final class FenixSpecification {
   private static final Map<Class<?>, AbstractPredicateHandler> specificationHandlerMap = FenixConfig.getSpecificationHandlerMap();

   public FenixSpecification() {
   }

   public static <T> Specification<T> ofBean(Object beanParam) {
      return (root, query, builder) -> mergePredicates(builder, (Map)beanParamToPredicate(root, builder, beanParam).stream().collect(Collectors.groupingBy(Predicate::getOperator)));
   }

   public static <T> Specification<T> of(List<Predicate> predicates) {
      return (root, query, builder) -> mergePredicates(builder, (Map)predicates.stream().collect(Collectors.groupingBy(Predicate::getOperator)));
   }

   public static <T> Specification<T> of(FenixPredicate fenixPredicate) {
      return (root, query, builder) -> mergePredicates(builder, (Map)fenixPredicate.toPredicate(new FenixPredicateBuilder(root, query, builder)).stream().collect(Collectors.groupingBy(Predicate::getOperator)));
   }

   private static Predicate mergePredicates(CriteriaBuilder builder, Map<Predicate.BooleanOperator, List<Predicate>> predicatesMap) {
      List<Predicate> andPredicates = (List)predicatesMap.get(BooleanOperator.AND);
      List<Predicate> orPredicates = (List)predicatesMap.get(BooleanOperator.OR);
      if (CollectionHelper.isNotEmpty((Collection)andPredicates) && CollectionHelper.isNotEmpty((Collection)orPredicates)) {
         return builder.or(builder.and((Predicate[])andPredicates.toArray(new Predicate[0])), builder.or((Predicate[])orPredicates.toArray(new Predicate[0])));
      } else if (CollectionHelper.isNotEmpty((Collection)orPredicates)) {
         return builder.or((Predicate[])orPredicates.toArray(new Predicate[0]));
      } else {
         return CollectionHelper.isNotEmpty((Collection)andPredicates) ? builder.and((Predicate[])andPredicates.toArray(new Predicate[0])) : null;
      }
   }

   public static <Z, X> List<Predicate> beanParamToPredicate(From<Z, X> from, CriteriaBuilder criteriaBuilder, Object beanParam) {
      Field[] fields = FieldHelper.getAllFields(beanParam.getClass());
      List<Predicate> predicates = new ArrayList(fields.length);

      for(Field field : fields) {
         Annotation[] annotations = field.getAnnotations();
         if (annotations != null) {
            for(Annotation annotation : annotations) {
               AbstractPredicateHandler handler = (AbstractPredicateHandler)specificationHandlerMap.get(annotation.annotationType());
               if (handler != null) {
                  Predicate predicate = buildPredicate(beanParam, field, criteriaBuilder, from, handler);
                  if (predicate != null) {
                     predicates.add(predicate);
                  }
               }
            }
         }
      }

      return predicates;
   }

   private static <Z, X> Predicate buildPredicate(Object beanParam, Field field, CriteriaBuilder criteriaBuilder, From<Z, X> root, AbstractPredicateHandler handler) {
      Class<? extends Annotation> annotationClass = handler.getAnnotation();
      Annotation annotation = field.getAnnotation(annotationClass);
      if (annotation == null) {
         return null;
      } else {
         String propertyName = field.getName();

         boolean match;
         try {
            Method propertyMethod = beanParam.getClass().getMethod(propertyName);
            propertyMethod.setAccessible(true);
            match = (Boolean)propertyMethod.invoke(beanParam);
         } catch (NoSuchMethodException var11) {
            Pair<String, Object> pair = getFieldNameAndValue(field, beanParam, annotation);
            return pair == null ? null : buildDefaultPredicate(criteriaBuilder, field, root, handler, pair.getLeft(), pair.getRight(), annotation);
         } catch (IllegalAccessException var12) {
            throw new BuildSpecificationException("\u3010Fenix \u5f02\u5e38\u3011\u4e0e\u5c5e\u6027\u540d\u76f8\u540c\u540d\u79f0\u7684 match \u5339\u914d\u65b9\u6cd5\uff0c\u4e0d\u80fd\u8bbf\u95ee\uff0c\u8bf7\u8bbe\u7f6e\u65b9\u6cd5\u7684\u8bbf\u95ee\u7ea7\u522b\u4e3a\u3010public\u3011\uff0c\u65b9\u6cd5\u8fd4\u56de\u503c\u7c7b\u578b\u4e3a\u3010boolean\u3011\u7c7b\u578b.");
         } catch (InvocationTargetException var13) {
            throw new BuildSpecificationException("\u3010Fenix \u5f02\u5e38\u3011\u4e0e\u5c5e\u6027\u540d\u76f8\u540c\u540d\u79f0\u7684 match \u5339\u914d\u65b9\u6cd5\uff0c\u8c03\u7528\u51fa\u9519\uff0c\u8bf7\u8bbe\u7f6e\u65b9\u6cd5\u7684\u8bbf\u95ee\u7ea7\u522b\u4e3a\u3010public\u3011\uff0c\u65b9\u6cd5\u8fd4\u56de\u503c\u7c7b\u578b\u4e3a\u3010boolean\u3011\u7c7b\u578b\uff0c\u5e76\u68c0\u67e5\u5176\u4ed6\u5f15\u8d77\u8c03\u7528\u5931\u8d25\u7684\u539f\u56e0.");
         }

         if (!match) {
            return null;
         } else {
            Pair<String, Object> pair = getFieldNameAndValue(field, beanParam, annotation);
            return pair == null ? null : handler.buildPredicate(criteriaBuilder, root, pair.getLeft(), pair.getRight(), annotation);
         }
      }
   }

   private static Pair<String, Object> getFieldNameAndValue(Field field, Object beanParam, Annotation annotation) {
      PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(beanParam.getClass(), field.getName());
      if (descriptor == null) {
         return null;
      } else {
         try {
            String fieldName = (String)annotation.getClass().getMethod("value").invoke(annotation);
            fieldName = StringHelper.isBlank(fieldName) ? field.getName() : fieldName;
            return Pair.<String, Object>of(fieldName, descriptor.getReadMethod().invoke(beanParam));
         } catch (ReflectiveOperationException e) {
            throw new BuildSpecificationException("\u3010Fenix \u5f02\u5e38\u3011\u6784\u5efa\u3010" + annotation.getClass().getName() + "\u3011\u6ce8\u89e3\u7684\u6761\u4ef6\u65f6\uff0c\u53cd\u5c04\u8c03\u7528\u83b7\u53d6\u5bf9\u5e94\u7684\u5c5e\u6027\u5b57\u6bb5\u503c\u5f02\u5e38\u3002", e);
         }
      }
   }

   private static <Z, X> Predicate buildDefaultPredicate(CriteriaBuilder criteriaBuilder, Field field, From<Z, X> root, AbstractPredicateHandler handler, String fieldName, Object value, Annotation annotation) {
      if (value == null) {
         return null;
      } else if (field.getType() == String.class) {
         return StringHelper.isNotBlank(value.toString()) ? handler.buildPredicate(criteriaBuilder, root, fieldName, value, annotation) : null;
      } else {
         return handler.buildPredicate(criteriaBuilder, root, fieldName, value, annotation);
      }
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "lambda$of$2c5cf3ab$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/springframework/data/jpa/domain/Specification") && lambda.getFunctionalInterfaceMethodName().equals("toPredicate") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;") && lambda.getImplClass().equals("com/kuma/boot/data/jpa/fenix/specification/FenixSpecification") && lambda.getImplMethodSignature().equals("(Lcom/kuma/boot/data/jpa/fenix/specification/predicate/FenixPredicate;Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;")) {
               return (Specification)(root, query, builder) -> mergePredicates(builder, (Map)fenixPredicate.toPredicate(new FenixPredicateBuilder(root, query, builder)).stream().collect(Collectors.groupingBy(Predicate::getOperator)));
            }
            break;
         case "lambda$ofBean$14a20b7f$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/springframework/data/jpa/domain/Specification") && lambda.getFunctionalInterfaceMethodName().equals("toPredicate") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;") && lambda.getImplClass().equals("com/kuma/boot/data/jpa/fenix/specification/FenixSpecification") && lambda.getImplMethodSignature().equals("(Ljava/lang/Object;Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;")) {
               return (Specification)(root, query, builder) -> mergePredicates(builder, (Map)beanParamToPredicate(root, builder, beanParam).stream().collect(Collectors.groupingBy(Predicate::getOperator)));
            }
            break;
         case "lambda$of$62be8ffc$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("org/springframework/data/jpa/domain/Specification") && lambda.getFunctionalInterfaceMethodName().equals("toPredicate") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;") && lambda.getImplClass().equals("com/kuma/boot/data/jpa/fenix/specification/FenixSpecification") && lambda.getImplMethodSignature().equals("(Ljava/util/List;Ljakarta/persistence/criteria/Root;Ljakarta/persistence/criteria/CriteriaQuery;Ljakarta/persistence/criteria/CriteriaBuilder;)Ljakarta/persistence/criteria/Predicate;")) {
               return (Specification)(root, query, builder) -> mergePredicates(builder, (Map)predicates.stream().collect(Collectors.groupingBy(Predicate::getOperator)));
            }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }
}
