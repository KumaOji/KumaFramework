package com.kuma.boot.elk.interceptor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.marker.MapEntriesAppendingMarker;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class ElkWebInterceptor implements HandlerInterceptor {
   private final ThreadLocal<Long> local = new ThreadLocal();
   private final LogstashTcpSocketAppender logstashTcpSocketAppender;

   public ElkWebInterceptor(LogstashTcpSocketAppender logstashTcpSocketAppender) {
      this.logstashTcpSocketAppender = logstashTcpSocketAppender;
   }

   public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
      if (this.logstashTcpSocketAppender != null && handler instanceof HandlerMethod) {
         this.local.set(System.currentTimeMillis());
      }

      return true;
   }

   public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
   }

   public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
      Long startTime = (Long)this.local.get();
      if (startTime != null) {
         this.local.remove();
         long costTime = System.currentTimeMillis() - startTime;
         String path = httpServletRequest.getRequestURI();
         HandlerMethod handler = (HandlerMethod)o;
         Map<String, Object> values = new HashMap();
         values.put("logger_type", "api");
         values.put("service", handler.getBeanType().getName());
         String var10002 = handler.getBeanType().getName();
         values.put("method", var10002 + "." + handler.getMethod().getName());
         values.put("path", httpServletRequest.getRequestURI());
         values.put("cost_time", costTime);
         values.put("result", e == null);
         values.put("result_message", e == null ? "success" : e.getClass().getName() + ": " + e.getMessage());
         this.logstashTcpSocketAppender.doAppend(this.createLoggerEvent(values, path + ": " + costTime));
      }
   }

   private LoggingEvent createLoggerEvent(Map<String, Object> values, String message) {
      LoggingEvent loggingEvent = new LoggingEvent();
      loggingEvent.setTimeStamp(System.currentTimeMillis());
      loggingEvent.setLevel(Level.INFO);
      loggingEvent.setLoggerName("ElkLogger");
      loggingEvent.addMarker(new MapEntriesAppendingMarker(values));
      loggingEvent.setMessage(message);
      loggingEvent.setArgumentArray(new String[0]);
      loggingEvent.setThreadName(Thread.currentThread().getName());
      return loggingEvent;
   }
}
