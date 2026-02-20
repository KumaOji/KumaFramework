/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.web.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.autoconfigure.properties.WebMvcFilterProperties;
import com.kuma.boot.web.servlet.filter.TenantFilter;
import com.kuma.boot.web.servlet.filter.TraceFilter;
import com.kuma.boot.web.servlet.filter.VersionFilter;
import com.kuma.boot.web.servlet.filter.WebContextFilter;
import com.kuma.boot.web.servlet.listener.TtcServletContextListener;
import com.kuma.boot.web.servlet.servlet.TtcAsyncServlet;
import com.kuma.boot.web.servlet.servlet.TtcServlet;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.List;

/**
 * Servlet 自动配置
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 22:13:50
 */
@AutoConfiguration
public class ServletAutoConfiguration implements WebApplicationInitializer, InitializingBean {

    private final WebMvcFilterProperties filterProperties;

    public ServletAutoConfiguration( WebMvcFilterProperties filterProperties) {
        this.filterProperties = filterProperties;
    }

    @Autowired(required = false)
    private Tracer tracer;

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(ServletAutoConfiguration.class, StarterNameConstants.WEB_STARTER);
    }

    //	@Component
    //	public class MyWebServerFactoryCustomizer implements
    // WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    //
    //		@Override
    //		public void customize(ConfigurableServletWebServerFactory server) {
    //		}
    //
    //	}
    //	@Component
    //	public class MyTomcatWebServerCustomizer implements
    // WebServerFactoryCustomizer<UndertowServletWebServerFactory> {
    //
    //		@Override
    //		public void customize(UndertowServletWebServerFactory factory) {
    //			// customize the factory here
    //		}
    //	}

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        LogUtils.info("servletContext.getServerInfo=== {}", servletContext.getServerInfo());

        // 注册servlet
        //		ServletRegistration.Dynamic myServlet = servletContext
        //			.addServlet("myServlet", MyServlet.class);
        //		myServlet.addMapping("/myServlet");
        //		myServlet.setLoadOnStartup(0);

        // 注册filter
        //		Dynamic lbIsolationFilter = servletContext
        //			.addFilter("lbIsolationFilter", LbIsolationFilter.class);
        //		lbIsolationFilter.addMappingForUrlPatterns(
        //			EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC), false, "/*");
        //
        //		Dynamic myFilter = servletContext.addFilter("myFilter", TenantFilter.class);
        //		myFilter.addMappingForUrlPatterns(
        //			EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC), false, "/*");
        //
        //		Dynamic traceFilter = servletContext.addFilter("traceFilter", TraceFilter.class);
        //		traceFilter.addMappingForUrlPatterns(
        //			EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC), false, "/*");
    }

    // 注册 Servlet 到容器中
    @Bean
    public ServletRegistrationBean<TtcServlet> ttcServletRegistrationBean() {
        ServletRegistrationBean<TtcServlet> servletRegistrationBean =
                new ServletRegistrationBean<>();
        servletRegistrationBean.setServlet(new TtcServlet());
        servletRegistrationBean.setLoadOnStartup(1);
        servletRegistrationBean.setAsyncSupported(true);
        servletRegistrationBean.setUrlMappings(List.of("/ttcServlet"));
        LogUtils.info("注册ttcServlet成功，名称: {}", TtcServlet.class.getName());
        return servletRegistrationBean;
    }

    @Bean
    public ServletRegistrationBean<TtcAsyncServlet> ttcAsyncServletRegistrationBean() {
        ServletRegistrationBean<TtcAsyncServlet> servletRegistrationBean =
                new ServletRegistrationBean<>();
        servletRegistrationBean.setServlet(new TtcAsyncServlet());
        servletRegistrationBean.setLoadOnStartup(1);
        servletRegistrationBean.setAsyncSupported(true);
        servletRegistrationBean.setUrlMappings(List.of("/ttcAsyncServlet"));
        LogUtils.info("注册ttcAsyncServlet成功，名称: {}", TtcAsyncServlet.class.getName());
        return servletRegistrationBean;
    }

    @Bean
    public ServletListenerRegistrationBean<TtcServletContextListener>
    ttcServletListenerRegistrationBean() {
        ServletListenerRegistrationBean<TtcServletContextListener> bean =
                new ServletListenerRegistrationBean<>();
        bean.setListener(new TtcServletContextListener());
        return bean;
    }

    @Bean
    @ConditionalOnProperty(prefix = WebMvcFilterProperties.PREFIX, name = "version", havingValue = "true")
    public FilterRegistrationBean<VersionFilter> versionFilterFilterRegistrationBean() {
        FilterRegistrationBean<VersionFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new VersionFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName(VersionFilter.class.getName());
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 6);
        return registrationBean;
    }

    @Bean
    @ConditionalOnProperty(prefix = WebMvcFilterProperties.PREFIX, name = "tenant", havingValue = "true")
    public FilterRegistrationBean<TenantFilter> tenantFilterFilterRegistrationBean() {
        FilterRegistrationBean<TenantFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TenantFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName(TenantFilter.class.getName());
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 5);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilterFilterRegistrationBean() {
        FilterRegistrationBean<TraceFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TraceFilter(filterProperties, tracer));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName(TraceFilter.class.getName());
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 4);
        return registrationBean;
    }

    @Bean
    @ConditionalOnProperty(
            prefix = WebMvcFilterProperties.PREFIX,
            name = "webContext",
            havingValue = "true")
    public FilterRegistrationBean<WebContextFilter> webContextFilterFilterRegistrationBean() {
        FilterRegistrationBean<WebContextFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new WebContextFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName(WebContextFilter.class.getName());
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 3);
        return registrationBean;
    }

    // . 记录请求数据
    // Spring Boot 为日志有效载荷提供了内置解决方案。
    // AbstractRequestLoggingFilter 是一个提供日志记录基本功能的过滤器。子类应覆盖 beforeRequest() 和 afterRequest() 方法，
    // 以围绕请求执行实际日志记录。
    // Spring Boot 框架提供了三个具体实现类，
    // 我们可以用它们来记录传入的请求：
    // CommonsRequestLoggingFilter
    // Log4jNestedDiagnosticContextFilter(过时，弃用)
    // ServletContextRequestLoggingFilter
    //@Bean
    //public CommonsRequestLoggingFilter commonsRequestLoggingFilter() {
    //    // 是否记录请求的查询参数信息
    //    CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
    //    // 是否记录请求body内容
    //    filter.setIncludeQueryString(true);
    //    // 是否记录请求header信息
    //    filter.setIncludePayload(true);
    //    // 是否记录请求客户端
    //    filter.setIncludeHeaders(true);
    //    // 设置日期记录的前缀
    //    filter.setIncludeClientInfo(true);
    //    filter.setAfterMessagePrefix("REQUEST DATA: ");
    //    return filter;
    //}
}
