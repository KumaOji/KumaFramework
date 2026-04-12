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

package com.kuma.boot.sensitive.sensitivewords;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.constant.RedisConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;
import java.util.Objects;

/** 敏感词加载 */
public class SensitiveWordsRunner implements ApplicationRunner {

   private final  RedisRepository redisRepository;

   public SensitiveWordsRunner(RedisRepository redisRepository) {
      this.redisRepository = redisRepository;
   }

   /**
    * 程序启动时，获取最新的需要过滤的敏感词
    *
    * <p>这里即便缓存中为空也没关系，定时任务会定时重新加载敏感词
    *
    * @param args 启动参数
    */
   @Override
   @SuppressWarnings("unchecked")
   public void run(ApplicationArguments args) {
      Object words = redisRepository.get(RedisConstants.SENSITIVE_WORDS_KEY);
      if (Objects.nonNull(words)) {
         LogUtils.info("系统初始化敏感词");

         List<String> sensitives = (List<String>) words;
         if (sensitives.isEmpty()) {
            return;
         }
         SensitiveWordsFilter.init(sensitives);
      }
   }
}
