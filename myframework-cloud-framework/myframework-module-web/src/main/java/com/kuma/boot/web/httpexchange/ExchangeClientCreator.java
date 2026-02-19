/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.jspecify.annotations.Nullable
 *  org.reactivestreams.Publisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder
 *  org.springframework.boot.http.client.HttpClientSettings
 *  org.springframework.boot.http.client.HttpRedirects
 *  org.springframework.boot.http.client.reactive.ClientHttpConnectorBuilder
 *  org.springframework.boot.restclient.RestClientCustomizer
 *  org.springframework.boot.ssl.SslBundle
 *  org.springframework.boot.ssl.SslBundles
 *  org.springframework.boot.webclient.WebClientCustomizer
 *  org.springframework.cloud.client.loadbalancer.DeferringLoadBalancerInterceptor
 *  org.springframework.cloud.client.loadbalancer.reactive.DeferringLoadBalancerExchangeFilterFunction
 *  org.springframework.core.annotation.AnnotationAwareOrderComparator
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.env.Environment
 *  org.springframework.http.client.ClientHttpRequestInterceptor
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.util.StringValueResolver
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.client.RestClient
 *  org.springframework.web.client.RestClient$Builder
 *  org.springframework.web.client.support.RestClientAdapter
 *  org.springframework.web.reactive.function.client.WebClient
 *  org.springframework.web.reactive.function.client.WebClient$Builder
 *  org.springframework.web.reactive.function.client.support.WebClientAdapter
 *  org.springframework.web.service.annotation.HttpExchange
 *  org.springframework.web.service.invoker.HttpExchangeAdapter
 *  org.springframework.web.service.invoker.HttpServiceArgumentResolver
 *  org.springframework.web.service.invoker.HttpServiceProxyFactory
 *  org.springframework.web.service.invoker.HttpServiceProxyFactory$Builder
 */
package com.kuma.boot.web.httpexchange;

import com.kuma.boot.web.httpexchange.shaded.ShadedHttpServiceProxyFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Flow;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.boot.http.client.HttpRedirects;
import org.springframework.boot.http.client.reactive.ClientHttpConnectorBuilder;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.webclient.WebClientCustomizer;
import org.springframework.cloud.client.loadbalancer.DeferringLoadBalancerInterceptor;
import org.springframework.cloud.client.loadbalancer.reactive.DeferringLoadBalancerExchangeFilterFunction;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.HttpExchangeAdapter;
import org.springframework.web.service.invoker.HttpServiceArgumentResolver;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

class ExchangeClientCreator {
    private static final Logger log = LoggerFactory.getLogger(ExchangeClientCreator.class);
    private static final boolean LOADBALANCER_PRESENT = ClassUtils.isPresent((String)"org.springframework.cloud.client.loadbalancer.LoadBalancerClient", null);
    private static final boolean DEFERRING_LOADBALANCER_INTERCEPTOR_PRESENT = ClassUtils.isPresent((String)"org.springframework.cloud.client.loadbalancer.DeferringLoadBalancerInterceptor", null);
    private static final boolean springBootStarterRestClientPresent = ClassUtils.isPresent((String)"org.springframework.boot.restclient.RestClientCustomizer", null);
    private static final boolean springBootStarterWebClientPresent = ClassUtils.isPresent((String)"org.springframework.boot.webclient.WebClientCustomizer", null);
    private static final Field exchangeAdapterField;
    private static final Field customArgumentResolversField;
    private static final Field conversionServiceField;
    private static final Field embeddedValueResolverField;
    private static final Field requestValuesProcessorsField;
    private static final Field exchangeAdapterDecoratorField;
    private final BeanFactory beanFactory;
    private final Environment environment;
    private final Class<?> clientType;
    private final boolean isUseHttpExchangeAnnotation;

    @SuppressFBWarnings(value={"CT_CONSTRUCTOR_THROW"})
    public ExchangeClientCreator(BeanFactory beanFactory, Class<?> clientType) {
        this.beanFactory = beanFactory;
        this.environment = (Environment)beanFactory.getBean(Environment.class);
        Assert.isTrue((boolean)clientType.isInterface(), () -> String.valueOf(clientType) + " is not an interface");
        this.clientType = clientType;
        Assert.isTrue((boolean)Util.isHttpExchangeInterface(clientType), () -> String.valueOf(clientType) + " is not a HttpExchange client");
        this.isUseHttpExchangeAnnotation = Util.hasAnnotation(clientType, HttpExchange.class);
    }

    public <T> T create() {
        HttpExchangeProperties httpExchangeProperties = (HttpExchangeProperties)this.beanFactory.getBeanProvider(HttpExchangeProperties.class).getIfUnique(() -> Util.getProperties(this.environment));
        HttpExchangeProperties.Channel chan = Util.findMatchedConfig(this.clientType, httpExchangeProperties).orElseGet(httpExchangeProperties::defaultChannel);
        if (this.isUseHttpExchangeAnnotation) {
            HttpServiceProxyFactory factory = this.factoryBuilder(chan).build();
            Object result = factory.createClient(this.clientType);
            Cache.addClient(result);
            return (T)result;
        }
        if (!httpExchangeProperties.isRequestMappingSupportEnabled()) {
            throw new IllegalStateException(String.valueOf(this.clientType) + " is using the @RequestMapping based annotation, please migrate to @HttpExchange, or set 'http-exchange.request-mapping-support-enabled=true' to enable support for processing @RequestMapping");
        }
        ShadedHttpServiceProxyFactory shadedFactory = ExchangeClientCreator.shadedProxyFactory(this.factoryBuilder(chan)).build();
        Object result = shadedFactory.createClient(this.clientType);
        Cache.addClient(result);
        return (T)result;
    }

    private HttpServiceProxyFactory.Builder factoryBuilder(HttpExchangeProperties.Channel channelConfig) {
        HttpServiceProxyFactory.Builder builder = HttpServiceProxyFactory.builder();
        this.beanFactory.getBeanProvider(HttpServiceProxyFactoryCustomizer.class).orderedStream().forEach(customizer -> customizer.customize(builder));
        this.setExchangeAdapter(builder, channelConfig);
        this.setEmbeddedValueResolver(builder);
        this.addCustomArgumentResolver(builder);
        return builder;
    }

    private void setExchangeAdapter(HttpServiceProxyFactory.Builder builder, HttpExchangeProperties.Channel channelConfig) {
        switch (this.getClientType(channelConfig)) {
            case REST_CLIENT: {
                builder.exchangeAdapter((HttpExchangeAdapter)RestClientAdapter.create((RestClient)ExchangeClientCreator.getClient(new Cache.ClientId(channelConfig, HttpExchangeProperties.ClientType.REST_CLIENT), () -> this.buildRestClient(channelConfig))));
                break;
            }
            case WEB_CLIENT: {
                builder.exchangeAdapter((HttpExchangeAdapter)WebClientAdapter.create((WebClient)ExchangeClientCreator.getClient(new Cache.ClientId(channelConfig, HttpExchangeProperties.ClientType.WEB_CLIENT), () -> this.buildWebClient(channelConfig))));
                break;
            }
            default: {
                throw new IllegalStateException("Unsupported client-type: " + String.valueOf((Object)channelConfig.getClientType()));
            }
        }
    }

    private static <T> T getClient(Cache.ClientId clientId, Supplier<T> supplier) {
        return Boolean.TRUE.equals(clientId.channel().getHttpClientReuseEnabled()) ? Cache.getHttpClient(clientId, supplier) : supplier.get();
    }

    private void addCustomArgumentResolver(HttpServiceProxyFactory.Builder builder) {
        List existingResolvers = Optional.ofNullable((List)ExchangeClientCreator.getFieldValue(builder, customArgumentResolversField)).orElseGet(List::of);
        this.beanFactory.getBeanProvider(HttpServiceArgumentResolver.class).orderedStream().filter(resolver -> !existingResolvers.contains(resolver)).forEach(arg_0 -> ((HttpServiceProxyFactory.Builder)builder).customArgumentResolver(arg_0));
    }

    private void setEmbeddedValueResolver(HttpServiceProxyFactory.Builder builder) {
        StringValueResolver resolver = Optional.ofNullable(ExchangeClientCreator.getFieldValue(builder, embeddedValueResolverField)).map(StringValueResolver.class::cast).map(r -> new UrlPlaceholderStringValueResolver(this.environment, (StringValueResolver)r)).orElseGet(() -> new UrlPlaceholderStringValueResolver(this.environment, null));
        builder.embeddedValueResolver(resolver);
    }

    private WebClient buildWebClient(HttpExchangeProperties.Channel channelConfig) {
        WebClient.Builder builder = WebClient.builder();
        this.configureWebClientBuilder(builder, channelConfig);
        String baseUrl = channelConfig.getBaseUrl();
        if (StringUtils.hasText((String)baseUrl)) {
            builder.baseUrl(ExchangeClientCreator.getRealBaseUrl(baseUrl));
        }
        if (!CollectionUtils.isEmpty(channelConfig.getHeaders())) {
            channelConfig.getHeaders().forEach(header -> builder.defaultHeader(header.key(), (String[])header.values().toArray(String[]::new)));
        }
        if (this.isLoadBalancerEnabled(channelConfig)) {
            builder.filters(filters -> {
                LinkedHashSet allFilters = new LinkedHashSet(filters);
                this.beanFactory.getBeanProvider(DeferringLoadBalancerExchangeFilterFunction.class).forEach(allFilters::add);
                filters.clear();
                filters.addAll(allFilters);
                AnnotationAwareOrderComparator.sort((List)filters);
            });
        }
        this.beanFactory.getBeanProvider(HttpClientCustomizer.WebClientCustomizer.class).orderedStream().forEach(customizer -> customizer.customize(builder, channelConfig));
        return builder.build();
    }

    private void configureWebClientBuilder(WebClient.Builder builder, HttpExchangeProperties.Channel channelConfig) {
        List customizers = this.beanFactory.getBeanProvider(WebClientCustomizer.class).orderedStream().toList();
        for (WebClientCustomizer customizer : customizers) {
            customizer.customize(builder);
        }
        ClientHttpConnectorBuilder clientConnectorBuilder = (ClientHttpConnectorBuilder)this.beanFactory.getBeanProvider(ClientHttpConnectorBuilder.class).getIfUnique(ClientHttpConnectorBuilder::detect);
        HttpClientSettings settings = this.buildHttpClientSettings(channelConfig);
        builder.clientConnector(clientConnectorBuilder.build(settings));
    }

    private RestClient buildRestClient(HttpExchangeProperties.Channel channelConfig) {
        RestClient.Builder builder = RestClient.builder();
        this.configureRestClientBuilder(builder, channelConfig);
        String baseUrl = channelConfig.getBaseUrl();
        if (StringUtils.hasText((String)baseUrl)) {
            builder.baseUrl(ExchangeClientCreator.getRealBaseUrl(baseUrl));
        }
        if (!CollectionUtils.isEmpty(channelConfig.getHeaders())) {
            channelConfig.getHeaders().forEach(header -> builder.defaultHeader(header.key(), (String[])header.values().toArray(String[]::new)));
        }
        if (this.isLoadBalancerEnabled(channelConfig)) {
            builder.requestInterceptors(interceptors -> {
                LinkedHashSet lbInterceptors = new LinkedHashSet(interceptors);
                if (DEFERRING_LOADBALANCER_INTERCEPTOR_PRESENT) {
                    this.beanFactory.getBeanProvider(DeferringLoadBalancerInterceptor.class).forEach(lbInterceptors::add);
                } else {
                    this.beanFactory.getBeanProvider(ClientHttpRequestInterceptor.class).forEach(lbInterceptors::add);
                }
                interceptors.clear();
                interceptors.addAll(lbInterceptors);
                AnnotationAwareOrderComparator.sort((List)interceptors);
            });
        }
        this.beanFactory.getBeanProvider(HttpClientCustomizer.RestClientCustomizer.class).orderedStream().forEach(customizer -> customizer.customize(builder, channelConfig));
        return builder.build();
    }

    private void configureRestClientBuilder(RestClient.Builder builder, HttpExchangeProperties.Channel channelConfig) {
        ClientHttpRequestFactoryBuilder requestFactoryBuilder = (ClientHttpRequestFactoryBuilder)this.beanFactory.getBeanProvider(ClientHttpRequestFactoryBuilder.class).getIfUnique(ClientHttpRequestFactoryBuilder::detect);
        HttpClientSettings settings = this.buildHttpClientSettings(channelConfig);
        builder.requestFactory(requestFactoryBuilder.build(settings));
        List customizers = this.beanFactory.getBeanProvider(RestClientCustomizer.class).orderedStream().toList();
        for (RestClientCustomizer customizer : customizers) {
            customizer.customize(builder);
        }
    }

    private HttpClientSettings buildHttpClientSettings(HttpExchangeProperties.Channel channelConfig) {
        HttpClientSettings globalConfig = (HttpClientSettings)this.beanFactory.getBeanProvider(HttpClientSettings.class).getIfUnique(HttpClientSettings::defaults);
        HttpRedirects redirects = Optional.ofNullable(channelConfig.getRedirects()).orElseGet(() -> ((HttpClientSettings)globalConfig).redirects());
        Duration connectTimeout = Optional.ofNullable(channelConfig.getConnectTimeout()).map(Duration::ofMillis).orElseGet(() -> ((HttpClientSettings)globalConfig).connectTimeout());
        Duration readTimeout = Optional.ofNullable(channelConfig.getReadTimeout()).map(Duration::ofMillis).orElseGet(() -> ((HttpClientSettings)globalConfig).readTimeout());
        SslBundle sslBundle = Optional.ofNullable(channelConfig.getSsl()).map(HttpExchangeProperties.Ssl::bundle).filter(StringUtils::hasText).map(bundle -> ((SslBundles)this.beanFactory.getBean(SslBundles.class)).getBundle(bundle)).orElseGet(() -> ((HttpClientSettings)globalConfig).sslBundle());
        return new HttpClientSettings(redirects, connectTimeout, readTimeout, sslBundle);
    }

    private boolean isLoadBalancerEnabled(HttpExchangeProperties.Channel channelConfig) {
        return LOADBALANCER_PRESENT && (Boolean)this.environment.getProperty("spring.cloud.loadbalancer.enabled", Boolean.class, (Object)true) != false && Boolean.TRUE.equals(channelConfig.getLoadbalancerEnabled());
    }

    private static String getRealBaseUrl(String baseUrl) {
        return baseUrl.contains("://") ? baseUrl : "http://" + baseUrl;
    }

    static ShadedHttpServiceProxyFactory.Builder shadedProxyFactory(HttpServiceProxyFactory.Builder proxyFactory) {
        HttpExchangeAdapter exchangeAdapter = (HttpExchangeAdapter)ExchangeClientCreator.getFieldValue(proxyFactory, exchangeAdapterField);
        List customArgumentResolvers = (List)ExchangeClientCreator.getFieldValue(proxyFactory, customArgumentResolversField);
        ConversionService conversionService = (ConversionService)ExchangeClientCreator.getFieldValue(proxyFactory, conversionServiceField);
        StringValueResolver embeddedValueResolver = (StringValueResolver)ExchangeClientCreator.getFieldValue(proxyFactory, embeddedValueResolverField);
        List requestValuesProcessors = (List)ExchangeClientCreator.getFieldValue(proxyFactory, requestValuesProcessorsField);
        Function exchangeAdapterDecorator = (Function)ExchangeClientCreator.getFieldValue(proxyFactory, exchangeAdapterDecoratorField);
        ShadedHttpServiceProxyFactory.Builder builder = ShadedHttpServiceProxyFactory.builder();
        Optional.ofNullable(exchangeAdapter).ifPresent(builder::exchangeAdapter);
        Optional.ofNullable(customArgumentResolvers).stream().flatMap(Collection::stream).forEach(builder::customArgumentResolver);
        Optional.ofNullable(conversionService).ifPresent(builder::conversionService);
        Optional.ofNullable(embeddedValueResolver).ifPresent(builder::embeddedValueResolver);
        Optional.ofNullable(requestValuesProcessors).stream().flatMap(Collection::stream).forEach(builder::httpRequestValuesProcessor);
        Optional.ofNullable(exchangeAdapterDecorator).ifPresent(builder::exchangeAdapterDecorator);
        return builder;
    }

    static boolean hasReactiveReturnTypeMethod(Class<?> clz) {
        return Arrays.stream(ReflectionUtils.getAllDeclaredMethods(clz)).filter(method -> AnnotationUtils.findAnnotation((Method)method, HttpExchange.class) != null || AnnotationUtils.findAnnotation((Method)method, RequestMapping.class) != null).map(Method::getReturnType).anyMatch(returnType -> Publisher.class.isAssignableFrom((Class<?>)returnType) || Flow.Publisher.class.isAssignableFrom((Class<?>)returnType));
    }

    private HttpExchangeProperties.ClientType getClientType(HttpExchangeProperties.Channel channel) {
        HttpExchangeProperties.ClientType type = channel.getClientType() != null ? channel.getClientType() : ExchangeClientCreator.getDefaultClientType();
        return switch (type) {
            default -> throw new MatchException(null, null);
            case HttpExchangeProperties.ClientType.REST_CLIENT -> {
                if (!springBootStarterRestClientPresent) {
                    throw new IllegalStateException("You need to add 'spring-boot-starter-restclient' to the classpath to use REST_CLIENT");
                }
                if (springBootStarterWebClientPresent && ExchangeClientCreator.hasReactiveReturnTypeMethod(this.clientType)) {
                    log.warn("{} contains methods with reactive return types, should use the client-type '{}' instead of '{}'", new Object[]{this.clientType.getSimpleName(), HttpExchangeProperties.ClientType.WEB_CLIENT, HttpExchangeProperties.ClientType.REST_CLIENT});
                    yield HttpExchangeProperties.ClientType.WEB_CLIENT;
                }
                yield HttpExchangeProperties.ClientType.REST_CLIENT;
            }
            case HttpExchangeProperties.ClientType.WEB_CLIENT -> {
                if (!springBootStarterWebClientPresent) {
                    throw new IllegalStateException("You need to add 'spring-boot-starter-webclient' to the classpath to use WEB_CLIENT");
                }
                yield HttpExchangeProperties.ClientType.WEB_CLIENT;
            }
        };
    }

    private static <T> @Nullable T getFieldValue(Object obj, Field field) {
        ReflectionUtils.makeAccessible((Field)field);
        return (T)ReflectionUtils.getField((Field)field, (Object)obj);
    }

    private static HttpExchangeProperties.ClientType getDefaultClientType() {
        if (springBootStarterRestClientPresent) {
            return HttpExchangeProperties.ClientType.REST_CLIENT;
        }
        if (springBootStarterWebClientPresent) {
            return HttpExchangeProperties.ClientType.WEB_CLIENT;
        }
        throw new IllegalStateException("You need to add 'spring-boot-starter-restclient' or 'spring-boot-starter-webclient' to the classpath");
    }

    static {
        try {
            Class<HttpServiceProxyFactory.Builder> clz = HttpServiceProxyFactory.Builder.class;
            exchangeAdapterField = clz.getDeclaredField("exchangeAdapter");
            customArgumentResolversField = clz.getDeclaredField("customArgumentResolvers");
            conversionServiceField = clz.getDeclaredField("conversionService");
            embeddedValueResolverField = clz.getDeclaredField("embeddedValueResolver");
            requestValuesProcessorsField = clz.getDeclaredField("requestValuesProcessors");
            exchangeAdapterDecoratorField = clz.getDeclaredField("exchangeAdapterDecorator");
        }
        catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }
}

