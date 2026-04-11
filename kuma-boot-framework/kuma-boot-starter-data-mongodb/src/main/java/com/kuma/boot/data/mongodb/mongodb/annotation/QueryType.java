package com.kuma.boot.data.mongodb.mongodb.annotation;

import java.lang.reflect.Field;
import java.util.List;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;

public enum QueryType {
   EQUALS {
      public Criteria buildCriteria(QueryField queryFieldAnnotation, Field field, Object value) {
         if (QueryType.check(queryFieldAnnotation, field, value)) {
            String queryField = QueryType.getQueryFieldName(queryFieldAnnotation, field);
            return Criteria.where(queryField).is(value.toString());
         } else {
            return new Criteria();
         }
      }
   },
   LIKE {
      public Criteria buildCriteria(QueryField queryFieldAnnotation, Field field, Object value) {
         if (QueryType.check(queryFieldAnnotation, field, value)) {
            String queryField = QueryType.getQueryFieldName(queryFieldAnnotation, field);
            return Criteria.where(queryField).regex(value.toString());
         } else {
            return new Criteria();
         }
      }
   },
   IN {
      public Criteria buildCriteria(QueryField queryFieldAnnotation, Field field, Object value) {
         if (QueryType.check(queryFieldAnnotation, field, value) && value instanceof List) {
            String queryField = QueryType.getQueryFieldName(queryFieldAnnotation, field);
            return Criteria.where(queryField).in((List)value);
         } else {
            return new Criteria();
         }
      }
   };

   private QueryType() {
   }

   private static boolean check(QueryField queryField, Field field, Object value) {
      return queryField != null && field != null && value != null;
   }

   public abstract Criteria buildCriteria(QueryField queryFieldAnnotation, Field field, Object value);

   private static String getQueryFieldName(QueryField queryField, Field field) {
      String queryFieldValue = queryField.attribute();
      if (!StringUtils.hasText(queryFieldValue)) {
         queryFieldValue = field.getName();
      }

      return queryFieldValue;
   }

   // $FF: synthetic method
   private static QueryType[] $values() {
      return new QueryType[]{EQUALS, LIKE, IN};
   }
}
