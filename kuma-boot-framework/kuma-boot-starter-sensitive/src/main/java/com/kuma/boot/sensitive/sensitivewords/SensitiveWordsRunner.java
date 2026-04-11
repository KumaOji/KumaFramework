package com.kuma.boot.sensitive.sensitivewords;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.List;
import java.util.Objects;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class SensitiveWordsRunner implements ApplicationRunner {
   private final RedisRepository redisRepository;

   public SensitiveWordsRunner(RedisRepository redisRepository) {
      this.redisRepository = redisRepository;
   }

   public void run(ApplicationArguments args) {
      Object words = this.redisRepository.get("SENSITIVE:WORDS:KEY");
      if (Objects.nonNull(words)) {
         LogUtils.info("\u7cfb\u7edf\u521d\u59cb\u5316\u654f\u611f\u8bcd", new Object[0]);
         List<String> sensitives = (List)words;
         if (sensitives.isEmpty()) {
            return;
         }

         SensitiveWordsFilter.init(sensitives);
      }

   }
}
