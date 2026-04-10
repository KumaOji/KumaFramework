package com.kuma.boot.elk.autoconfigure;

import com.kuma.boot.elk.aspect.WebControllerAspect;
import com.kuma.boot.elk.autoconfigure.properties.ElkHealthLogStatisticProperties;
import com.kuma.boot.elk.interceptor.ElkWebInterceptor;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
@ConditionalOnProperty(
   prefix = "kuma.boot.elk.web",
   name = {"enabled"},
   havingValue = "true"
)
public class ElkWebInterceptorAutoConfiguration implements WebMvcConfigurer {
   private final LogstashTcpSocketAppender logstashTcpSocketAppender;

   public ElkWebInterceptorAutoConfiguration(@Autowired(required = false) LogstashTcpSocketAppender logstashTcpSocketAppender) {
      this.logstashTcpSocketAppender = logstashTcpSocketAppender;
   }

   @Bean
   public ElkWebInterceptor elkWebInterceptor() {
      return new ElkWebInterceptor(this.logstashTcpSocketAppender);
   }

   @Bean
   @ConditionalOnClass(
      name = {"org.aspectj.lang.annotation.Aspect"}
   )
   @ConditionalOnProperty(
      prefix = "kuma.boot.elk.web.aspect",
      name = {"enabled"},
      havingValue = "true"
   )
   public WebControllerAspect webControllerAspect(ElkHealthLogStatisticProperties logStatisticProperties) {
      return new WebControllerAspect(logStatisticProperties);
   }

   public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(this.elkWebInterceptor());
   }
}
