package com.kuma.boot.data.jpa.extend;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class JpaExtendRepositoryFactoryBean<R extends Repository<T, I>, T, I extends Serializable> extends JpaRepositoryFactoryBean<R, T, I> {
   private EntityManager entityManager;
   private EntityPathResolver entityPathResolver;
   private EscapeCharacter escapeCharacter;
   private JpaQueryMethodFactory queryMethodFactory;
   private JPAQueryFactory jpaQueryFactory;

   public JpaExtendRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
      super(repositoryInterface);
   }

   @PersistenceContext
   public void setEntityManager(EntityManager entityManager) {
      super.setEntityManager(entityManager);
      this.entityManager = entityManager;
   }

   @Autowired
   public void setEntityPathResolver(ObjectProvider<EntityPathResolver> resolver) {
      super.setEntityPathResolver(resolver);
      this.entityPathResolver = (EntityPathResolver)resolver.getIfAvailable(() -> SimpleEntityPathResolver.INSTANCE);
   }

   @Autowired
   public void setQueryMethodFactory(@Nullable ObjectProvider<JpaQueryMethodFactory> factory) {
      super.setQueryMethodFactory(factory);
   }

   @Autowired
   public void setJPAQueryFactory(JPAQueryFactory jpaQueryFactory) {
      this.jpaQueryFactory = jpaQueryFactory;
   }

   protected RepositoryFactorySupport doCreateRepositoryFactory() {
      JpaExtendRepositoryFactory factory = new JpaExtendRepositoryFactory(this.entityManager);
      factory.setEntityPathResolver(this.entityPathResolver);
      factory.setEscapeCharacter(this.escapeCharacter);
      if (this.queryMethodFactory != null) {
         factory.setQueryMethodFactory(this.queryMethodFactory);
      }

      return factory;
   }

   public void setEscapeCharacter(char escapeCharacter) {
      super.setEscapeCharacter(escapeCharacter);
      this.escapeCharacter = EscapeCharacter.of(escapeCharacter);
   }
}
