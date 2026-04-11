package com.kuma.boot.webagg.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import com.querydsl.core.types.Predicate;
import com.kuma.boot.cache.redis.model.CacheKey;
import com.kuma.boot.data.jpa.base.repository.JpaSuperRepository;
import com.kuma.boot.data.mybatis.mybatisplus.base.mapper.MpSuperMapper;
import com.kuma.boot.lock.support.DistributedLock;
import com.kuma.boot.webagg.entity.SuperEntity;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

public interface BaseSuperService<T extends SuperEntity<T, I>, I extends Serializable> extends IService<T> {
   MpSuperMapper<T, I> mapper();

   JpaSuperRepository<T, I> respository();

   JdbcTemplate jdbcTemplate();

   JdbcClient jdbcClient();

   void refreshCache();

   void clearCache();

   T getByIdCache(I id);

   T getByKey(CacheKey key, Function<CacheKey, Object> loader);

   List<T> findByIds(@NonNull Collection<? extends Serializable> ids, Function<Collection<? extends Serializable>, Collection<T>> loader);

   boolean saveIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate, Wrapper<T> countWrapper, String msg);

   boolean saveIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate, Wrapper<T> countWrapper);

   boolean saveOrUpdateIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate, Wrapper<T> countWrapper, String msg);

   boolean saveOrUpdateIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate, Wrapper<T> countWrapper);

   String getColumnName(SFunction<T, ?> function);

   boolean deleteByFields(SFunction<T, ?> field, Collection<?> fieldValues);

   boolean deleteByField(SFunction<T, ?> field, Object fieldValue);

   boolean deleteByIds(Collection<? extends Serializable> idList);

   boolean deleteById(Serializable id);

   Long countByField(SFunction<T, ?> field, Object fieldValue);

   boolean existedByField(SFunction<T, ?> field, Object fieldValue, Serializable id);

   boolean existedByField(SFunction<T, ?> field, Object fieldValue);

   boolean existedById(Serializable id);

   List<T> findAllByFields(SFunction<T, ?> field, Collection<? extends Serializable> fieldValues);

   List<T> findAllByField(SFunction<T, ?> field, Object fieldValue);

   List<T> findAllByIds(Collection<? extends Serializable> idList);

   Optional<T> findByField(SFunction<T, ?> field, Object fieldValue);

   Optional<T> findById(Serializable id);

   List<T> findAll();

   boolean updateByField(T t, SFunction<T, ?> field, Object fieldValue);

   boolean updateAllById(Collection<T> entityList);

   List<T> saveAll(List<T> list);

   default T jpaFindById(I id) {
      return (T)(this.respository().findById(id).orElse((Object)null));
   }

   default boolean jpaExistsById(I id) {
      return this.respository().existsById(id);
   }

   default long jpaCount() {
      return this.respository().count();
   }

   default long jpaCount(Example<T> example) {
      return this.respository().count(example);
   }

   default List<T> jpaFindAll() {
      return this.respository().findAll();
   }

   default List<T> jpaFindAll(Sort sort) {
      return this.respository().findAll(sort);
   }

   default List<T> jpaFindAll(Example<T> example) {
      return this.respository().findAll(example);
   }

   default List<T> jpaFindAll(Example<T> example, Sort sort) {
      return this.respository().findAll(example, sort);
   }

   default Page<T> jpaFindByPage(Pageable pageable) {
      return this.respository().findAll(pageable);
   }

   default Page<T> jpaFindByPage(int pageNumber, int pageSize) {
      return this.jpaFindByPage(PageRequest.of(pageNumber, pageSize));
   }

   default Page<T> jpaFindByPage(int pageNumber, int pageSize, Sort sort) {
      return this.jpaFindByPage(PageRequest.of(pageNumber, pageSize, sort));
   }

   default Page<T> jpaFindByPage(int pageNumber, int pageSize, Sort.Direction direction, String... properties) {
      return this.jpaFindByPage(PageRequest.of(pageNumber, pageSize, direction, properties));
   }

   default Page<T> jpaFindByPage(Example<T> example, Pageable pageable) {
      return this.respository().findAll(example, pageable);
   }

   default Page<T> jpaFindByPage(Example<T> example, int pageNumber, int pageSize) {
      return this.respository().findAll(example, PageRequest.of(pageNumber, pageSize));
   }

   default Page<T> jpaFindByPage(int pageNumber, int pageSize, Sort.Direction direction) {
      return this.jpaFindByPage(PageRequest.of(pageNumber, pageSize, direction, new String[0]));
   }

   default void jpaDelete(T entity) {
      this.respository().delete(entity);
   }

   default void jpaDeleteAllInBatch() {
      this.respository().deleteAllInBatch();
   }

   default void jpaDeleteAll(Iterable<T> entities) {
      this.respository().deleteAll(entities);
   }

   default void jpaDeleteAll() {
      this.respository().deleteAll();
   }

   default void jpaDeleteById(I id) {
      this.respository().deleteById(id);
   }

   default T jpaSave(T domain) {
      return (T)(this.respository().save(domain));
   }

   default <S extends T> List<S> jpaSaveAll(Iterable<S> entities) {
      return this.respository().saveAll(entities);
   }

   default T jpaSaveAndFlush(T entity) {
      return (T)(this.respository().saveAndFlush(entity));
   }

   default T jpaSaveOrUpdate(T entity) {
      return (T)this.jpaSaveAndFlush(entity);
   }

   default void jpaFlush() {
      this.respository().flush();
   }
}
