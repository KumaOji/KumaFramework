package com.kuma.boot.prometheus.interceptor;

import com.kuma.boot.prometheus.collector.PrometheusCollector;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class PrometheusMetricsInterceptor implements HandlerInterceptor {
   private final PrometheusCollector prometheusCollector;

   public PrometheusMetricsInterceptor(PrometheusCollector prometheusCollector) {
      this.prometheusCollector = prometheusCollector;
   }

   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      String requestURI = request.getRequestURI();
      String method = request.getMethod();
      int status = response.getStatus();
      return true;
   }

   public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
      String requestURI = request.getRequestURI();
      String method = request.getMethod();
      int status = response.getStatus();
   }
}
