/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.aopalliance.intercept.Interceptor
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.jspecify.annotations.Nullable
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.aop.framework.ReflectiveMethodInvocation
 *  org.springframework.core.KotlinDetector
 *  org.springframework.core.MethodIntrospector
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.format.support.DefaultFormattingConversionService
 *  org.springframework.util.Assert
 *  org.springframework.util.StringValueResolver
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.service.annotation.HttpExchange
 *  org.springframework.web.service.invoker.CookieValueArgumentResolver
 *  org.springframework.web.service.invoker.HttpExchangeAdapter
 *  org.springframework.web.service.invoker.HttpMethodArgumentResolver
 *  org.springframework.web.service.invoker.HttpRequestValues$Builder
 *  org.springframework.web.service.invoker.HttpRequestValues$Processor
 *  org.springframework.web.service.invoker.HttpServiceArgumentResolver
 *  org.springframework.web.service.invoker.PathVariableArgumentResolver
 *  org.springframework.web.service.invoker.RequestAttributeArgumentResolver
 *  org.springframework.web.service.invoker.RequestBodyArgumentResolver
 *  org.springframework.web.service.invoker.RequestHeaderArgumentResolver
 *  org.springframework.web.service.invoker.RequestParamArgumentResolver
 *  org.springframework.web.service.invoker.RequestPartArgumentResolver
 *  org.springframework.web.service.invoker.UriBuilderFactoryArgumentResolver
 *  org.springframework.web.service.invoker.UrlArgumentResolver
 */
package com.kuma.boot.web.httpexchange.shaded;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.aopalliance.intercept.Interceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.util.Assert;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.CookieValueArgumentResolver;
import org.springframework.web.service.invoker.HttpExchangeAdapter;
import org.springframework.web.service.invoker.HttpMethodArgumentResolver;
import org.springframework.web.service.invoker.HttpRequestValues;
import org.springframework.web.service.invoker.HttpServiceArgumentResolver;
import org.springframework.web.service.invoker.PathVariableArgumentResolver;
import org.springframework.web.service.invoker.RequestAttributeArgumentResolver;
import org.springframework.web.service.invoker.RequestBodyArgumentResolver;
import org.springframework.web.service.invoker.RequestHeaderArgumentResolver;
import org.springframework.web.service.invoker.RequestParamArgumentResolver;
import org.springframework.web.service.invoker.RequestPartArgumentResolver;
import org.springframework.web.service.invoker.UriBuilderFactoryArgumentResolver;
import org.springframework.web.service.invoker.UrlArgumentResolver;

public final class ShadedHttpServiceProxyFactory {
    private final HttpExchangeAdapter exchangeAdapter;
    private final List<HttpServiceArgumentResolver> argumentResolvers;
    private final HttpRequestValues.Processor requestValuesProcessor;
    private final @Nullable StringValueResolver embeddedValueResolver;

    private ShadedHttpServiceProxyFactory(HttpExchangeAdapter exchangeAdapter, List<HttpServiceArgumentResolver> argumentResolvers, List<HttpRequestValues.Processor> requestValuesProcessor, @Nullable StringValueResolver embeddedValueResolver) {
        this.exchangeAdapter = exchangeAdapter;
        this.argumentResolvers = argumentResolvers;
        this.requestValuesProcessor = new CompositeHttpRequestValuesProcessor(requestValuesProcessor);
        this.embeddedValueResolver = embeddedValueResolver;
    }

    public <S> S createClient(Class<S> serviceType) {
        List<ShadedHttpServiceMethod> httpServiceMethods = MethodIntrospector.selectMethods(serviceType, this::isExchangeMethod).stream().map(method -> this.createHttpServiceMethod(serviceType, (Method)method)).toList();
        return this.getProxy(serviceType, httpServiceMethods);
    }

    private <S> S getProxy(Class<S> serviceType, List<ShadedHttpServiceMethod> httpServiceMethods) {
        HttpServiceMethodInterceptor interceptor = new HttpServiceMethodInterceptor(httpServiceMethods);
        ProxyFactory factory = new ProxyFactory(serviceType, (Interceptor)interceptor);
        return (S)factory.getProxy(serviceType.getClassLoader());
    }

    private boolean isExchangeMethod(Method method) {
        return AnnotatedElementUtils.hasAnnotation((AnnotatedElement)method, HttpExchange.class) || AnnotatedElementUtils.hasAnnotation((AnnotatedElement)method, RequestMapping.class);
    }

    private <S> ShadedHttpServiceMethod createHttpServiceMethod(Class<S> serviceType, Method method) {
        Assert.notNull(this.argumentResolvers, (String)"No argument resolvers: afterPropertiesSet was not called");
        return new ShadedHttpServiceMethod(method, serviceType, this.argumentResolvers, this.requestValuesProcessor, this.exchangeAdapter, this.embeddedValueResolver);
    }

    public static Builder builderFor(HttpExchangeAdapter exchangeAdapter) {
        return new Builder().exchangeAdapter(exchangeAdapter);
    }

    public static Builder builder() {
        return new Builder();
    }

    private record CompositeHttpRequestValuesProcessor(List<HttpRequestValues.Processor> processors) implements HttpRequestValues.Processor
    {
        public void process(Method method, MethodParameter[] parameters, @Nullable Object[] arguments, HttpRequestValues.Builder builder) {
            for (HttpRequestValues.Processor processor : this.processors) {
                processor.process(method, parameters, arguments, builder);
            }
        }
    }

    private static final class HttpServiceMethodInterceptor
    implements MethodInterceptor {
        private final Map<Method, ShadedHttpServiceMethod> httpServiceMethods;

        private HttpServiceMethodInterceptor(List<ShadedHttpServiceMethod> methods) {
            this.httpServiceMethods = methods.stream().collect(Collectors.toMap(ShadedHttpServiceMethod::getMethod, Function.identity()));
        }

        public Object invoke(MethodInvocation invocation) throws Throwable {
            Method method = invocation.getMethod();
            ShadedHttpServiceMethod httpServiceMethod = this.httpServiceMethods.get(method);
            if (httpServiceMethod != null) {
                Object[] arguments = KotlinDetector.isSuspendingFunction((Method)method) ? HttpServiceMethodInterceptor.resolveCoroutinesArguments(invocation.getArguments()) : invocation.getArguments();
                return httpServiceMethod.invoke(arguments);
            }
            if (method.isDefault() && invocation instanceof ReflectiveMethodInvocation) {
                ReflectiveMethodInvocation reflectiveMethodInvocation = (ReflectiveMethodInvocation)invocation;
                Object proxy = reflectiveMethodInvocation.getProxy();
                return InvocationHandler.invokeDefault(proxy, method, invocation.getArguments());
            }
            throw new IllegalStateException("Unexpected method invocation: " + String.valueOf(method));
        }

        private static Object[] resolveCoroutinesArguments(@Nullable Object[] args) {
            if (args == null) {
                throw new IllegalStateException("Unexpected null arguments");
            }
            Object[] functionArgs = new Object[args.length - 1];
            System.arraycopy(args, 0, functionArgs, 0, args.length - 1);
            return functionArgs;
        }
    }

    public static final class Builder {
        private @Nullable HttpExchangeAdapter exchangeAdapter;
        private Function<HttpExchangeAdapter, HttpExchangeAdapter> exchangeAdapterDecorator = Function.identity();
        private final List<HttpServiceArgumentResolver> customArgumentResolvers = new ArrayList<HttpServiceArgumentResolver>();
        private @Nullable ConversionService conversionService;
        private final List<HttpRequestValues.Processor> requestValuesProcessors = new ArrayList<HttpRequestValues.Processor>();
        private @Nullable StringValueResolver embeddedValueResolver;

        private Builder() {
        }

        public Builder exchangeAdapter(HttpExchangeAdapter adapter) {
            this.exchangeAdapter = adapter;
            return this;
        }

        public Builder exchangeAdapterDecorator(Function<HttpExchangeAdapter, HttpExchangeAdapter> decorator) {
            this.exchangeAdapterDecorator = this.exchangeAdapterDecorator.andThen(decorator);
            return this;
        }

        public Builder customArgumentResolver(HttpServiceArgumentResolver resolver) {
            this.customArgumentResolvers.add(resolver);
            return this;
        }

        public Builder conversionService(ConversionService conversionService) {
            this.conversionService = conversionService;
            return this;
        }

        public Builder httpRequestValuesProcessor(HttpRequestValues.Processor processor) {
            this.requestValuesProcessors.add(processor);
            return this;
        }

        public Builder embeddedValueResolver(StringValueResolver embeddedValueResolver) {
            this.embeddedValueResolver = embeddedValueResolver;
            return this;
        }

        public ShadedHttpServiceProxyFactory build() {
            Assert.notNull((Object)this.exchangeAdapter, (String)"HttpClientAdapter is required");
            HttpExchangeAdapter adapterToUse = this.exchangeAdapterDecorator.apply(this.exchangeAdapter);
            return new ShadedHttpServiceProxyFactory(adapterToUse, this.initArgumentResolvers(), this.requestValuesProcessors, this.embeddedValueResolver);
        }

        private List<HttpServiceArgumentResolver> initArgumentResolvers() {
            ArrayList<HttpServiceArgumentResolver> resolvers = new ArrayList<HttpServiceArgumentResolver>(this.customArgumentResolvers);
            ConversionService service = this.conversionService != null ? this.conversionService : new DefaultFormattingConversionService();
            resolvers.add((HttpServiceArgumentResolver)new RequestHeaderArgumentResolver(service));
            resolvers.add((HttpServiceArgumentResolver)new RequestBodyArgumentResolver(this.exchangeAdapter));
            resolvers.add((HttpServiceArgumentResolver)new PathVariableArgumentResolver(service));
            resolvers.add((HttpServiceArgumentResolver)new RequestParamArgumentResolver(service));
            resolvers.add((HttpServiceArgumentResolver)new RequestPartArgumentResolver(this.exchangeAdapter));
            resolvers.add((HttpServiceArgumentResolver)new CookieValueArgumentResolver(service));
            if (this.exchangeAdapter.supportsRequestAttributes()) {
                resolvers.add((HttpServiceArgumentResolver)new RequestAttributeArgumentResolver());
            }
            resolvers.add((HttpServiceArgumentResolver)new UrlArgumentResolver());
            resolvers.add((HttpServiceArgumentResolver)new UriBuilderFactoryArgumentResolver());
            resolvers.add((HttpServiceArgumentResolver)new HttpMethodArgumentResolver());
            return resolvers;
        }
    }
}

