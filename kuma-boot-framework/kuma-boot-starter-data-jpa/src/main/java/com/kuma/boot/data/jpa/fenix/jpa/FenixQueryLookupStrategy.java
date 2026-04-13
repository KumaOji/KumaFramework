package com.kuma.boot.data.jpa.fenix.jpa;

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.persistence.EntityManager;
import java.lang.reflect.Method;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.query.DeclaredQuery;
import org.springframework.data.jpa.repository.query.JpaQueryConfiguration;
import org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.util.StringUtils;

public class FenixQueryLookupStrategy implements QueryLookupStrategy {
   private final EntityManager entityManager;
   private final QueryLookupStrategy jpaQueryLookupStrategy;
   private final JpaQueryMethodFactory queryMethodFactory;

   private FenixQueryLookupStrategy(EntityManager entityManager, JpaQueryMethodFactory queryMethodFactory, QueryLookupStrategy.@Nullable Key key, JpaQueryConfiguration queryConfiguration) {
      this.entityManager = entityManager;
      this.queryMethodFactory = queryMethodFactory;
      this.jpaQueryLookupStrategy = JpaQueryLookupStrategy.create(entityManager, queryMethodFactory, key, queryConfiguration);
   }

   static QueryLookupStrategy create(EntityManager entityManager, JpaQueryMethodFactory queryMethodFactory, QueryLookupStrategy.@Nullable Key key, JpaQueryConfiguration configuration) {
      return new FenixQueryLookupStrategy(entityManager, queryMethodFactory, key, configuration);
   }

   public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {
      QueryFenix queryFenixAnnotation = (QueryFenix)method.getAnnotation(QueryFenix.class);
      if (queryFenixAnnotation == null) {
         return this.jpaQueryLookupStrategy.resolveQuery(method, metadata, factory, namedQueries);
      } else {
         JpaQueryMethod queryMethod = this.queryMethodFactory.build(method, metadata, factory);
         FenixJpaQuery fenixJpaQuery = new FenixJpaQuery(queryMethod, this.entityManager);
         fenixJpaQuery.setQueryFenix(queryFenixAnnotation);
         fenixJpaQuery.setQueryClass(method.getDeclaringClass());
         fenixJpaQuery.setHasDeclaredCountQuery(this.getCountQuery(queryMethod, namedQueries, this.entityManager, queryFenixAnnotation.nativeQuery()) != null);
         return fenixJpaQuery;
      }
   }

   private @Nullable DeclaredQuery getCountQuery(JpaQueryMethod method, NamedQueries namedQueries, EntityManager entityManager, boolean nativeQuery) {
      String query = doGetCountQuery(method, namedQueries, entityManager);
      return StringUtils.hasText(query) ? this.getDeclaredQuery(query, nativeQuery) : null;
   }

   private static @Nullable String doGetCountQuery(JpaQueryMethod method, NamedQueries namedQueries, EntityManager em) {
      if (StringUtils.hasText(method.getCountQuery())) {
         return method.getCountQuery();
      } else {
         String queryName = method.getNamedCountQueryName();
         if (!StringUtils.hasText(queryName)) {
            return method.getCountQuery();
         } else if (namedQueries.hasQuery(queryName)) {
            return namedQueries.getQuery(queryName);
         } else {
            return hasNamedQuery(em, queryName) ? method.getQueryExtractor().extractQueryString(em.createNamedQuery(queryName)) : null;
         }
      }
   }

   private static boolean hasNamedQuery(EntityManager em, String queryName) {
      try {
         EntityManager lookupEm = em.getEntityManagerFactory().createEntityManager();

         boolean var3;
         try {
            lookupEm.createNamedQuery(queryName);
            var3 = true;
         } catch (Throwable var6) {
            if (lookupEm != null) {
               try {
                  lookupEm.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (lookupEm != null) {
            lookupEm.close();
         }

         return var3;
      } catch (IllegalArgumentException var7) {
         if (LogUtils.isDebugEnabled()) {
            LogUtils.debug(String.format("Did not find named query %s", queryName), new Object[0]);
         }

         return false;
      }
   }

   private DeclaredQuery getDeclaredQuery(String query, boolean isNativeQuery) {
      return isNativeQuery ? DeclaredQuery.nativeQuery(query) : DeclaredQuery.jpqlQuery(query);
   }
}
