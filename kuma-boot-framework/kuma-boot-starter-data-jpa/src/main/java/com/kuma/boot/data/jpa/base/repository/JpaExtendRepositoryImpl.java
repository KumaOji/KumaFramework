/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.data.jpa.base.repository;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.kuma.boot.core.utils.context.ContextUtils;
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

/**
 * 基础jpa Repository
 *
 * @param <T> the type of the entity to handle
 * @param <I> the type of the entity's identifier
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:32:26
 */
public class JpaExtendRepositoryImpl<T, I extends Serializable>
        extends SimpleJpaRepository<T, I> implements JpaExtendRepository<T, I> {

   protected final JPAQueryFactory jpaQueryFactory;
   protected final QuerydslJpaPredicateExecutor<T> jpaPredicateExecutor;
   protected final EntityManager em;
   private final EntityPath<T> path;
   protected final Querydsl querydsl;

   public JpaExtendRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
      super(entityInformation, em);

      Class<T> domainClass = null;
      if (entityInformation instanceof AbstractEntityInformation) {
         AbstractEntityInformation<T, I> abstractEntityInformation = (AbstractEntityInformation<T, I>) entityInformation;
         domainClass = abstractEntityInformation.getJavaType();
      }

      if (domainClass == null) {
         throw new IllegalArgumentException("domainClass is null");
      }

      this.em = em;
      this.jpaPredicateExecutor =
              new QuerydslJpaPredicateExecutor<>(
                      JpaEntityInformationSupport.getEntityInformation(domainClass, em),
                      em,
                      SimpleEntityPathResolver.INSTANCE,
                      getRepositoryMethodMetadata());
      // todo 此处需要修改
      this.jpaQueryFactory = new JPAQueryFactory(em);
      this.path = SimpleEntityPathResolver.INSTANCE.createPath(domainClass);
      this.querydsl = new Querydsl(em, new PathBuilder<T>(path.getType(), path.getMetadata()));
   }

   public JpaExtendRepositoryImpl(Class<T> domainClass, EntityManager em) {
      super(domainClass, em);
      this.em = em;
      this.jpaPredicateExecutor =
              new QuerydslJpaPredicateExecutor<>(
                      JpaEntityInformationSupport.getEntityInformation(domainClass, em),
                      em,
                      SimpleEntityPathResolver.INSTANCE,
                      getRepositoryMethodMetadata());
      // todo 此处需要修改
      this.jpaQueryFactory = new JPAQueryFactory(em);
      this.path = SimpleEntityPathResolver.INSTANCE.createPath(domainClass);
      this.querydsl = new Querydsl(em, new PathBuilder<T>(path.getType(), path.getMetadata()));
   }

   @Resource
   private JdbcClient jdbcClient;

   @Override
   public JdbcClient jdbcClient() {
      if (jdbcClient == null) {
         ObjectProvider<JdbcClient> beanProvider = ContextUtils.getApplicationContext()
                 .getBeanProvider(JdbcClient.class);
         jdbcClient = beanProvider.getIfAvailable();
      }
      return jdbcClient;
   }

   @Resource
   private JdbcTemplate jdbcTemplate;

   @Override
   public JdbcTemplate jdbcTemplate() {
      if (jdbcTemplate == null) {
         ObjectProvider<JdbcTemplate> beanProvider = ContextUtils.getApplicationContext()
                 .getBeanProvider(JdbcTemplate.class);
         jdbcTemplate = beanProvider.getIfAvailable();
      }
      return jdbcTemplate;
   }

   /**
    * findPageable
    *
    * @param predicate predicate
    * @param pageable  pageable
    * @param orders    orders
    * @return {@link Page}{@literal <T>}
    * @since 2022-09-02 08:28:43
    */
   @Override
   public Page<T> findPageable(
           Predicate predicate, Pageable pageable, OrderSpecifier<?>... orders) {
      final JPAQuery<T> countQuery = jpaQueryFactory.selectFrom(path);
      countQuery.where(predicate);
      JPQLQuery<T> query = querydsl.applyPagination(pageable, countQuery);
      query.orderBy(orders);
      return PageableExecutionUtils.getPage(
              query.fetch(), pageable, () -> countQuery.fetch().size());
   }

   /**
    * count
    *
    * @param predicate predicate
    * @return int
    * @since 2022-09-02 08:28:54
    */
   @Override
   public int countByPredicate(Predicate predicate) {
      return jpaQueryFactory.selectFrom(path).where(predicate).fetch().size();
   }

   /**
    * exists
    *
    * @param predicate predicate
    * @return {@link Boolean }
    * @since 2022-09-02 08:29:00
    */
   @Override
   public Boolean existsByPredicate(Predicate predicate) {
      return jpaPredicateExecutor.exists(predicate);
   }

   /**
    * fetch
    *
    * @param predicate predicate
    * @return {@link List }<{@link T }>
    * @since 2022-09-02 08:29:03
    */
   @Override
   public List<T> fetch(Predicate predicate) {
      return jpaQueryFactory.selectFrom(path).where(predicate).fetch();
   }

   /**
    * fetchOne
    *
    * @param predicate predicate
    * @return {@link T }
    * @since 2021-10-09 20:30:50
    */
   @Override
   public T fetchOne(Predicate predicate) {
      return jpaQueryFactory.selectFrom(path).where(predicate).fetchOne();
   }

   /**
    * fetchCount
    *
    * @param predicate predicate
    * @return int
    * @since 2022-09-02 08:29:08
    */
   @Override
   public int fetchCount(Predicate predicate) {
      return jpaQueryFactory.selectFrom(path).where(predicate).fetch().size();
   }

   /**
    * find
    *
    * @param predicate predicate
    * @param expr      expr
    * @param o         o
    * @return {@link List }<{@link ? }>
    * @since 2022-09-02 08:29:12
    */
   @Override
   public List<?> find(Predicate predicate, Expression<?> expr, OrderSpecifier<?>... o) {
      return jpaQueryFactory.select(expr).from(path).where(predicate).orderBy(o).fetch();
   }

   @Override
   @Lock(LockModeType.PESSIMISTIC_WRITE)
   public Optional<T> findWithLockingById(I id) {
      return findById(id);
   }

   @Override
   @Lock(LockModeType.PESSIMISTIC_WRITE)
   public List<T> findAllWithLockingByIdIn(Collection<I> ids) {
      return findAllById(ids);
   }

   @Override
   @Lock(LockModeType.PESSIMISTIC_WRITE)
   public <S extends T> Optional<S> findOneWithLocking(Example<S> example) {
      return findOne(example);
   }

//	@Override
//	@Lock(LockModeType.PESSIMISTIC_WRITE)
//	public Optional<T> findByIdForUpdate(I id) {
//		return findById(id);
//	}

   /**
    * 通用加锁查询
    */
   @Override
   public T findWithLockingWithEm(I id) {
      return em.find(super.getDomainClass(), id, LockModeType.PESSIMISTIC_WRITE);
   }

   /**
    * 通用加锁查询列表
    */
   @Override
   public List<T> findAllWithLockingWithEm(List<I> ids) {
      String jpql = String.format("SELECT e FROM %s e WHERE e.id IN :ids",
              super.getDomainClass().getSimpleName());

      return em.createQuery(jpql, super.getDomainClass())
              .setParameter("ids", ids)
              .setLockMode(LockModeType.PESSIMISTIC_WRITE)
              .getResultList();
   }


}
