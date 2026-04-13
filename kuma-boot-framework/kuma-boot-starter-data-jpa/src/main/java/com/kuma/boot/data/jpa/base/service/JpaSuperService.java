package com.kuma.boot.data.jpa.base.service;

import com.querydsl.core.types.Predicate;
import com.kuma.boot.data.jpa.base.entity.JpaSuperEntity;
import com.kuma.boot.data.jpa.base.repository.JpaSuperRepository;
import com.kuma.boot.lock.support.DistributedLock;
import java.io.Serializable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

public interface JpaSuperService<T extends JpaSuperEntity<I>, I extends Serializable> {
   JdbcClient jdbcClient();

   JdbcTemplate jdbcTemplate();

   JpaSuperRepository<T, I> repository();

   boolean saveIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate, String msg);

   boolean saveIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate);

   boolean saveOrUpdateIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate, String msg);

   boolean saveOrUpdateIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate);
}
