package com.kuma.boot.data.jpa.extend.revision;

import com.kuma.boot.data.jpa.base.repository.JpaExtendRepositoryImpl;
import com.kuma.boot.data.jpa.extend.JpaExtendQueryLookupStrategy;
import com.kuma.boot.data.jpa.extend.RevisionRepositoryFactory;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.query.DefaultJpaQueryMethodFactory;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.jpa.repository.query.QueryRewriterProvider;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.ValueExpressionDelegate;

public class JpaExtendRepositoryFactoryWithRevision extends RevisionRepositoryFactory {
   private final EntityManager entityManager;
   private final PersistenceProvider extractor;
   private EscapeCharacter escapeCharacter;
   private JpaQueryMethodFactory queryMethodFactory;
   private QueryRewriterProvider queryRewriterProvider;

   public JpaExtendRepositoryFactoryWithRevision(EntityManager entityManager, Class<?> revisionEntityClass) {
      super(entityManager, revisionEntityClass);
      this.escapeCharacter = EscapeCharacter.DEFAULT;
      this.entityManager = entityManager;
      this.extractor = PersistenceProvider.fromEntityManager(entityManager);
      this.queryMethodFactory = new DefaultJpaQueryMethodFactory(this.extractor);
      this.queryRewriterProvider = QueryRewriterProvider.simple();
   }

   protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.@Nullable Key key, ValueExpressionDelegate valueExpressionDelegate) {
      return Optional.of(JpaExtendQueryLookupStrategy.create(this.entityManager, this.queryMethodFactory, key, valueExpressionDelegate, this.queryRewriterProvider, this.escapeCharacter));
   }

   protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
      return super.getTargetRepository(information, entityManager);
   }

   protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
      return JpaExtendRepositoryImpl.class;
   }
}
