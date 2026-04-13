package com.kuma.boot.data.jpa.base.service.impl;

import cn.hutool.core.util.StrUtil;
import com.querydsl.core.types.Predicate;
import com.kuma.boot.common.exception.LockException;
import com.kuma.boot.data.jpa.base.entity.JpaSuperEntity;
import com.kuma.boot.data.jpa.base.repository.JpaSuperRepository;
import com.kuma.boot.data.jpa.base.service.JpaSuperService;
import com.kuma.boot.lock.support.DistributedLock;
import jakarta.annotation.Resource;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

public class JpaSuperServiceImpl<R extends JpaSuperRepository<T, I>, T extends JpaSuperEntity<I>, I extends Serializable> implements JpaSuperService<T, I> {
   @Autowired
   private R repository;
   @Resource
   private JdbcClient jdbcClient;
   @Resource
   private JdbcTemplate jdbcTemplate;

   public JpaSuperServiceImpl() {
   }

   public JpaSuperRepository<T, I> repository() {
      return this.repository;
   }

   public JdbcClient jdbcClient() {
      return this.jdbcClient;
   }

   public JdbcTemplate jdbcTemplate() {
      return this.jdbcTemplate;
   }

   public boolean saveIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate, String msg) {
      if (lock == null) {
         throw new LockException("\u5206\u5e03\u5f0f\u9501\u4e3a\u7a7a");
      } else if (StrUtil.isEmpty(lockKey)) {
         throw new LockException("\u9501\u7684key\u4e3a\u7a7a");
      } else {
         return true;
      }
   }

   public boolean saveIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate) {
      return this.saveIdempotency(entity, lock, lockKey, predicate, (String)null);
   }

   public boolean saveOrUpdateIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate, String msg) {
      return false;
   }

   public boolean saveOrUpdateIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate) {
      return this.saveOrUpdateIdempotency(entity, lock, lockKey, predicate, (String)null);
   }
}
