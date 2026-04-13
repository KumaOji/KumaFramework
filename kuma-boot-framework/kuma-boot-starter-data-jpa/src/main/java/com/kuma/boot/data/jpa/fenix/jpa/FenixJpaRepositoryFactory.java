package com.kuma.boot.data.jpa.fenix.jpa;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.DefaultJpaQueryMethodFactory;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.JpaQueryConfiguration;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.jpa.repository.query.QueryEnhancerSelector;
import org.springframework.data.jpa.repository.query.QueryRewriterProvider;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.CachingValueExpressionDelegate;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.ValueExpressionDelegate;

public class FenixJpaRepositoryFactory extends JpaRepositoryFactory {
   private final EntityManager entityManager;
   private final QueryExtractor extractor;
   private final QueryEnhancerSelector queryEnhancerSelector;
   private final EscapeCharacter escapeCharacter;
   private final JpaQueryMethodFactory queryMethodFactory;
   private final QueryRewriterProvider queryRewriterProvider;

   public FenixJpaRepositoryFactory(EntityManager entityManager) {
      super(entityManager);
      this.queryEnhancerSelector = QueryEnhancerSelector.DEFAULT_SELECTOR;
      this.escapeCharacter = EscapeCharacter.DEFAULT;
      this.entityManager = entityManager;
      this.extractor = PersistenceProvider.fromEntityManager(entityManager);
      this.queryMethodFactory = new DefaultJpaQueryMethodFactory(this.extractor);
      this.queryRewriterProvider = QueryRewriterProvider.simple();
   }

   protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.Key key, ValueExpressionDelegate valueExpressionDelegate) {
      JpaQueryConfiguration queryConfiguration = new JpaQueryConfiguration(this.queryRewriterProvider, this.queryEnhancerSelector, new CachingValueExpressionDelegate(valueExpressionDelegate), this.escapeCharacter);
      return Optional.of(FenixQueryLookupStrategy.create(this.entityManager, this.queryMethodFactory, key, queryConfiguration));
   }

   protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
      return FenixSimpleJpaRepository.class;
   }
}
