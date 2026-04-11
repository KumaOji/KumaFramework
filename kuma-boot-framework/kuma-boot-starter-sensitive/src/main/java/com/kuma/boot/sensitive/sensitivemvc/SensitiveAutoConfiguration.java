package com.kuma.boot.sensitive.sensitivemvc;

import com.kuma.boot.sensitive.sensitivemvc.resolve.RequestMappingResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@Import({FastJsonAutoConfiguration.class, FastJson2AutoConfiguration.class})
public class SensitiveAutoConfiguration {
   public SensitiveAutoConfiguration() {
   }

   @Bean
   @ConditionalOnMissingBean
   public RequestMappingResolver handlerMethodServletParser(@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping requestMappingHandlerMapping) {
      return new RequestMappingResolver(requestMappingHandlerMapping);
   }
}
