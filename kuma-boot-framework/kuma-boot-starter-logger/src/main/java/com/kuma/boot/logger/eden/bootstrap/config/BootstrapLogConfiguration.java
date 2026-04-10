package com.kuma.boot.logger.eden.bootstrap.config;

import com.kuma.boot.logger.eden.bootstrap.filter.BootstrapLogHttpFilter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.Environment;

@Role(2)
@Configuration(
   proxyBeanMethods = false
)
public class BootstrapLogConfiguration {
   public BootstrapLogConfiguration() {
   }

   @Bean
   public BootstrapLogHttpFilter logHttpFilter(Environment environment, ObjectProvider<BootstrapLogConfig> bootstrapLogConfigs) {
      BootstrapLogConfig config = (BootstrapLogConfig)bootstrapLogConfigs.getIfUnique(BootstrapLogConfig::new);
      BootstrapLogHttpFilter httpFilter = new BootstrapLogHttpFilter(environment);
      httpFilter.setEnabledMdc(config.isEnabledMdc());
      return httpFilter;
   }

   @WebFilter(
      filterName = "bootstrapLogHttpFilter",
      urlPatterns = {"/*"}
   )
   public static class CustomFilterRegistration implements Filter {
      @Autowired
      private BootstrapLogHttpFilter bootstrapLogHttpFilter;

      public CustomFilterRegistration() {
      }

      public void init(FilterConfig filterConfig) throws ServletException {
         this.bootstrapLogHttpFilter.init(filterConfig);
      }

      public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
         this.bootstrapLogHttpFilter.doFilter(request, response, chain);
      }

      public void destroy() {
         this.bootstrapLogHttpFilter.destroy();
      }
   }
}
