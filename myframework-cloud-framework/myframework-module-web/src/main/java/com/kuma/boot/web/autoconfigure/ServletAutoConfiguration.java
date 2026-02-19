/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  io.micrometer.tracing.Tracer
 *  jakarta.servlet.Filter
 *  jakarta.servlet.Servlet
 *  jakarta.servlet.ServletContext
 *  jakarta.servlet.ServletException
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.web.servlet.FilterRegistrationBean
 *  org.springframework.boot.web.servlet.ServletListenerRegistrationBean
 *  org.springframework.boot.web.servlet.ServletRegistrationBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.web.WebApplicationInitializer
 */
package com.kuma.boot.web.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.autoconfigure.properties.WebMvcFilterProperties;
import com.kuma.boot.web.servlet.filter.TenantFilter;
import com.kuma.boot.web.servlet.filter.TraceFilter;
import com.kuma.boot.web.servlet.filter.VersionFilter;
import com.kuma.boot.web.servlet.filter.WebContextFilter;
import com.kuma.boot.web.servlet.listener.KmcServletContextListener;
import com.kuma.boot.web.servlet.servlet.KmcAsyncServlet;
import com.kuma.boot.web.servlet.servlet.KmcServlet;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.util.EventListener;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.WebApplicationInitializer;

@AutoConfiguration
public class ServletAutoConfiguration
implements WebApplicationInitializer,
InitializingBean {
    private final WebMvcFilterProperties filterProperties;
    @Autowired(required=false)
    private Tracer tracer;

    public ServletAutoConfiguration(WebMvcFilterProperties filterProperties) {
        this.filterProperties = filterProperties;
    }

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(ServletAutoConfiguration.class, (String)"kuma-boot-starter-web", (String[])new String[0]);
    }

    public void onStartup(ServletContext servletContext) throws ServletException {
        LogUtils.info((String)"servletContext.getServerInfo=== {}", (Object[])new Object[]{servletContext.getServerInfo()});
    }

    @Bean
    public ServletRegistrationBean<KmcServlet> kmcServletRegistrationBean() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        servletRegistrationBean.setServlet((Servlet)new KmcServlet());
        servletRegistrationBean.setLoadOnStartup(1);
        servletRegistrationBean.setAsyncSupported(true);
        servletRegistrationBean.setUrlMappings(List.of("/kmcServlet"));
        LogUtils.info((String)"\u6ce8\u518ckmcServlet\u6210\u529f\uff0c\u540d\u79f0: {}", (Object[])new Object[]{KmcServlet.class.getName()});
        return servletRegistrationBean;
    }

    @Bean
    public ServletRegistrationBean<KmcAsyncServlet> kmcAsyncServletRegistrationBean() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        servletRegistrationBean.setServlet((Servlet)new KmcAsyncServlet());
        servletRegistrationBean.setLoadOnStartup(1);
        servletRegistrationBean.setAsyncSupported(true);
        servletRegistrationBean.setUrlMappings(List.of("/kmcAsyncServlet"));
        LogUtils.info((String)"\u6ce8\u518ckmcAsyncServlet\u6210\u529f\uff0c\u540d\u79f0: {}", (Object[])new Object[]{KmcAsyncServlet.class.getName()});
        return servletRegistrationBean;
    }

    @Bean
    public ServletListenerRegistrationBean<KmcServletContextListener> kmcServletListenerRegistrationBean() {
        ServletListenerRegistrationBean bean = new ServletListenerRegistrationBean();
        bean.setListener((EventListener)((Object)new KmcServletContextListener()));
        return bean;
    }

    @Bean
    @ConditionalOnProperty(prefix="kuma.boot.web.filter", name={"version"}, havingValue="true")
    public FilterRegistrationBean<VersionFilter> versionFilterFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter((Filter)new VersionFilter());
        registrationBean.addUrlPatterns(new String[]{"/*"});
        registrationBean.setName(VersionFilter.class.getName());
        registrationBean.setOrder(-2147483642);
        return registrationBean;
    }

    @Bean
    @ConditionalOnProperty(prefix="kuma.boot.web.filter", name={"tenant"}, havingValue="true")
    public FilterRegistrationBean<TenantFilter> tenantFilterFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter((Filter)new TenantFilter());
        registrationBean.addUrlPatterns(new String[]{"/*"});
        registrationBean.setName(TenantFilter.class.getName());
        registrationBean.setOrder(-2147483643);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilterFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter((Filter)new TraceFilter(this.filterProperties, this.tracer));
        registrationBean.addUrlPatterns(new String[]{"/*"});
        registrationBean.setName(TraceFilter.class.getName());
        registrationBean.setOrder(-2147483644);
        return registrationBean;
    }

    @Bean
    @ConditionalOnProperty(prefix="kuma.boot.web.filter", name={"webContext"}, havingValue="true")
    public FilterRegistrationBean<WebContextFilter> webContextFilterFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter((Filter)new WebContextFilter());
        registrationBean.addUrlPatterns(new String[]{"/*"});
        registrationBean.setName(WebContextFilter.class.getName());
        registrationBean.setOrder(-2147483645);
        return registrationBean;
    }
}

