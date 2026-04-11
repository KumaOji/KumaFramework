package com.kuma.boot.xss.autoconfigure;

import cn.hutool.core.collection.CollUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.xss.autoconfigure.properties.XssProperties;
import com.kuma.boot.xss.filter.XssFilter;
import com.kuma.boot.xss.interceptor.XssCleanInterceptor;
import com.kuma.boot.xss.xsssupport.DefaultXssCleaner;
import com.kuma.boot.xss.xsssupport.FormXssClean;
import com.kuma.boot.xss.xsssupport.XssCleaner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
@EnableConfigurationProperties({XssProperties.class})
@ConditionalOnProperty(
   prefix = "kuma.boot.xss",
   name = {"enabled"},
   havingValue = "true"
)
public class XssAutoConfiguration implements WebMvcConfigurer, InitializingBean {
   private final XssProperties xssProperties;

   public void afterPropertiesSet() throws Exception {
      LogUtils.started(XssAutoConfiguration.class, "kuma-boot-starter-xss", new String[0]);
   }

   public XssAutoConfiguration(XssProperties xssProperties) {
      this.xssProperties = xssProperties;
   }

   @Bean
   public XssCleaner xssCleaner(XssProperties properties) {
      return new DefaultXssCleaner(properties);
   }

   @Bean
   public FormXssClean formXssClean(XssProperties properties, XssCleaner xssCleaner) {
      return new FormXssClean(properties, xssCleaner);
   }

   @Bean
   public JsonMapperBuilderCustomizer xssJacksonCustomizer(XssProperties properties, XssCleaner xssCleaner) {
      return (builder) -> {
      };
   }

   public void addInterceptors(InterceptorRegistry registry) {
      List<String> patterns = this.xssProperties.getPathPatterns();
      if (patterns.isEmpty()) {
         patterns.add("/**");
      }

      XssCleanInterceptor interceptor = new XssCleanInterceptor(this.xssProperties);
      registry.addInterceptor(interceptor).addPathPatterns(patterns).excludePathPatterns(this.xssProperties.getPathExcludePatterns()).order(Integer.MAX_VALUE);
   }

   @Bean
   @ConditionalOnProperty(
      prefix = "kuma.boot.xss",
      name = {"enabled"},
      havingValue = "true",
      matchIfMissing = true
   )
   public FilterRegistrationBean<XssFilter> xssFilterFilterRegistrationBean() {
      FilterRegistrationBean<XssFilter> filterRegistration = new FilterRegistrationBean();
      filterRegistration.setFilter(new XssFilter());
      filterRegistration.setEnabled(this.xssProperties.getEnabled());
      filterRegistration.addUrlPatterns((String[])this.xssProperties.getPatterns().toArray(new String[0]));
      filterRegistration.setOrder(this.xssProperties.getOrder());
      Map<String, String> initParameters = new HashMap(4);
      initParameters.put("ignorePath", CollUtil.join(this.xssProperties.getIgnorePaths(), ","));
      initParameters.put("ignoreParamValue", CollUtil.join(this.xssProperties.getIgnoreParamValues(), ","));
      filterRegistration.setInitParameters(initParameters);
      return filterRegistration;
   }
}
