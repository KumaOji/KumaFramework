package com.kuma.boot.data.jpa.simplestjpa;

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;

public class ServiceImpl<R extends BaseRepository<T, ID>, T, ID extends Serializable> implements IService<T, ID> {
   @Autowired(
      required = false
   )
   protected R repository;
   @Autowired(
      required = false
   )
   private EntityManager entityManager;
   @Autowired(
      required = false
   )
   private JPAQueryFactory jpaQueryFactory;

   public ServiceImpl() {
   }

   public BaseRepository<T, ID> getRepository() {
      return this.repository;
   }

   public EntityManager getEntityManager() {
      return this.entityManager;
   }

   public JPAQueryFactory queryChain() {
      return this.jpaQueryFactory;
   }

   public JPAUpdateClause updateChain(EntityPath<T> entityPath) {
      return this.jpaQueryFactory.update(entityPath);
   }
}
