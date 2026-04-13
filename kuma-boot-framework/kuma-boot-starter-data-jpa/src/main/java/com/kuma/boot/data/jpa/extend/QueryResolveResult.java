/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.data.jpa.extend;

import jakarta.persistence.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.query.JpaParameters;

/**
 * QueryResolveResult
 *
 * @author shuigedeng
 * @version 2026.03
 * @since 2025-12-19 09:30:45
 */
public abstract class QueryResolveResult {

   protected String afterParseSQL;

   public abstract void setQueryParams( Query query );

   public static class NameExpressionQueryResolveResult extends QueryResolveResult {

      private List<String> removeParamNames;

      private Map<String, Object> allQueryParams;

      public NameExpressionQueryResolveResult(
              String afterParseSQL,
              List<String> removeParamNames,
              Map<String, Object> allQueryParams ) {
         this.afterParseSQL = afterParseSQL;
         this.removeParamNames = removeParamNames;
         this.allQueryParams = allQueryParams;
      }

      @Override
      public void setQueryParams( Query query ) {
         removeParamNames.stream().forEach(item -> allQueryParams.remove(item));
         allQueryParams.forEach(query::setParameter);
      }

      public List<String> getRemoveParamNames() {
         return removeParamNames;
      }

      public void setRemoveParamNames( List<String> removeParamNames ) {
         this.removeParamNames = removeParamNames;
      }

      public Map<String, Object> getAllQueryParams() {
         return allQueryParams;
      }

      public void setAllQueryParams( Map<String, Object> allQueryParams ) {
         this.allQueryParams = allQueryParams;
      }
   }

   public static class PositionExpressionQueryResolveResult extends QueryResolveResult {

      private List<Integer> removeParamIndex;

      private Map<Integer, Object> allQueryParams;

      public PositionExpressionQueryResolveResult(
              String afterParseSQL,
              List<Integer> removeParamIndex,
              Map<Integer, Object> allQueryParams ) {
         this.afterParseSQL = afterParseSQL;
         this.removeParamIndex = removeParamIndex;
         this.allQueryParams = allQueryParams;
      }

      @Override
      public void setQueryParams( Query query ) {
         removeParamIndex.stream().forEach(item -> allQueryParams.remove(item));
         Map<Integer, Object> newQueryParams = new HashMap<>();

         allQueryParams.forEach(
                 ( k, v ) -> {
                    int count =
                            removeParamIndex.stream()
                                    .filter(item -> k > item)
                                    .collect(Collectors.toList())
                                    .size();
                    // 修改 索引 +1 是参数从1开始
                    Integer newIndex = k - count + 1;
                    newQueryParams.put(newIndex, v);
                 });

         newQueryParams.forEach(query::setParameter);
      }

      public List<Integer> getRemoveParamIndex() {
         return removeParamIndex;
      }

      public void setRemoveParamIndex( List<Integer> removeParamIndex ) {
         this.removeParamIndex = removeParamIndex;
      }

      public Map<Integer, Object> getAllQueryParams() {
         return allQueryParams;
      }

      public void setAllQueryParams( Map<Integer, Object> allQueryParams ) {
         this.allQueryParams = allQueryParams;
      }
   }

   public static class EmptyQueryResolveResult extends QueryResolveResult {

      private boolean positionParam;
      private JpaParameters parameters;

      private Object[] values;

      public EmptyQueryResolveResult(
              String afterParseSQL,
              boolean positionParam,
              JpaParameters parameters,
              Object[] values ) {
         this.afterParseSQL = afterParseSQL;
         this.positionParam = positionParam;
         this.parameters = parameters;
         this.values = values;
      }

      @Override
      public void setQueryParams( Query query ) {
         if (parameters == null || parameters.isEmpty()) {
            return;
         }
         if (positionParam) {
            Map<Integer, Object> positionQueryParams =
                    JpaExtendQueryUtils.toPositionMap(values);
            positionQueryParams.forEach(
                    ( position, value ) -> query.setParameter(position + 1, value));
         } else {
            Map<String, Object> nameQueryParams =
                    JpaExtendQueryUtils.getParams(parameters, values);
            nameQueryParams.forEach(query::setParameter);
         }
      }

      public boolean isPositionParam() {
         return positionParam;
      }

      public void setPositionParam( boolean positionParam ) {
         this.positionParam = positionParam;
      }

      public JpaParameters getParameters() {
         return parameters;
      }

      public void setParameters( JpaParameters parameters ) {
         this.parameters = parameters;
      }

      public Object[] getValues() {
         return values;
      }

      public void setValues( Object[] values ) {
         this.values = values;
      }
   }

   public String getAfterParseSQL() {
      return afterParseSQL;
   }

   public void setAfterParseSQL( String afterParseSQL ) {
      this.afterParseSQL = afterParseSQL;
   }
}
