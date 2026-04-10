package com.kuma.boot.skywalking.config;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.skywalking.ClusterTrace;
import com.kuma.boot.skywalking.interceptor.TraceInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ConditionalOnWebApplication
public class TraceInterceptorConfigurer implements WebMvcConfigurer {
   private final ClusterTrace clusterTrace;

   public TraceInterceptorConfigurer(ClusterTrace clusterTrace) {
      this.clusterTrace = clusterTrace;
   }

   public void addInterceptors(InterceptorRegistry registry) {
      LogUtils.info("Enabled SkyWalking Tracing...", new Object[0]);
      registry.addInterceptor(new TraceInterceptor(this.clusterTrace)).addPathPatterns(new String[]{"/**"}).order(1);
   }
}
