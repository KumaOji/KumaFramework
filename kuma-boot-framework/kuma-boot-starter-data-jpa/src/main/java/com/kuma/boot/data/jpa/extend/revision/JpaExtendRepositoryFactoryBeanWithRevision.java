package com.kuma.boot.data.jpa.extend.revision;

import jakarta.persistence.EntityManager;
import java.io.Serializable;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.history.RevisionRepository;

public class JpaExtendRepositoryFactoryBeanWithRevision<R extends RevisionRepository<T, I, Long>, T, I extends Serializable> extends EnversRevisionRepositoryFactoryBean<R, T, I, Long> {
   private Class<?> revisionEntityClazz;

   public JpaExtendRepositoryFactoryBeanWithRevision(Class<? extends R> repositoryInterface) {
      super(repositoryInterface);
   }

   protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
      JpaExtendRepositoryFactoryWithRevision factorySupport = new JpaExtendRepositoryFactoryWithRevision(entityManager, this.revisionEntityClazz);
      return factorySupport;
   }

   public void setRevisionEntityClass(Class<?> revisionEntityClass) {
      this.revisionEntityClazz = revisionEntityClass;
      super.setRevisionEntityClass(revisionEntityClass);
   }
}
