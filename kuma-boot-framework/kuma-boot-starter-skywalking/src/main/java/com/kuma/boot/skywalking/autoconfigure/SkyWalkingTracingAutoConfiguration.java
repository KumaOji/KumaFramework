package com.kuma.boot.skywalking.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.skywalking.ClusterTrace;
import com.kuma.boot.skywalking.SkyWalkingClusterTrace;
import com.kuma.boot.skywalking.autoconfigure.properties.SkywalkingProperties;
import com.kuma.boot.skywalking.config.TraceInterceptorConfigurer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({SkywalkingProperties.class})
public class SkyWalkingTracingAutoConfiguration implements InitializingBean {
   public SkyWalkingTracingAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(SkyWalkingTracingAutoConfiguration.class, "kuma-boot-starter-skywalking", new String[0]);
   }

   @PostConstruct
   public void init() {
      LogUtils.info("Load Auto Configuration : {}", new Object[]{this.getClass().getName()});
   }

   @Bean
   public ClusterTrace skyWalkingClusterTrace() {
      return new SkyWalkingClusterTrace();
   }

   @Bean
   public TraceInterceptorConfigurer traceInterceptorConfigurer(ClusterTrace clusterTrace) {
      return new TraceInterceptorConfigurer(clusterTrace);
   }
}
