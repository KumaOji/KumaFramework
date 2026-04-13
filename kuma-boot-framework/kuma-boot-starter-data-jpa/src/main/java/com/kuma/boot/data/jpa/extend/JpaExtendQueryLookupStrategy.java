package com.kuma.boot.data.jpa.extend;

import jakarta.persistence.EntityManager;
import java.lang.reflect.Method;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.JpaQueryConfiguration;
import org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.jpa.repository.query.QueryEnhancerSelector;
import org.springframework.data.jpa.repository.query.QueryRewriterProvider;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.CachingValueExpressionDelegate;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.ValueExpressionDelegate;

public class JpaExtendQueryLookupStrategy implements QueryLookupStrategy {
   private final EntityManager entityManager;
   private QueryLookupStrategy jpaQueryLookupStrategy;
   private JpaQueryMethodFactory queryMethodFactory;

   public JpaExtendQueryLookupStrategy(EntityManager em, JpaQueryMethodFactory queryMethodFactory, QueryLookupStrategy.@Nullable Key key, ValueExpressionDelegate valueExpressionDelegate, QueryRewriterProvider queryRewriterProvider, EscapeCharacter escape) {
      JpaQueryConfiguration jpaQueryConfiguration = new JpaQueryConfiguration(QueryRewriterProvider.simple(), QueryEnhancerSelector.DEFAULT_SELECTOR, new CachingValueExpressionDelegate(valueExpressionDelegate), EscapeCharacter.DEFAULT);
      this.jpaQueryLookupStrategy = JpaQueryLookupStrategy.create(em, queryMethodFactory, key, jpaQueryConfiguration);
      this.entityManager = em;
      this.queryMethodFactory = queryMethodFactory;
   }

   public static QueryLookupStrategy create(EntityManager em, JpaQueryMethodFactory queryMethodFactory, QueryLookupStrategy.@Nullable Key key, ValueExpressionDelegate valueExpressionDelegate, QueryRewriterProvider queryRewriterProvider, EscapeCharacter escape) {
      return new JpaExtendQueryLookupStrategy(em, queryMethodFactory, key, valueExpressionDelegate, queryRewriterProvider, escape);
   }

   public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {
      if (method.getAnnotation(MyQuery.class) == null) {
         return this.jpaQueryLookupStrategy.resolveQuery(method, metadata, factory, namedQueries);
      } else {
         MyQuery myQuery = (MyQuery)method.getAnnotation(MyQuery.class);
         JpaQueryMethod jpaQueryMethod = this.queryMethodFactory.build(method, metadata, factory);
         return (RepositoryQuery)(myQuery.nativeQuery() ? new NativeJpaExtendQuery(jpaQueryMethod, this.entityManager, myQuery) : new SimpleJpaExtendQuery(jpaQueryMethod, this.entityManager, myQuery));
      }
   }
}
