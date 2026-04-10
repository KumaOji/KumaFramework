package com.kuma.boot.job.xxl.executor.config;

import com.kuma.boot.job.xxl.autoconfigure.XxlJobAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration(
   after = {XxlJobAutoConfiguration.class}
)
@ComponentScan(
   basePackages = {"com.kuma.boot.job.xxl.executor"}
)
@ConditionalOnProperty(
   prefix = "kuma.boot.job.xxl",
   name = {"autoRegister"},
   havingValue = "true"
)
public class XxlJobAutoRegisterConfiguration {
   public XxlJobAutoRegisterConfiguration() {
   }
}
