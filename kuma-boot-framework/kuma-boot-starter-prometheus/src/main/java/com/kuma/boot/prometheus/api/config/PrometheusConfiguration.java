package com.kuma.boot.prometheus.api.config;

import com.kuma.boot.prometheus.api.core.PrometheusApi;
import com.kuma.boot.prometheus.api.core.ReactivePrometheusApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.ConditionalOnReactiveDiscoveryEnabled;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.context.annotation.Import;

@AutoConfiguration
public class PrometheusConfiguration {
   @AutoConfiguration
   @ConditionalOnBean({DiscoveryClient.class})
   @ConditionalOnDiscoveryEnabled
   @ConditionalOnProperty({"spring.cloud.discovery.blocking.enabled"})
   @Import({PrometheusApi.class})
   public static class PrometheusApiConfiguration {
   }

   @AutoConfiguration
   @ConditionalOnBean({ReactiveDiscoveryClient.class})
   @ConditionalOnDiscoveryEnabled
   @ConditionalOnReactiveDiscoveryEnabled
   @Import({ReactivePrometheusApi.class})
   public static class ReactivePrometheusApiConfiguration {
   }
}
