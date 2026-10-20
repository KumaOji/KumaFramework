package com.kuma.boot.logger.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.beans.factory.InitializingBean;

@AutoConfiguration
public class KlogAutoConfiguration implements InitializingBean {
   public KlogAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(KlogAutoConfiguration.class, "kuma-boot-starter-logger", new String[0]);
   }
}
