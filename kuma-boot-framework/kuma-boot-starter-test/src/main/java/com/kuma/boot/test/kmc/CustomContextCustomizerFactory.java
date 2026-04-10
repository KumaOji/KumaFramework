package com.kuma.boot.test.kmc;

import java.util.List;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

public class CustomContextCustomizerFactory implements ContextCustomizerFactory {
   public CustomContextCustomizerFactory() {
   }

   public ContextCustomizer createContextCustomizer(Class<?> testClass, List<ContextConfigurationAttributes> configAttributes) {
      return null;
   }
}
