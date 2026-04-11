package com.kuma.boot.idgenerator.generator;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.support.thread.ThreadFactoryCreator;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.redisson.Redisson;
import org.redisson.spring.data.connection.RedissonConnection;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

public class RedisIdGenerator implements DisposableBean, CommandLineRunner {
   private static final long CACHE_TIMEOUT = 86400L;
   private static final long SCHEDULE_TIMEOUT = 1800L;
   private static final byte WORKER_ID_BIT_LENGTH = 16;
   private static final int MAX_WORKER_ID_NUMBER_BY_MODE = 65535;
   private short workerId = -1;
   private String cacheKey;
   private RedisRepository redisRepository;
   private final Runnable resetExpire = () -> {
      if (Objects.nonNull(this.redisRepository)) {
         this.redisRepository.opsForValue().set(this.cacheKey, System.currentTimeMillis(), 86400L, TimeUnit.SECONDS);
      }

   };

   public RedisIdGenerator(RedisRepository redisRepository) {
      this.redisRepository = redisRepository;
   }

   public void run(String... args) throws Exception {
      this.initIdWorker();
   }

   private void initIdWorker() {
      if (Objects.isNull(this.redisRepository)) {
         this.redisRepository = (RedisRepository)ContextUtils.getBean(RedisRepository.class, true);
      }

      RedisAtomicLong redisAtomicLong = new RedisAtomicLong("ID:GENERATOR:INDEX:COUNTER", this.redisRepository.getConnectionFactory());

      for(int i = 0; i <= 65535; ++i) {
         long andInc = redisAtomicLong.getAndIncrement();
         long result = andInc % 65536L;
         if (andInc >= 65535L) {
            redisAtomicLong.set(andInc % 65535L);
         }

         this.cacheKey = "ID:GENERATOR:INDEX:" + result;
         boolean useSuccess = Boolean.TRUE.equals(this.redisRepository.opsForValue().setIfAbsent(this.cacheKey, System.currentTimeMillis(), 86400L, TimeUnit.SECONDS));
         if (useSuccess) {
            this.workerId = (short)((int)result);
            break;
         }
      }

      if (this.workerId == -1) {
         throw new RuntimeException(String.format("\u5df2\u5c1d\u8bd5\u751f\u6210%d\u4e2aID\u751f\u6210\u5668\u7f16\u53f7, \u65e0\u6cd5\u83b7\u53d6\u5230\u53ef\u7528\u7f16\u53f7", 65536));
      } else {
         LogUtils.info("\u5f53\u524dID\u751f\u6210\u5668\u7f16\u53f7: " + this.workerId, new Object[0]);
         ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, ThreadFactoryCreator.create("kmc-idgenerator-scheduled-task"));
         scheduledThreadPoolExecutor.scheduleWithFixedDelay(this.resetExpire, 1800L, 1800L, TimeUnit.SECONDS);
      }
   }

   public void destroy() throws Exception {
      if (Objects.nonNull(this.redisRepository) && StrUtil.isNotBlank(this.cacheKey)) {
         try {
            if (!this.redisRepository.isClosed()) {
               RedisConnection connection = this.redisRepository.getConnectionFactory().getConnection();
               if (connection instanceof RedissonConnection) {
                  RedissonConnection redissonConnection = (RedissonConnection)connection;
                  Redisson redisson = (Redisson)redissonConnection.getNativeConnection();
                  if (!redisson.getServiceManager().isShutdown()) {
                     this.redisRepository.del(new String[]{this.cacheKey});
                  }
               } else {
                  this.redisRepository.del(new String[]{this.cacheKey});
               }
            }
         } catch (Exception e) {
            LogUtils.error("\u5220\u9664\u5f53\u524dID\u751f\u6210\u5668\u7f16\u53f7\u5f02\u5e38", new Object[]{e});
         }
      }

   }
}
