package com.kuma.boot.encrypt.crypto.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties({EncryptProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.crypto",
   name = {"enabled"},
   havingValue = "true",
   matchIfMissing = true
)
public class EncryptAutoConfiguration implements InitializingBean {
   public void afterPropertiesSet() throws Exception {
      LogUtils.started(EncryptAutoConfiguration.class, "kuma-boot-starter-encrypt", new String[0]);
   }
}
