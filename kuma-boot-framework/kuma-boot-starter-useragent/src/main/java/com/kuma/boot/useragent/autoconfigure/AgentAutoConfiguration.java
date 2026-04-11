package com.kuma.boot.useragent.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.useragent.reactive.ReactiveUserAgentFilter;
import com.kuma.boot.useragent.reactive.ReactiveUserAgentResolver;
import com.kuma.boot.useragent.servlet.UserAgentFilter;
import com.kuma.boot.useragent.servlet.UserAgentResolver;
import com.kuma.boot.useragent.support.UserAgentConverter;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
@ConditionalOnBean({UserAgentConverter.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.useragent",
   name = {"enabled"},
   havingValue = "true"
)
public class AgentAutoConfiguration implements InitializingBean {
   public AgentAutoConfiguration() {
   }

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(AgentAutoConfiguration.class, "kuma-boot-starter-useragent", new String[0]);
   }

   @AutoConfiguration
   @ConditionalOnWebApplication(
      type = Type.SERVLET
   )
   public static class UserAgentMvcAutoConfiguration implements WebMvcConfigurer {
      public UserAgentMvcAutoConfiguration() {
      }

      @Bean
      public FilterRegistrationBean<UserAgentFilter> filterRegistrationBean() {
         FilterRegistrationBean<UserAgentFilter> registrationBean = new FilterRegistrationBean();
         registrationBean.setFilter(new UserAgentFilter());
         registrationBean.addUrlPatterns(new String[]{"/*"});
         registrationBean.setName("userAgentFilter");
         registrationBean.setOrder(1);
         return registrationBean;
      }

      public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
         resolvers.add(new UserAgentResolver());
      }
   }

   @AutoConfiguration
   @ConditionalOnWebApplication(
      type = Type.REACTIVE
   )
   public static class UserAgentReactiveAutoConfiguration implements WebFluxConfigurer {
      public UserAgentReactiveAutoConfiguration() {
      }

      @Bean
      public ReactiveUserAgentFilter reactiveUserAgentFilter() {
         return new ReactiveUserAgentFilter();
      }

      public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
         configurer.addCustomResolver(new org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver[]{new ReactiveUserAgentResolver()});
      }
   }
}
