package com.kuma.boot.logger.eden.logging.env;

import cn.hutool.core.text.CharSequenceUtil;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.logger.eden.IpConfigUtils;
import org.slf4j.MDC;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

public class BootstrapLogEnvironmentPostProcessor implements EnvironmentPostProcessor {
   public BootstrapLogEnvironmentPostProcessor() {
   }

   public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
      if (!environment.containsProperty("logging.bootstrap.enabled") || Boolean.parseBoolean(CharSequenceUtil.trimToEmpty(environment.getProperty("logging.bootstrap.enabled")))) {
         String appName = CharSequenceUtil.trimToEmpty(environment.getProperty("spring.application.name"));
         String profile = CharSequenceUtil.trimToEmpty(String.join(",", ContextUtils.getActiveProfile()));
         MDC.put("app", appName);
         MDC.put("profile", profile);
         MDC.put("localAddr", IpConfigUtils.getIpAddress());
      }

   }
}
