package com.kuma.boot.data.jpa.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@AutoConfiguration
public class FunctionDescriptorConfiguration implements ApplicationListener<ContextRefreshedEvent> {
   public FunctionDescriptorConfiguration() {
   }

   public void onApplicationEvent(ContextRefreshedEvent event) {
   }
}
