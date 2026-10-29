package com.kuma.boot.idgenerator.generator;

import cn.hutool.core.net.NetUtil;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.lock.support.DistributedLock;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.ValueOperations;

public class RedisLockIdGenerator implements CommandLineRunner, ApplicationContextAware {
   private ApplicationContext applicationContext;
   private static final String CACHE_ID_GENERATOR = "LOCK_ID_GENERATOR";
   private static final String CACHE_WORKERID_MAXID = "CACHE_WORKER_ID_MAXID";
   private static final String CACHE_ID_IP = "CACHE_ID_IP";
   private RedisRepository redisRepository;
   private DistributedLock distributedLock;

   public RedisLockIdGenerator(RedisRepository redisRepository, DistributedLock distributedLock) {
      this.redisRepository = redisRepository;
      this.distributedLock = distributedLock;
   }

   public void run(String... args) {
      this.idGeneratorWithDistributedLock();
   }

   public void idGeneratorWithDistributedLock() {
      if (Objects.isNull(this.redisRepository)) {
         this.redisRepository = (RedisRepository)this.applicationContext.getBean(RedisRepository.class);
      }

      if (Objects.isNull(this.distributedLock)) {
         this.distributedLock = (DistributedLock)this.applicationContext.getBean(DistributedLock.class);
      }

      String macAddress = NetUtil.getLocalMacAddress();
      boolean existWorkerId = this.redisRepository.opsForHash().hasKey("CACHE_ID_IP", macAddress);
      if (existWorkerId) {
         Integer workerId = (Integer)this.redisRepository.opsForHash().get("CACHE_ID_IP", macAddress);
         LogUtils.info("配置分布式workerId {} - {}", new Object[]{macAddress, workerId});
         this.initWorkerId(workerId);
      } else {
         try {
            boolean result = this.distributedLock.tryLock("LOCK_ID_GENERATOR", 120L, TimeUnit.SECONDS);
            if (!result) {
               throw new RuntimeException(macAddress + "设置分布式Id机器号失败");
            }

            ValueOperations<String, Object> stringOperation = this.redisRepository.opsForValue();
            boolean initWorkerId = Boolean.TRUE.equals(stringOperation.setIfAbsent("CACHE_WORKER_ID_MAXID", 1));
            if (!initWorkerId) {
               stringOperation.increment("CACHE_WORKER_ID_MAXID");
            }

            Integer workerId = (Integer)stringOperation.get("CACHE_WORKER_ID_MAXID");
            this.initWorkerId(workerId);
            this.redisRepository.opsForHash().put("CACHE_ID_IP", macAddress, workerId);
            LogUtils.info("配置分布式workerId {} - {}", new Object[]{macAddress, workerId});
         } catch (Exception e) {
            LogUtils.error(e);
         } finally {
            this.distributedLock.unlock();
         }

      }
   }

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }

   private void initWorkerId(Integer workerId) {
      if (Objects.nonNull(workerId)) {
      }

   }
}
