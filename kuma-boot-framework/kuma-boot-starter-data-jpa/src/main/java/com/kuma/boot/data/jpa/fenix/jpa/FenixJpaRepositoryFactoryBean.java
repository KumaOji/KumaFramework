package com.kuma.boot.data.jpa.fenix.jpa;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class FenixJpaRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends JpaRepositoryFactoryBean<T, S, ID> {
   public FenixJpaRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
      super(repositoryInterface);
   }

   protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
      return new FenixJpaRepositoryFactory(entityManager);
   }

   public void afterPropertiesSet() {
      super.setRepositoryBaseClass(FenixSimpleJpaRepository.class);
      super.afterPropertiesSet();
   }
}
