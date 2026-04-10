package com.kuma.boot.logger.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;

public class TlogAutoConfiguration implements InitializingBean {
   public TlogAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(TlogAutoConfiguration.class, "kuma-boot-starter-logger", new String[0]);
   }
}
