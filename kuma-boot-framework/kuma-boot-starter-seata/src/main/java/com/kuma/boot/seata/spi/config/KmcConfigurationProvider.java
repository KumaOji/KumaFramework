package com.kuma.boot.seata.spi.config;

import org.apache.seata.common.loader.LoadLevel;
import org.apache.seata.config.Configuration;
import org.apache.seata.config.ConfigurationProvider;

@LoadLevel(
   name = "Apollo",
   order = 1
)
public class KmcConfigurationProvider implements ConfigurationProvider {
   public Configuration provide() {
      return KmcConfiguration.getInstance();
   }
}
