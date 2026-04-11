package com.kuma.boot.xss.xssorigin.config;

import com.kuma.boot.xss.xssorigin.core.filter.XssFilter;
import com.kuma.boot.xss.xssorigin.core.propertie.XssProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.PathMatcher;

@EnableConfigurationProperties({XssProperties.class})
@AutoConfiguration
public class XssAutoConfig {
   public XssAutoConfig() {
   }

   @Bean
   public FilterRegistrationBean<XssFilter> xssFilter(XssProperties properties, PathMatcher pathMatcher) {
      FilterRegistrationBean<XssFilter> registrationBean = new FilterRegistrationBean();
      registrationBean.setFilter(new XssFilter(properties, pathMatcher));
      registrationBean.setOrder(-105);
      return registrationBean;
   }
}
