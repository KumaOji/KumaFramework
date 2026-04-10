package com.kuma.boot.logger.logging.config;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

public class LoggingInitializer implements EnvironmentPostProcessor, Ordered {
   public LoggingInitializer() {
   }

   public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
      String logBase = environment.getProperty("logging.file.path", "logs");
      if (!environment.containsProperty("logging.file.name")) {
      }

   }

   public int getOrder() {
      return Integer.MAX_VALUE;
   }
}
