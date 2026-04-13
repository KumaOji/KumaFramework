package com.kuma.boot.data.jpa.base.repository;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.kuma.boot.common.utils.context.ContextUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

public class JpaExtendRepositoryImpl<T, I extends Serializable> extends SimpleJpaRepository<T, I> implements JpaExtendRepository<T, I> {
   protected final JPAQueryFactory jpaQueryFactory;
   protected final QuerydslJpaPredicateExecutor<T> jpaPredicateExecutor;
   protected final EntityManager em;
   private final EntityPath<T> path;
   protected final Querydsl querydsl;
   @Resource
   private JdbcClient jdbcClient;
   @Resource
   private JdbcTemplate jdbcTemplate;

   public JpaExtendRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
      super(entityInformation, em);
      Class<T> domainClass = null;
      if (entityInformation instanceof AbstractEntityInformation<T, I> abstractEntityInformation) {
         domainClass = abstractEntityInformation.getJavaType();
      }

      if (domainClass == null) {
         throw new IllegalArgumentException("domainClass is null");
      } else {
         this.em = em;
         this.jpaPredicateExecutor = new QuerydslJpaPredicateExecutor(JpaEntityInformationSupport.getEntityInformation(domainClass, em), em, SimpleEntityPathResolver.INSTANCE, this.getRepositoryMethodMetadata());
         this.jpaQueryFactory = new JPAQueryFactory(em);
         this.path = SimpleEntityPathResolver.INSTANCE.createPath(domainClass);
         this.querydsl = new Querydsl(em, new PathBuilder(this.path.getType(), this.path.getMetadata()));
      }
   }

   public JpaExtendRepositoryImpl(Class<T> domainClass, EntityManager em) {
      super(domainClass, em);
      this.em = em;
      this.jpaPredicateExecutor = new QuerydslJpaPredicateExecutor(JpaEntityInformationSupport.getEntityInformation(domainClass, em), em, SimpleEntityPathResolver.INSTANCE, this.getRepositoryMethodMetadata());
      this.jpaQueryFactory = new JPAQueryFactory(em);
      this.path = SimpleEntityPathResolver.INSTANCE.createPath(domainClass);
      this.querydsl = new Querydsl(em, new PathBuilder(this.path.getType(), this.path.getMetadata()));
   }

   public JdbcClient jdbcClient() {
      if (this.jdbcClient == null) {
         ObjectProvider<JdbcClient> beanProvider = ContextUtils.getApplicationContext().getBeanProvider(JdbcClient.class);
         this.jdbcClient = (JdbcClient)beanProvider.getIfAvailable();
      }

      return this.jdbcClient;
   }

   public JdbcTemplate jdbcTemplate() {
      if (this.jdbcTemplate == null) {
         ObjectProvider<JdbcTemplate> beanProvider = ContextUtils.getApplicationContext().getBeanProvider(JdbcTemplate.class);
         this.jdbcTemplate = (JdbcTemplate)beanProvider.getIfAvailable();
      }

      return this.jdbcTemplate;
   }

   public Page<T> findPageable(Predicate predicate, Pageable pageable, OrderSpecifier<?>... orders) {
      JPAQuery<T> countQuery = this.jpaQueryFactory.selectFrom(this.path);
      countQuery.where(predicate);
      JPQLQuery<T> query = this.querydsl.applyPagination(pageable, countQuery);
      query.orderBy(orders);
      return PageableExecutionUtils.getPage(query.fetch(), pageable, () -> (long)countQuery.fetch().size());
   }

   public int countByPredicate(Predicate predicate) {
      return ((JPAQuery)this.jpaQueryFactory.selectFrom(this.path).where(predicate)).fetch().size();
   }

   public Boolean existsByPredicate(Predicate predicate) {
      return this.jpaPredicateExecutor.exists(predicate);
   }

   public List<T> fetch(Predicate predicate) {
      return ((JPAQuery)this.jpaQueryFactory.selectFrom(this.path).where(predicate)).fetch();
   }

   public T fetchOne(Predicate predicate) {
      return (T)((JPAQuery)this.jpaQueryFactory.selectFrom(this.path).where(predicate)).fetchOne();
   }

   public int fetchCount(Predicate predicate) {
      return ((JPAQuery)this.jpaQueryFactory.selectFrom(this.path).where(predicate)).fetch().size();
   }

   public List<?> find(Predicate predicate, Expression<?> expr, OrderSpecifier<?>... o) {
      return ((JPAQuery)((JPAQuery)((JPAQuery)this.jpaQueryFactory.select(expr).from(this.path)).where(predicate)).orderBy(o)).fetch();
   }

   @Lock(LockModeType.PESSIMISTIC_WRITE)
   public Optional<T> findWithLockingById(I id) {
      return this.findById(id);
   }

   @Lock(LockModeType.PESSIMISTIC_WRITE)
   public List<T> findAllWithLockingByIdIn(Collection<I> ids) {
      return this.findAllById(ids);
   }

   @Lock(LockModeType.PESSIMISTIC_WRITE)
   public <S extends T> Optional<S> findOneWithLocking(Example<S> example) {
      return this.findOne(example);
   }

   public T findWithLockingWithEm(I id) {
      return (T)this.em.find(super.getDomainClass(), id, LockModeType.PESSIMISTIC_WRITE);
   }

   public List<T> findAllWithLockingWithEm(List<I> ids) {
      String jpql = String.format("SELECT e FROM %s e WHERE e.id IN :ids", super.getDomainClass().getSimpleName());
      return this.em.createQuery(jpql, super.getDomainClass()).setParameter("ids", ids).setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
   }
}
