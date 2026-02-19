/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.validation.Validation
 *  jakarta.validation.Validator
 *  jakarta.validation.ValidatorFactory
 *  okhttp3.OkHttpClient$Builder
 *  org.hibernate.validator.HibernateValidator
 *  org.hibernate.validator.HibernateValidatorConfiguration
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.cloud.client.loadbalancer.LoadBalanced
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.converter.ConverterFactory
 *  org.springframework.core.task.AsyncTaskExecutor
 *  org.springframework.format.FormatterRegistry
 *  org.springframework.http.CacheControl
 *  org.springframework.http.converter.ByteArrayHttpMessageConverter
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.converter.HttpMessageConverters$ServerBuilder
 *  org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 *  org.springframework.web.context.request.async.CallableProcessingInterceptor
 *  org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
 *  org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor
 *  org.springframework.web.context.request.async.TimeoutDeferredResultProcessingInterceptor
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 *  org.springframework.web.method.support.HandlerMethodReturnValueHandler
 *  org.springframework.web.servlet.HandlerExceptionResolver
 *  org.springframework.web.servlet.HandlerInterceptor
 *  org.springframework.web.servlet.ViewResolver
 *  org.springframework.web.servlet.config.annotation.ApiVersionConfigurer
 *  org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer
 *  org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
 *  org.springframework.web.servlet.config.annotation.CorsRegistry
 *  org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer
 *  org.springframework.web.servlet.config.annotation.InterceptorRegistry
 *  org.springframework.web.servlet.config.annotation.PathMatchConfigurer
 *  org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
 *  org.springframework.web.servlet.config.annotation.ViewControllerRegistry
 *  org.springframework.web.servlet.config.annotation.ViewResolverRegistry
 *  org.springframework.web.servlet.config.annotation.WebMvcConfigurer
 *  org.springframework.web.servlet.view.InternalResourceViewResolver
 */
package com.kuma.boot.web.autoconfigure;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.aop.CountTimeAop;
import com.kuma.boot.web.autoconfigure.properties.WebMvcFilterProperties;
import com.kuma.boot.web.autoconfigure.properties.WebMvcInterceptorProperties;
import com.kuma.boot.web.mvc.converter.IntegerToEnumConverterFactory;
import com.kuma.boot.web.mvc.converter.String2DateConverter;
import com.kuma.boot.web.mvc.converter.String2LocalDateConverter;
import com.kuma.boot.web.mvc.converter.String2LocalDateTimeConverter;
import com.kuma.boot.web.mvc.converter.String2LocalTimeConverter;
import com.kuma.boot.web.mvc.converter.StringToEnumConverterFactory;
import com.kuma.boot.web.mvc.interceptor.DoubtApiInterceptor;
import com.kuma.boot.web.mvc.interceptor.HeaderThreadLocalInterceptor;
import com.kuma.boot.web.mvc.interceptor.TraceMdcInterceptor;
import com.kuma.boot.web.mvc.resolver.ActMethodArgumentResolver;
import com.kuma.boot.web.mvc.resolver.LoginUserArgumentResolver;
import com.kuma.boot.web.support.listener.RequestMappingScanListener;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.Duration;
import java.util.List;
import okhttp3.OkHttpClient;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutDeferredResultProcessingInterceptor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@AutoConfiguration
@EnableConfigurationProperties(value={WebMvcFilterProperties.class, WebMvcInterceptorProperties.class})
@Import(value={CountTimeAop.class, TraceMdcInterceptor.class})
public class WebMvcAutoConfiguration
implements WebMvcConfigurer,
InitializingBean {
    @Autowired
    @Qualifier(value="asyncThreadPoolTaskExecutor")
    private ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor;
    @Autowired
    private WebMvcInterceptorProperties interceptorProperties;
    @Autowired
    private TraceMdcInterceptor traceMdcInterceptor;

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(WebMvcAutoConfiguration.class, (String)"kuma-boot-starter-web", (String[])new String[0]);
    }

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOriginPatterns(new String[]{"*"}).allowedMethods(new String[]{"GET", "POST", "PUT", "DELETE", "OPTIONS"}).allowedHeaders(new String[]{"*"}).exposedHeaders(new String[]{"*"}).allowCredentials(true).maxAge(3600L);
        super.addCorsMappings(registry);
    }

    public void configurePathMatch(PathMatchConfigurer configurer) {
        super.configurePathMatch(configurer);
    }

    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        super.configureDefaultServletHandling(configurer);
    }

    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        super.configureContentNegotiation(configurer);
    }

    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.registerCallableInterceptors(new CallableProcessingInterceptor[]{new TimeoutCallableProcessingInterceptor()});
        configurer.registerDeferredResultInterceptors(new DeferredResultProcessingInterceptor[]{new TimeoutDeferredResultProcessingInterceptor()});
        configurer.setDefaultTimeout(1000L);
        configurer.setTaskExecutor((AsyncTaskExecutor)this.asyncThreadPoolTaskExecutor);
    }

    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        super.configureApiVersioning(configurer);
    }

    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        super.configureHandlerExceptionResolvers(resolvers);
    }

    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new LoginUserArgumentResolver());
        argumentResolvers.add(new ActMethodArgumentResolver());
    }

    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        super.addReturnValueHandlers(handlers);
    }

    public void addInterceptors(InterceptorRegistry registry) {
        if (this.interceptorProperties.getHeader().booleanValue()) {
            registry.addInterceptor((HandlerInterceptor)new HeaderThreadLocalInterceptor()).addPathPatterns(new String[]{"/**"}).excludePathPatterns(new String[]{"/actuator/**"});
        }
        if (this.interceptorProperties.getDoubtApi().booleanValue()) {
            registry.addInterceptor((HandlerInterceptor)new DoubtApiInterceptor(this.interceptorProperties)).addPathPatterns(new String[]{"/**"}).excludePathPatterns(new String[]{"/actuator/**"});
        }
        registry.addInterceptor((HandlerInterceptor)this.traceMdcInterceptor).addPathPatterns(new String[]{"/**"}).excludePathPatterns(new String[]{"/actuator/**"});
    }

    public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
        builder.addCustomConverter((HttpMessageConverter)new ByteArrayHttpMessageConverter());
        builder.addCustomConverter((HttpMessageConverter)new JacksonJsonHttpMessageConverter());
    }

    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        super.extendHandlerExceptionResolvers(resolvers);
    }

    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory((ConverterFactory)new IntegerToEnumConverterFactory());
        registry.addConverterFactory((ConverterFactory)new StringToEnumConverterFactory());
        registry.addConverter((Converter)new String2DateConverter());
        registry.addConverter((Converter)new String2LocalDateConverter());
        registry.addConverter((Converter)new String2LocalDateTimeConverter());
        registry.addConverter((Converter)new String2LocalTimeConverter());
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(new String[]{"/**"}).addResourceLocations(new String[]{"classpath:/static/"}).setCacheControl(CacheControl.maxAge((Duration)Duration.ofSeconds(3600L)).cachePublic());
        registry.addResourceHandler(new String[]{"doc.html"}).addResourceLocations(new String[]{"classpath:/META-INF/resources/"}).setCacheControl(CacheControl.maxAge((Duration)Duration.ofSeconds(3600L)).cachePublic());
        registry.addResourceHandler(new String[]{"/webjars/**"}).addResourceLocations(new String[]{"classpath:/META-INF/resources/webjars/"}).setCacheControl(CacheControl.maxAge((Duration)Duration.ofSeconds(3600L)).cachePublic());
    }

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("index");
    }

    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/WEB-INF/jsp/");
        internalResourceViewResolver.setSuffix(".jsp");
        registry.viewResolver((ViewResolver)internalResourceViewResolver);
    }

    @Bean
    @LoadBalanced
    public OkHttpClient.Builder builder() {
        return new OkHttpClient.Builder();
    }

    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = ((HibernateValidatorConfiguration)((HibernateValidatorConfiguration)Validation.byProvider(HibernateValidator.class).configure()).failFast(true)).buildValidatorFactory();
        return validatorFactory.getValidator();
    }

    @Configuration
    @ConditionalOnClass(value={RedisRepository.class})
    public static class RequestMappingScanListenerConfiguration {
        @Bean
        @ConditionalOnBean(value={RedisRepository.class})
        public RequestMappingScanListener requestMappingScanListener(RedisRepository redisRepository) {
            return new RequestMappingScanListener(redisRepository);
        }
    }
}

