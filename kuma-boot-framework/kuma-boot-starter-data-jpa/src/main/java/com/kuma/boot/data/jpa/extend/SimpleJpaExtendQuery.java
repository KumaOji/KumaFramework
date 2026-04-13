package com.kuma.boot.data.jpa.extend;

import com.kuma.boot.common.utils.lang.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.jpa.repository.query.QueryUtils;

public class SimpleJpaExtendQuery extends AbstractJpaExtendQuery {
   public SimpleJpaExtendQuery(JpaQueryMethod method, EntityManager em, MyQuery myQuery) {
      super(method, em, myQuery);
   }

   protected Query createJpaQuery(String sortedQueryString) {
      Class<?> objectType = this.getQueryMethod().getReturnedObjectType();
      Query query;
      if (this.getQueryMethod().isQueryForEntity()) {
         query = this.em.createQuery(sortedQueryString, objectType);
      } else {
         query = this.em.createQuery(sortedQueryString);
      }

      return query;
   }

   protected Query createJpaCountQuery(String queryString) {
      if (StringUtils.isEmpty(this.declaredQuery.getCountQueryString())) {
         queryString = QueryUtils.createCountQueryFor(queryString, this.declaredQuery.getQueryString(), this.declaredQuery.isNativeQuery());
      }

      Query query = this.em.createQuery(queryString, Long.class);
      return query;
   }

   public boolean hasDeclaredCountQuery() {
      return false;
   }
}
