package com.kuma.boot.data.jpa.extend;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.hibernate.envers.DefaultRevisionEntity;
import org.springframework.data.envers.repository.support.ReflectionRevisionEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.history.support.RevisionEntityInformation;

public class RevisionRepositoryFactory<T, ID, N extends Number & Comparable<N>> extends JpaRepositoryFactory {
   private final RevisionEntityInformation revisionEntityInformation;
   private final EntityManager entityManager;

   public RevisionRepositoryFactory(EntityManager entityManager, Class<?> revisionEntityClass) {
      super(entityManager);
      this.entityManager = entityManager;
      this.revisionEntityInformation = (RevisionEntityInformation)Optional.ofNullable(revisionEntityClass).filter((it) -> !it.equals(DefaultRevisionEntity.class)).map(ReflectionRevisionEntityInformation::new).orElseGet(DefaultRevisionEntityInformation::new);
   }

   protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
      return super.getRepositoryFragments(metadata);
   }
}
