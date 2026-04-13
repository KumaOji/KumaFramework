package com.kuma.boot.data.jpa.extend;

import jakarta.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.query.JpaParameters;

public abstract class QueryResolveResult {
   protected String afterParseSQL;

   public QueryResolveResult() {
   }

   public abstract void setQueryParams(Query query);

   public String getAfterParseSQL() {
      return this.afterParseSQL;
   }

   public void setAfterParseSQL(String afterParseSQL) {
      this.afterParseSQL = afterParseSQL;
   }

   public static class NameExpressionQueryResolveResult extends QueryResolveResult {
      private List<String> removeParamNames;
      private Map<String, Object> allQueryParams;

      public NameExpressionQueryResolveResult(String afterParseSQL, List<String> removeParamNames, Map<String, Object> allQueryParams) {
         this.afterParseSQL = afterParseSQL;
         this.removeParamNames = removeParamNames;
         this.allQueryParams = allQueryParams;
      }

      public void setQueryParams(Query query) {
         this.removeParamNames.stream().forEach((item) -> this.allQueryParams.remove(item));
         Map var10000 = this.allQueryParams;
         Objects.requireNonNull(query);
         var10000.forEach(query::setParameter);
      }

      public List<String> getRemoveParamNames() {
         return this.removeParamNames;
      }

      public void setRemoveParamNames(List<String> removeParamNames) {
         this.removeParamNames = removeParamNames;
      }

      public Map<String, Object> getAllQueryParams() {
         return this.allQueryParams;
      }

      public void setAllQueryParams(Map<String, Object> allQueryParams) {
         this.allQueryParams = allQueryParams;
      }
   }

   public static class PositionExpressionQueryResolveResult extends QueryResolveResult {
      private List<Integer> removeParamIndex;
      private Map<Integer, Object> allQueryParams;

      public PositionExpressionQueryResolveResult(String afterParseSQL, List<Integer> removeParamIndex, Map<Integer, Object> allQueryParams) {
         this.afterParseSQL = afterParseSQL;
         this.removeParamIndex = removeParamIndex;
         this.allQueryParams = allQueryParams;
      }

      public void setQueryParams(Query query) {
         this.removeParamIndex.stream().forEach((item) -> this.allQueryParams.remove(item));
         Map<Integer, Object> newQueryParams = new HashMap();
         this.allQueryParams.forEach((k, v) -> {
            int count = ((List)this.removeParamIndex.stream().filter((item) -> k > item).collect(Collectors.toList())).size();
            Integer newIndex = k - count + 1;
            newQueryParams.put(newIndex, v);
         });
         Objects.requireNonNull(query);
         newQueryParams.forEach(query::setParameter);
      }

      public List<Integer> getRemoveParamIndex() {
         return this.removeParamIndex;
      }

      public void setRemoveParamIndex(List<Integer> removeParamIndex) {
         this.removeParamIndex = removeParamIndex;
      }

      public Map<Integer, Object> getAllQueryParams() {
         return this.allQueryParams;
      }

      public void setAllQueryParams(Map<Integer, Object> allQueryParams) {
         this.allQueryParams = allQueryParams;
      }
   }

   public static class EmptyQueryResolveResult extends QueryResolveResult {
      private boolean positionParam;
      private JpaParameters parameters;
      private Object[] values;

      public EmptyQueryResolveResult(String afterParseSQL, boolean positionParam, JpaParameters parameters, Object[] values) {
         this.afterParseSQL = afterParseSQL;
         this.positionParam = positionParam;
         this.parameters = parameters;
         this.values = values;
      }

      public void setQueryParams(Query query) {
         if (this.parameters != null && !this.parameters.isEmpty()) {
            if (this.positionParam) {
               Map<Integer, Object> positionQueryParams = JpaExtendQueryUtils.toPositionMap(this.values);
               positionQueryParams.forEach((position, value) -> query.setParameter(position + 1, value));
            } else {
               Map<String, Object> nameQueryParams = JpaExtendQueryUtils.getParams(this.parameters, this.values);
               Objects.requireNonNull(query);
               nameQueryParams.forEach(query::setParameter);
            }

         }
      }

      public boolean isPositionParam() {
         return this.positionParam;
      }

      public void setPositionParam(boolean positionParam) {
         this.positionParam = positionParam;
      }

      public JpaParameters getParameters() {
         return this.parameters;
      }

      public void setParameters(JpaParameters parameters) {
         this.parameters = parameters;
      }

      public Object[] getValues() {
         return this.values;
      }

      public void setValues(Object[] values) {
         this.values = values;
      }
   }
}
