package com.kuma.boot.data.jpa.extend;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;

public class NativeJpaExtendQuery extends AbstractJpaExtendQuery {
   public NativeJpaExtendQuery(JpaQueryMethod method, EntityManager em, MyQuery myQuery) {
      super(method, em, myQuery);
   }

   protected Query createJpaQuery(String sortedQueryString) {
      Class<?> objectType = this.getQueryMethod().getReturnedObjectType();
      Query query;
      if (this.getQueryMethod().isQueryForEntity()) {
         query = this.em.createNativeQuery(sortedQueryString, objectType);
      } else {
         query = this.em.createNativeQuery(sortedQueryString);
         ((NativeQueryImpl)query.unwrap(NativeQueryImpl.class)).setResultTransformer(new JpaExtendResultTransformer(objectType));
      }

      return query;
   }

   protected Query createJpaCountQuery(String queryString) {
      Query query = this.em.createNativeQuery(JpaExtendQueryUtils.toCountQuery(queryString));
      return query;
   }

   public boolean hasDeclaredCountQuery() {
      return false;
   }
}
