package com.kuma.boot.data.jpa.extend;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.AbstractJpaQuery;
import org.springframework.data.jpa.repository.query.InvalidJpaQueryMethodException;
import org.springframework.data.jpa.repository.query.JpaParameters;
import org.springframework.data.jpa.repository.query.JpaParametersParameterAccessor;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.jpa.repository.query.QueryUtils;

public abstract class AbstractJpaExtendQuery extends AbstractJpaQuery {
   protected DeclaredQuery declaredQuery;
   protected EntityManager em;

   public AbstractJpaExtendQuery(JpaQueryMethod method, EntityManager em, MyQuery myQuery) {
      super(method, em);
      if (QueryUtils.hasConstructorExpression(myQuery.value())) {
         throw new InvalidJpaQueryMethodException("SQL Cannot has constructor expression , SQL: " + myQuery.value());
      } else {
         this.declaredQuery = new DeclaredQuery(myQuery.value(), myQuery.countQuery(), StringUtils.isEmpty(myQuery.countProjection()) ? null : myQuery.countProjection(), myQuery.nativeQuery(), myQuery.expressionQuery());
         this.em = em;
      }
   }

   protected Query doCreateQuery(JpaParametersParameterAccessor accessor) {
      JpaParameters parameters = this.getQueryMethod().getParameters();
      Object[] values = accessor.getValues();
      RemovePageSortParametersParameterAccessor removePageSortParametersParameterAccessor = new RemovePageSortParametersParameterAccessor(parameters, values);
      QueryResolveResult resolveResult = ExpressionQueryResolverStrategy.resolve(this.declaredQuery.getQueryString(), this.declaredQuery.isExpressionQuery(), parameters, removePageSortParametersParameterAccessor.getRemovePageSortParameters());
      String nativeQuery = resolveResult.getAfterParseSQL();
      LogUtils.info("MyExtendJpaQuery before sql :{} resolve sql:{}", new Object[]{this.declaredQuery.getQueryString(), nativeQuery});
      String sortedQueryString = QueryUtils.applySorting(nativeQuery, removePageSortParametersParameterAccessor.getSort());
      Query query = this.createJpaQuery(sortedQueryString);
      resolveResult.setQueryParams(query);
      if (parameters.hasPageableParameter()) {
         Pageable pageable = (Pageable)values[parameters.getPageableIndex()];
         if (pageable != null) {
            query.setFirstResult((int)pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
         }
      }

      return query;
   }

   protected abstract Query createJpaQuery(String sortedQueryString);

   protected Query doCreateCountQuery(JpaParametersParameterAccessor accessor) {
      JpaParameters parameters = this.getQueryMethod().getParameters();
      Object[] values = accessor.getValues();
      RemovePageSortParametersParameterAccessor removePageSortParametersParameterAccessor = new RemovePageSortParametersParameterAccessor(parameters, values);
      String countQueryString = StringUtils.isEmpty(this.declaredQuery.getCountQueryString()) ? this.declaredQuery.getQueryString() : this.declaredQuery.getCountQueryString();
      QueryResolveResult resolveResult = ExpressionQueryResolverStrategy.resolve(countQueryString, this.declaredQuery.isExpressionQuery(), parameters, removePageSortParametersParameterAccessor.getRemovePageSortParameters());
      String queryString = resolveResult.getAfterParseSQL();
      Query query = this.createJpaCountQuery(queryString);
      resolveResult.setQueryParams(query);
      return query;
   }

   protected abstract Query createJpaCountQuery(String queryString);

   public static class DeclaredQuery {
      private String queryString;
      private String countQueryString;
      private String countProjection;
      private boolean nativeQuery;
      private boolean expressionQuery;

      public DeclaredQuery() {
      }

      public DeclaredQuery(String queryString, String countQueryString, String countProjection, boolean nativeQuery, boolean expressionQuery) {
         this.queryString = queryString;
         this.countQueryString = countQueryString;
         this.countProjection = countProjection;
         this.nativeQuery = nativeQuery;
         this.expressionQuery = expressionQuery;
      }

      public String getQueryString() {
         return this.queryString;
      }

      public void setQueryString(String queryString) {
         this.queryString = queryString;
      }

      public String getCountQueryString() {
         return this.countQueryString;
      }

      public void setCountQueryString(String countQueryString) {
         this.countQueryString = countQueryString;
      }

      public String getCountProjection() {
         return this.countProjection;
      }

      public void setCountProjection(String countProjection) {
         this.countProjection = countProjection;
      }

      public boolean isNativeQuery() {
         return this.nativeQuery;
      }

      public void setNativeQuery(boolean nativeQuery) {
         this.nativeQuery = nativeQuery;
      }

      public boolean isExpressionQuery() {
         return this.expressionQuery;
      }

      public void setExpressionQuery(boolean expressionQuery) {
         this.expressionQuery = expressionQuery;
      }
   }
}
