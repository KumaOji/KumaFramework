package com.kuma.boot.prometheus.autoconfigure;

import com.kuma.boot.prometheus.autoconfigure.properties.PrometheusProperties;
import com.kuma.boot.prometheus.collector.PrometheusCollector;
import com.kuma.boot.prometheus.interceptor.PrometheusMetricsInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableConfigurationProperties({PrometheusProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.prometheus",
   name = {"enabled"},
   havingValue = "true"
)
@ConditionalOnBean({PrometheusCollector.class})
@ConditionalOnWebApplication(
   type = Type.SERVLET
)
@AutoConfiguration(
   after = {PrometheusAutoConfiguration.class}
)
public class PrometheusInterceptorAutoConfiguration implements WebMvcConfigurer {
   private final PrometheusCollector prometheusCollector;

   public PrometheusInterceptorAutoConfiguration(PrometheusCollector prometheusCollector) {
      this.prometheusCollector = prometheusCollector;
   }

   public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(new PrometheusMetricsInterceptor(this.prometheusCollector)).addPathPatterns(new String[]{"/**"}).excludePathPatterns(new String[]{"/actuator/**"});
   }
}
