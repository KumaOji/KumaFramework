package com.kuma.boot.job.xxl.autoconfigure;

import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(
   before = {XxlJobAutoConfiguration.class}
)
@ConditionalOnClass({DiscoveryClient.class})
@ConditionalOnBean({DiscoveryClient.class})
public class XxlJobCloudAutoConfiguration {
   private static final String TAO_TAO_CLOUD_XXL_JOB_ADMIN = "kuma-cloud-xxljob";

   public XxlJobCloudAutoConfiguration() {
   }

   @Bean
   public XxlJobServerList xxlJobServerList(final DiscoveryClient discoveryClient) {
      return new XxlJobServerList() {
         {
            Objects.requireNonNull(XxlJobCloudAutoConfiguration.this);
         }

         public String getXxlJobServerList() {
            return (String)discoveryClient.getServices().stream().filter((s) -> s.contains("kuma-cloud-xxljob")).flatMap((s) -> discoveryClient.getInstances(s).stream()).map((instance) -> String.format("http://%s:%s", instance.getHost(), instance.getPort())).collect(Collectors.joining(","));
         }
      };
   }
}
