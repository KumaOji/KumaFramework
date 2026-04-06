package com.kuma.boot.seata.spi.spring;

import javax.annotation.Nullable;
import org.apache.seata.common.loader.LoadLevel;
import org.apache.seata.spring.annotation.ScannerChecker;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

@LoadLevel(
   name = "KmcConfigBeans",
   order = 150
)
public class KmcConfigBeansScannerChecker implements ScannerChecker {
   public boolean check(Object bean, String beanName, @Nullable ConfigurableListableBeanFactory beanFactory) throws Exception {
      return true;
   }
}
