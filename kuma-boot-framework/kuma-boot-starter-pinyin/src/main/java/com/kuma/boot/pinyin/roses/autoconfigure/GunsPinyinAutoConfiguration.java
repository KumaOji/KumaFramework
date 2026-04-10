package com.kuma.boot.pinyin.roses.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.pinyin.roses.PinyinServiceImpl;
import com.kuma.boot.pinyin.roses.api.PinYinApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class GunsPinyinAutoConfiguration implements InitializingBean {
   public GunsPinyinAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(GunsPinyinAutoConfiguration.class, "kuma-boot-starter-pinyin", new String[0]);
   }

   @Bean
   @ConditionalOnMissingBean({PinYinApi.class})
   public PinYinApi pinYinApi() {
      return new PinyinServiceImpl();
   }
}
