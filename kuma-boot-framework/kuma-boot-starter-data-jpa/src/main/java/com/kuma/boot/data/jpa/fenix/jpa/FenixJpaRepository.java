package com.kuma.boot.data.jpa.fenix.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface FenixJpaRepository<T, ID> extends JpaRepository<T, ID> {
   <S extends T> void saveBatch(Iterable<S> entities);

   <S extends T> void saveBatch(Iterable<S> entities, int batchSize);

   <S extends T> void updateBatch(Iterable<S> entities);

   <S extends T> void updateBatch(Iterable<S> entities, int batchSize);

   <S extends T> S saveOrUpdateByNotNullProperties(S entity);

   <S extends T> void saveOrUpdateAllByNotNullProperties(Iterable<S> entities);

   void deleteByIds(Iterable<ID> ids);

   void deleteBatchByIds(Iterable<ID> ids);

   void deleteBatchByIds(Iterable<ID> ids, int batchSize);
}
