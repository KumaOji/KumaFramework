package com.kuma.boot.seata.spi.config;

import java.util.HashSet;
import java.util.Set;
import org.apache.seata.common.exception.NotSupportYetException;
import org.apache.seata.config.AbstractConfiguration;
import org.apache.seata.config.ConfigurationChangeListener;

public class KmcConfiguration extends AbstractConfiguration {
   private static volatile KmcConfiguration instance;

   private KmcConfiguration() {
   }

   public static KmcConfiguration getInstance() {
      if (instance == null) {
         synchronized(KmcConfiguration.class) {
            if (instance == null) {
               instance = new KmcConfiguration();
            }
         }
      }

      return instance;
   }

   public String getLatestConfig(String dataId, String defaultValue, long timeoutMills) {
      return "";
   }

   public boolean putConfig(String dataId, String content, long timeoutMills) {
      throw new NotSupportYetException("not support putConfig");
   }

   public boolean putConfigIfAbsent(String dataId, String content, long timeoutMills) {
      throw new NotSupportYetException("not support atomic operation putConfigIfAbsent");
   }

   public boolean removeConfig(String dataId, long timeoutMills) {
      throw new NotSupportYetException("not support removeConfig");
   }

   public void addConfigListener(String dataId, ConfigurationChangeListener listener) {
   }

   public void removeConfigListener(String dataId, ConfigurationChangeListener listener) {
   }

   public Set<ConfigurationChangeListener> getConfigListeners(String dataId) {
      return new HashSet<>();
   }

   public String getTypeName() {
      return "kmc";
   }
}
