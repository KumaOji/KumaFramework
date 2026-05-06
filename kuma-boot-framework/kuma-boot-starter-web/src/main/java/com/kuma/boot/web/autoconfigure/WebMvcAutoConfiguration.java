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

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.aop.CountTimeAop;
import com.kuma.boot.web.aot.WebAopRuntimeHintsRegistrar;
import com.kuma.boot.web.autoconfigure.properties.WebMvcFilterProperties;
import com.kuma.boot.web.autoconfigure.properties.WebMvcInterceptorProperties;
import com.kuma.boot.web.mvc.converter.*;
import com.kuma.boot.web.mvc.interceptor.DoubtApiInterceptor;
import com.kuma.boot.web.mvc.interceptor.HeaderThreadLocalInterceptor;
import com.kuma.boot.web.mvc.interceptor.TraceMdcInterceptor;
import com.kuma.boot.web.mvc.resolver.ActMethodArgumentResolver;
import com.kuma.boot.web.mvc.resolver.LoginUserArgumentResolver;
import com.kuma.boot.web.support.listener.RequestMappingScanListener;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import okhttp3.OkHttpClient;
import org.hibernate.validator.HibernateValidator;
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
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutDeferredResultProcessingInterceptor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.time.Duration;
import java.util.List;

/**
 * 自定义mvc 自动配置
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 21:30:20
 */
@AutoConfiguration
@ImportRuntimeHints(WebAopRuntimeHintsRegistrar.class)
@EnableConfigurationProperties({
        WebMvcFilterProperties.class,
        WebMvcInterceptorProperties.class,
})
@Import({CountTimeAop.class, TraceMdcInterceptor.class})
public class WebMvcAutoConfiguration implements WebMvcConfigurer, InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(WebMvcAutoConfiguration.class, StarterNameConstants.WEB_STARTER);
    }

    /**
     * 异步线程池任务执行人
     */
    @Autowired
    @Qualifier("asyncThreadPoolTaskExecutor")
    private ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor;

    /**
     * 拦截器属性
     */
    @Autowired private WebMvcInterceptorProperties interceptorProperties;

    @Autowired private TraceMdcInterceptor traceMdcInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                // 允许所有路径
                .addMapping("/**")
                //允许的请求路径 // 允许所有来源，生产环境应限制具体域名
                .allowedOriginPatterns("*")
                //限制允许的源：不要在生产环境使用"*"通配符，而应该指定具体的前端域名：
                //.setAllowedOrigins(Arrays.asList("https://www.example.com", "https://app.example.com"));
                // 允许的HTTP方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                //允许的请求头  // 允许所有请求头
                .allowedHeaders("*")
                //设置响应的头信息， 在其中可以设置其他的头信息，不进行配置时， 默认可以获取到Cache-Control、Content-Language、Content-Type、Expires、Last-Modified、Pragma字段
                .exposedHeaders("*")
                //谨慎使用credentials：如果设置了allowCredentials = true，allowedOrigins不能使用"*"通配符。
                //是否发送cookie，默认不发送   // 允许携带凭证(cookie)
                .allowCredentials(true)
                //配置预检请求的有效时间， 单位是秒，表示：在多长时间内，不需要发出第二次预检请求  预检请求结果缓存时间（秒）
                .maxAge(3600);
        // 跨域配置
        WebMvcConfigurer.super.addCorsMappings(registry);
    }

    // 路径匹配规则
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        //// 设置是否模糊匹配，默认真。例如/user是否匹配/user.*。如果真，也就是说"/user.html"的请求会被"/user"的Controller所拦截。
        // configurer.setUseSuffixPatternMatch(false);
        //// 设置是否自动后缀模式匹配，默认真。如/user是否匹配/user/。如果真，也就是说, "/user"和"/user/"都会匹配到"/user"的Controller。
        // configurer.setUseTrailingSlashMatch(true);
        WebMvcConfigurer.super.configurePathMatch(configurer);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        // 设置是否开启默认servlet处理，默认false。如果开启，则SpringMVC会对静态资源（如html、js、css等）进行处理，并将其映射到指定的目录下。
        WebMvcConfigurer.super.configureDefaultServletHandling(configurer);
    }

    // 内容协商策略 配置内容裁决的一些参数
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        //// 自定义策略
        // configurer.favorPathExtension(true)// 是否通过请求Url的扩展名来决定mediaType，默认true
        //	.ignoreAcceptHeader(true)// 不检查Accept请求头
        //	.parameterName("mediaType")
        //	.defaultContentType(MediaType.TEXT_HTML)// 设置默认的MediaType
        //	.mediaType("html", MediaType.TEXT_HTML)// 请求以.html结尾的会被当成MediaType.TEXT_HTML
        //	.mediaType("json", MediaType.APPLICATION_JSON)//
        // 请求以.json结尾的会被当成MediaType.APPLICATION_JSON
        //	.mediaType("xml", MediaType.APPLICATION_ATOM_XML);//
        // 请求以.xml结尾的会被当成MediaType.APPLICATION_ATOM_XML
        //
        //// 或者下面这种写法
        // Map<String, MediaType> map = new HashMap<>();
        // map.put("html", MediaType.TEXT_HTML);
        // map.put("json", MediaType.APPLICATION_JSON);
        // map.put("xml", MediaType.APPLICATION_ATOM_XML);
        //// 指定基于参数的解析类型
        // ParameterContentNegotiationStrategy negotiationStrategy = new
        // ParameterContentNegotiationStrategy(map);
        //// 指定基于请求头的解析
        // configurer.strategies(Arrays.asList(negotiationStrategy));
        WebMvcConfigurer.super.configureContentNegotiation(configurer);
    }

    // 异步调用支持
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // 注册callable拦截器
        configurer.registerCallableInterceptors(new TimeoutCallableProcessingInterceptor());
        // 注册deferredResult拦截器
        configurer.registerDeferredResultInterceptors(
                new TimeoutDeferredResultProcessingInterceptor());
        // 异步请求超时时间
        configurer.setDefaultTimeout(1000);
        // 设定异步请求线程池callable等, spring默认线程不可重用
        configurer.setTaskExecutor(asyncThreadPoolTaskExecutor);
    }

    @Override
    public void configureApiVersioning( ApiVersionConfigurer configurer ) {
//		configurer.setVersionRequired(false);
//		configurer.useRequestHeader("API-Version");
        // "/api/{version}/xxx" version是1.2.3 major.minor.patch、2.0、3 这种格式，解析时会移除非数字部分
//        configurer.usePathSegment(1);

//		StandardApiVersionDeprecationHandler handler = new StandardApiVersionDeprecationHandler();
//		handler.configureVersion("1").setDeprecationLink(URI.create("https://example.org/deprecation"));
//
//		configurer.useRequestHeader("X-API-Version")
//			.addSupportedVersions("1", "1.1", "1.3", "1.6")
//			.setDeprecationHandler(handler);

        WebMvcConfigurer.super.configureApiVersioning(configurer);
    }

    @Override
    public void configureHandlerExceptionResolvers( List<HandlerExceptionResolver> resolvers ) {
        WebMvcConfigurer.super.configureHandlerExceptionResolvers(resolvers);
    }

    // 参数解析器
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new LoginUserArgumentResolver());
        argumentResolvers.add(new ActMethodArgumentResolver());
    }

    // 返回值处理器
    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        // 自定义返回值处理器
        WebMvcConfigurer.super.addReturnValueHandlers(handlers);
    }

    // 拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (interceptorProperties.getHeader()) {
            registry.addInterceptor(new HeaderThreadLocalInterceptor())
                    .addPathPatterns("/**")
                    .excludePathPatterns("/actuator/**");
        }

        if (interceptorProperties.getDoubtApi()) {
            registry.addInterceptor(new DoubtApiInterceptor(interceptorProperties))
                    .addPathPatterns("/**")
                    .excludePathPatterns("/actuator/**");
        }

        registry.addInterceptor(traceMdcInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/actuator/**");
    }

    // 信息转化器
    @Override
    public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
        // Iterator<HttpMessageConverter<?>> iterator = converters.iterator();
        // while(iterator.hasNext()){
        //    HttpMessageConverter<?> converter = iterator.next();
        //    if(converter instanceof MappingJackson2HttpMessageConverter){
        //        iterator.remove();
        //    }
        // }

        // 创建fastJson消息转换器
        // FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        //// 创建配置类
        // FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        // fastJsonConfig.setWriteContentLength(true);
        // fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        //
        //        //1.格式化输出
        //        fastJsonConfig.setWriterFeatures(
        //                //将空置输出为缺省值，Number类型的null都输出为0，String类型的null输出为""，数组和Collection类型的输出为[]
        //                JSONWriter.Feature.NullAsDefaultValue,
        //                //字段如 vBtn  会被转为 VBtn  处理这样的问题
        //                JSONWriter.Feature.FieldBased,
        //                //long 转 string 丢失精度问题
        //                JSONWriter.Feature.WriteLongAsString,
        //
        //                JSONWriter.Feature.WriteNullListAsEmpty,
        //                //json格式化
        //                JSONWriter.Feature.PrettyFormat,
        //                //输出map中value为null的数据 保留map空的字段
        //                JSONWriter.Feature.WriteMapNullValue,
        //                //输出boolean 为 false
        //                JSONWriter.Feature.WriteNullBooleanAsFalse,
        //                //输出list 为 []
        //                JSONWriter.Feature.WriteNullListAsEmpty,
        //                //输出number 为 0
        //                JSONWriter.Feature.WriteNullNumberAsZero,
        //                //输出字符串 为 ""
        //                JSONWriter.Feature.WriteNullStringAsEmpty,
        //                //对map进行排序
        //                JSONWriter.Feature.MapSortField
        //        );
        //        //2.2配置反序列化的行为
        //        fastJsonConfig.setReaderFeatures(JSONReader.Feature.FieldBased,
        //                JSONReader.Feature.SupportArrayToBean,
        //                //对读取到的字符串值做trim处理
        //                JSONReader.Feature.TrimString);

        // fastConverter.setFastJsonConfig(fastJsonConfig);

        // SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        // serializeConfig.put(LocalDateTime.class, LocalDateTimeToTimestampSerializer.instance);
        //// 解决Long转json精度丢失的问题
        // serializeConfig.put(Long.class, ToStringSerializer.instance);
        // serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        // serializeConfig.put(BigDecimal.class, ToStringSerializer.instance);
        // serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        //
        //// 修改配置返回内容的过滤
        // fastJsonConfig.setSerializerFeatures(
        //        SerializerFeature.PrettyFormat,
        //        SerializerFeature.DisableCircularReferenceDetect,
        //        SerializerFeature.WriteMapNullValue,
        //        SerializerFeature.WriteNullBooleanAsFalse,  //boolean null返回false
        //        SerializerFeature.WriteNullStringAsEmpty,   //字符串null返回空字符串
        //        //空字段保留
        //        SerializerFeature.WriteNullListAsEmpty);
        // fastJsonConfig.setSerializeConfig(serializeConfig);

        // 将fastjson添加到视图消息转换器列表内
        // converters.add(0, fastConverter);
        // 需要追加byte，否则springdoc-openapi接口会响应Base64编码内容，导致接口文档显示失败
        // https://github.com/springdoc/springdoc-openapi/issues/2143
        // 解决方案
        builder.addCustomConverter(new ByteArrayHttpMessageConverter());
        builder.addCustomConverter(new JacksonJsonHttpMessageConverter());
        // 把自定义的序列化规则设置进入转换器里
//        for (HttpMessageConverter<?> converter : converters) {
//            if (converter instanceof JacksonJsonHttpMessageConverter jackson2Converter) {
////                jackson2Converter.setJsonMapper(JsonUtils.MAPPER);
//            }
//        }
    }


    // 异常处理器扩展
    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        //		resolvers.add(new MyExceptionResolver());
        WebMvcConfigurer.super.extendHandlerExceptionResolvers(resolvers);
    }

    // 格式化器和转换器
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new IntegerToEnumConverterFactory());
        registry.addConverterFactory(new StringToEnumConverterFactory());

        registry.addConverter(new String2DateConverter());
        registry.addConverter(new String2LocalDateConverter());
        registry.addConverter(new String2LocalDateTimeConverter());
        registry.addConverter(new String2LocalTimeConverter());
    }

    // 静态资源处理器
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(Duration.ofSeconds(3600)).cachePublic());

        // doc.html 与 /webjars/** 由 SpringDocAutoConfiguration / knife4j 模块注册，避免重复映射
    }

    // 视图控制器
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("index");
    }

    // 视图解析器
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver internalResourceViewResolver =
                new InternalResourceViewResolver();
        // 请求视图文件的前缀地址
        internalResourceViewResolver.setPrefix("/WEB-INF/jsp/");
        // 请求视图文件的后缀
        internalResourceViewResolver.setSuffix(".jsp");
        registry.viewResolver(internalResourceViewResolver);
    }

    @Bean
    @LoadBalanced
    public OkHttpClient.Builder builder() {
        return new OkHttpClient.Builder();
    }

    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory =
                Validation.byProvider(HibernateValidator.class)
                        .configure()
                        // 快速失败模式
                        .failFast(true)
                        .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

    // @Bean
    // public RequestContextListener requestContextListener() {
    //	return new RequestContextListener();
    // }


    @Configuration
    @ConditionalOnClass(RedisRepository.class)
    public static class RequestMappingScanListenerConfiguration {

        @Bean
        @ConditionalOnBean(RedisRepository.class)
        public RequestMappingScanListener requestMappingScanListener(RedisRepository redisRepository) {
            return new RequestMappingScanListener(redisRepository);
        }
    }


    //    public static class LocalDateTimeToTimestampSerializer implements ObjectSerializer {
    //
    //        public static final LocalDateTimeToTimestampSerializer instance = new
    // LocalDateTimeToTimestampSerializer();
    //        private static final String defaultPattern = "yyyy-MM-dd HH:mm:ss";
    //
    //        public LocalDateTimeToTimestampSerializer() {
    //        }
    //
    //        @Override
    //        public void write(JSONSerializer serializer, Object object, Object fieldName, Type
    // fieldType, int
    // features) throws IOException {
    //            SerializeWriter out = serializer.out;
    //            if (object == null) {
    //                out.writeNull();
    //            } else {
    //                LocalDateTime result = (LocalDateTime) object;
    //                out.writeString(result.format(DateTimeFormatter.ofPattern(defaultPattern)));
    //            }
    //        }
    //
    //    }
}
