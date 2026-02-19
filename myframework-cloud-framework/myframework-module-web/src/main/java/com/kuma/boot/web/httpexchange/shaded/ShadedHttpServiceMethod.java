/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.jspecify.annotations.Nullable
 *  org.reactivestreams.Publisher
 *  org.springframework.core.DefaultParameterNameDiscoverer
 *  org.springframework.core.KotlinDetector
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ParameterNameDiscoverer
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.core.ReactiveAdapter
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.core.annotation.MergedAnnotation
 *  org.springframework.core.annotation.MergedAnnotationPredicates
 *  org.springframework.core.annotation.MergedAnnotations
 *  org.springframework.core.annotation.MergedAnnotations$SearchStrategy
 *  org.springframework.core.annotation.RepeatableContainers
 *  org.springframework.core.annotation.SynthesizingMethodParameter
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.MediaType
 *  org.springframework.http.ResponseEntity
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.util.StringValueResolver
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.service.invoker.HttpExchangeAdapter
 *  org.springframework.web.service.invoker.HttpRequestValues
 *  org.springframework.web.service.invoker.HttpRequestValues$Builder
 *  org.springframework.web.service.invoker.HttpRequestValues$Processor
 *  org.springframework.web.service.invoker.HttpServiceArgumentResolver
 *  org.springframework.web.service.invoker.ReactiveHttpRequestValues
 *  org.springframework.web.service.invoker.ReactorHttpExchangeAdapter
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package com.kuma.boot.web.httpexchange.shaded;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.reactivestreams.Publisher;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotationPredicates;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.service.invoker.HttpExchangeAdapter;
import org.springframework.web.service.invoker.HttpRequestValues;
import org.springframework.web.service.invoker.HttpServiceArgumentResolver;
import org.springframework.web.service.invoker.ReactiveHttpRequestValues;
import org.springframework.web.service.invoker.ReactorHttpExchangeAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

final class ShadedHttpServiceMethod {
    private static final boolean REACTOR_PRESENT = ClassUtils.isPresent((String)"reactor.core.publisher.Mono", (ClassLoader)ShadedHttpServiceMethod.class.getClassLoader());
    private final Method method;
    private final MethodParameter[] parameters;
    private final List<HttpServiceArgumentResolver> argumentResolvers;
    private final HttpRequestValues.Processor requestValuesProcessor;
    private final HttpRequestValuesInitializer requestValuesInitializer;
    private final ResponseFunction responseFunction;

    ShadedHttpServiceMethod(Method method, Class<?> containingClass, List<HttpServiceArgumentResolver> argumentResolvers, HttpRequestValues.Processor valuesProcessor, HttpExchangeAdapter adapter, @Nullable StringValueResolver embeddedValueResolver) {
        this.method = method;
        this.parameters = ShadedHttpServiceMethod.initMethodParameters(method);
        this.argumentResolvers = argumentResolvers;
        this.requestValuesProcessor = valuesProcessor;
        boolean isReactorAdapter = REACTOR_PRESENT && adapter instanceof ReactorHttpExchangeAdapter;
        this.requestValuesInitializer = HttpRequestValuesInitializer.create(method, containingClass, embeddedValueResolver, isReactorAdapter ? ReactiveHttpRequestValues::builder : HttpRequestValues::builder);
        this.responseFunction = isReactorAdapter ? ReactorExchangeResponseFunction.create((ReactorHttpExchangeAdapter)adapter, method) : ExchangeResponseFunction.create(adapter, method);
    }

    private static MethodParameter[] initMethodParameters(Method method) {
        int count = method.getParameterCount();
        if (count == 0) {
            return new MethodParameter[0];
        }
        if (KotlinDetector.isSuspendingFunction((Method)method)) {
            --count;
        }
        DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
        MethodParameter[] parameters = new MethodParameter[count];
        for (int i = 0; i < count; ++i) {
            parameters[i] = new SynthesizingMethodParameter(method, i);
            parameters[i].initParameterNameDiscovery((ParameterNameDiscoverer)nameDiscoverer);
        }
        return parameters;
    }

    public Method getMethod() {
        return this.method;
    }

    public @Nullable Object invoke(@Nullable Object[] arguments) {
        HttpRequestValues.Builder requestValues = this.requestValuesInitializer.initializeRequestValuesBuilder();
        this.applyArguments(requestValues, arguments);
        this.requestValuesProcessor.process(this.method, this.parameters, arguments, requestValues);
        return this.responseFunction.execute(requestValues.build());
    }

    private void applyArguments(HttpRequestValues.Builder requestValues, @Nullable Object[] arguments) {
        Assert.isTrue((arguments.length == this.parameters.length ? 1 : 0) != 0, (String)"Method argument mismatch");
        int i = 0;
        while (i < arguments.length) {
            Object value = arguments[i];
            boolean resolved = false;
            for (HttpServiceArgumentResolver resolver : this.argumentResolvers) {
                if (!resolver.resolve(value, this.parameters[i], requestValues)) continue;
                resolved = true;
                break;
            }
            int index = i++;
            Assert.state((boolean)resolved, () -> "Could not resolve parameter [" + this.parameters[index].getParameterIndex() + "] in " + this.parameters[index].getExecutable().toGenericString() + ": No suitable resolver");
        }
    }

    private record HttpRequestValuesInitializer(@Nullable HttpMethod httpMethod, @Nullable String url, @Nullable MediaType contentType, @Nullable List<MediaType> acceptMediaTypes, MultiValueMap<String, String> headers, @Nullable String version, Supplier<HttpRequestValues.Builder> requestValuesSupplier) {
        public HttpRequestValues.Builder initializeRequestValuesBuilder() {
            HttpRequestValues.Builder requestValues = this.requestValuesSupplier.get();
            if (this.httpMethod != null) {
                requestValues.setHttpMethod(this.httpMethod);
            }
            if (this.url != null) {
                requestValues.setUriTemplate(this.url);
            }
            if (this.contentType != null) {
                requestValues.setContentType(this.contentType);
            }
            if (this.acceptMediaTypes != null) {
                requestValues.setAccept(this.acceptMediaTypes);
            }
            this.headers.forEach((name, values) -> values.forEach(value -> requestValues.addHeader(name, new String[]{value})));
            if (this.version != null) {
                requestValues.setApiVersion((Object)this.version);
            }
            return requestValues;
        }

        public static HttpRequestValuesInitializer create(Method method, Class<?> containingClass, @Nullable StringValueResolver embeddedValueResolver, Supplier<HttpRequestValues.Builder> requestValuesSupplier) {
            List<AnnotationDescriptor> methodHttpExchanges = HttpRequestValuesInitializer.getAnnotationDescriptors(method);
            Assert.state((!methodHttpExchanges.isEmpty() ? 1 : 0) != 0, () -> "Expected @HttpExchange annotation on method " + String.valueOf(method));
            Assert.state((methodHttpExchanges.size() == 1 ? 1 : 0) != 0, () -> "Multiple @HttpExchange annotations found on method %s, but only one is allowed: %s".formatted(method, methodHttpExchanges));
            List<AnnotationDescriptor> typeHttpExchanges = HttpRequestValuesInitializer.getAnnotationDescriptors(containingClass);
            Assert.state((typeHttpExchanges.size() <= 1 ? 1 : 0) != 0, () -> "Multiple @HttpExchange annotations found on %s, but only one is allowed: %s".formatted(containingClass, typeHttpExchanges));
            RequestMapping methodAnnotation = (RequestMapping)AnnotatedElementUtils.findMergedAnnotation(containingClass, RequestMapping.class);
            RequestMapping typeAnnotation = (RequestMapping)AnnotatedElementUtils.findMergedAnnotation((AnnotatedElement)method, RequestMapping.class);
            HttpMethod httpMethod = HttpRequestValuesInitializer.initHttpMethod(typeAnnotation, methodAnnotation);
            String url = HttpRequestValuesInitializer.initUrl(typeAnnotation, methodAnnotation, embeddedValueResolver);
            MediaType contentType = HttpRequestValuesInitializer.initContentType(typeAnnotation, methodAnnotation);
            List<MediaType> acceptableMediaTypes = HttpRequestValuesInitializer.initAccept(typeAnnotation, methodAnnotation);
            MultiValueMap<String, String> headers = HttpRequestValuesInitializer.initHeaders(typeAnnotation, methodAnnotation, embeddedValueResolver);
            String version = HttpRequestValuesInitializer.initVersion(typeAnnotation, methodAnnotation);
            return new HttpRequestValuesInitializer(httpMethod, url, contentType, acceptableMediaTypes, headers, version, requestValuesSupplier);
        }

        private static @Nullable HttpMethod initHttpMethod(@Nullable RequestMapping typeAnnot, RequestMapping annot) {
            String value2;
            String value1 = (typeAnnot != null ? typeAnnot.method().length : 0) > 0 ? typeAnnot.method()[0].asHttpMethod().name() : null;
            String string = value2 = annot.method().length > 0 ? annot.method()[0].asHttpMethod().name() : null;
            if (StringUtils.hasText(value2)) {
                return HttpMethod.valueOf((String)value2);
            }
            if (StringUtils.hasText((String)value1)) {
                return HttpMethod.valueOf((String)value1);
            }
            return null;
        }

        private static @Nullable String initUrl(@Nullable RequestMapping typeAnnot, RequestMapping annot, @Nullable StringValueResolver embeddedValueResolver) {
            String url1 = (typeAnnot != null ? typeAnnot.value().length : 0) > 0 ? typeAnnot.value()[0] : null;
            String url2 = annot.value()[0];
            if (embeddedValueResolver != null) {
                url1 = url1 != null ? embeddedValueResolver.resolveStringValue(url1) : null;
                url2 = embeddedValueResolver.resolveStringValue(url2);
            }
            boolean hasUrl1 = StringUtils.hasText((String)url1);
            boolean hasUrl2 = StringUtils.hasText((String)url2);
            if (hasUrl1 && hasUrl2) {
                return url1 + (!url1.endsWith("/") && !url2.startsWith("/") ? "/" : "") + url2;
            }
            if (!hasUrl1 && !hasUrl2) {
                return null;
            }
            return hasUrl2 ? url2 : url1;
        }

        private static @Nullable MediaType initContentType(@Nullable RequestMapping typeAnnot, RequestMapping annot) {
            String value2;
            String value1 = (typeAnnot != null ? typeAnnot.consumes().length : 0) > 0 ? typeAnnot.consumes()[0] : "";
            String string = value2 = annot.consumes().length > 0 ? annot.consumes()[0] : "";
            if (StringUtils.hasText((String)value2)) {
                return MediaType.parseMediaType((String)value2);
            }
            if (StringUtils.hasText((String)value1)) {
                return MediaType.parseMediaType((String)value1);
            }
            return null;
        }

        private static @Nullable List<MediaType> initAccept(@Nullable RequestMapping typeAnnot, RequestMapping annot) {
            Object[] value1 = typeAnnot != null ? typeAnnot.produces() : null;
            Object[] value2 = annot.produces();
            if (!ObjectUtils.isEmpty((Object[])value2)) {
                return MediaType.parseMediaTypes(Arrays.asList(value2));
            }
            if (!ObjectUtils.isEmpty((Object[])value1)) {
                return MediaType.parseMediaTypes(Arrays.asList(value1));
            }
            return null;
        }

        private static MultiValueMap<String, String> initHeaders(@Nullable RequestMapping typeAnnotation, RequestMapping methodAnnotation, @Nullable StringValueResolver embeddedValueResolver) {
            LinkedMultiValueMap headers = new LinkedMultiValueMap();
            if (typeAnnotation != null) {
                HttpRequestValuesInitializer.addHeaders(typeAnnotation.headers(), embeddedValueResolver, (MultiValueMap<String, String>)headers);
            }
            HttpRequestValuesInitializer.addHeaders(methodAnnotation.headers(), embeddedValueResolver, (MultiValueMap<String, String>)headers);
            return headers;
        }

        private static @Nullable String initVersion(@Nullable RequestMapping typeAnnotation, RequestMapping methodAnnotation) {
            if (StringUtils.hasText((String)methodAnnotation.version())) {
                return methodAnnotation.version();
            }
            if (typeAnnotation != null && StringUtils.hasText((String)typeAnnotation.version())) {
                return typeAnnotation.version();
            }
            return null;
        }

        private static void addHeaders(String[] rawValues, @Nullable StringValueResolver embeddedValueResolver, MultiValueMap<String, String> outputHeaders) {
            for (String rawValue : rawValues) {
                String[] pair = StringUtils.split((String)rawValue, (String)"=");
                if (pair == null) continue;
                String name = pair[0].trim();
                ArrayList<String> values = new ArrayList<String>();
                for (String value : StringUtils.commaDelimitedListToSet((String)pair[1])) {
                    if (embeddedValueResolver != null) {
                        value = embeddedValueResolver.resolveStringValue(value);
                    }
                    if (value == null) continue;
                    value = value.trim();
                    values.add(value);
                }
                if (values.isEmpty()) continue;
                outputHeaders.addAll((Object)name, values);
            }
        }

        private static List<AnnotationDescriptor> getAnnotationDescriptors(AnnotatedElement element) {
            return MergedAnnotations.from((AnnotatedElement)element, (MergedAnnotations.SearchStrategy)MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, (RepeatableContainers)RepeatableContainers.none()).stream(RequestMapping.class).filter(MergedAnnotationPredicates.firstRunOf(MergedAnnotation::getAggregateIndex)).map(AnnotationDescriptor::new).distinct().toList();
        }

        private static class AnnotationDescriptor {
            private final RequestMapping httpExchange;
            private final MergedAnnotation<?> root;

            @SuppressFBWarnings(value={"CT_CONSTRUCTOR_THROW"})
            AnnotationDescriptor(MergedAnnotation<RequestMapping> mergedAnnotation) {
                this.httpExchange = (RequestMapping)mergedAnnotation.synthesize();
                this.root = mergedAnnotation.getRoot();
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            public boolean equals(Object obj) {
                if (!(obj instanceof AnnotationDescriptor)) return false;
                AnnotationDescriptor that = (AnnotationDescriptor)obj;
                if (!this.httpExchange.equals((Object)that.httpExchange)) return false;
                return true;
            }

            public int hashCode() {
                return this.httpExchange.hashCode();
            }

            public String toString() {
                return this.root.synthesize().toString();
            }
        }
    }

    private record ReactorExchangeResponseFunction(Function<HttpRequestValues, Publisher<?>> responseFunction, @Nullable ReactiveAdapter returnTypeAdapter, boolean blockForOptional, @Nullable Duration blockTimeout) implements ResponseFunction
    {
        @Override
        public @Nullable Object execute(HttpRequestValues requestValues) {
            Publisher<?> responsePublisher = this.responseFunction.apply(requestValues);
            if (this.returnTypeAdapter != null) {
                return this.returnTypeAdapter.fromPublisher(responsePublisher);
            }
            if (this.blockForOptional) {
                return this.blockTimeout != null ? ((Mono)responsePublisher).blockOptional(this.blockTimeout) : ((Mono)responsePublisher).blockOptional();
            }
            return this.blockTimeout != null ? ((Mono)responsePublisher).block(this.blockTimeout) : ((Mono)responsePublisher).block();
        }

        public static ResponseFunction create(ReactorHttpExchangeAdapter client, Method method) {
            Function<HttpRequestValues, Publisher<?>> responseFunction;
            Class actualType;
            ReactiveAdapter reactiveAdapter;
            MethodParameter returnParam = new MethodParameter(method, -1);
            Class<Mono> returnType = returnParam.getParameterType();
            boolean isSuspending = KotlinDetector.isSuspendingFunction((Method)method);
            if (isSuspending) {
                returnType = Mono.class;
            }
            MethodParameter actualParam = (reactiveAdapter = client.getReactiveAdapterRegistry().getAdapter(returnType)) != null ? returnParam.nested() : returnParam.nestedIfOptional();
            Class clazz = actualType = isSuspending ? actualParam.getParameterType() : actualParam.getNestedParameterType();
            if (ClassUtils.isVoidType((Class)actualType)) {
                responseFunction = arg_0 -> ((ReactorHttpExchangeAdapter)client).exchangeForMono(arg_0);
            } else if (reactiveAdapter != null && reactiveAdapter.isNoValue()) {
                responseFunction = arg_0 -> ((ReactorHttpExchangeAdapter)client).exchangeForMono(arg_0);
            } else if (actualType.equals(HttpHeaders.class)) {
                responseFunction = arg_0 -> ((ReactorHttpExchangeAdapter)client).exchangeForHeadersMono(arg_0);
            } else if (actualType.equals(ResponseEntity.class)) {
                MethodParameter bodyParam = isSuspending ? actualParam : actualParam.nested();
                Class bodyType = bodyParam.getNestedParameterType();
                if (bodyType.equals(Void.class)) {
                    responseFunction = arg_0 -> ((ReactorHttpExchangeAdapter)client).exchangeForBodilessEntityMono(arg_0);
                } else {
                    ReactiveAdapter bodyAdapter = client.getReactiveAdapterRegistry().getAdapter(bodyType);
                    responseFunction = ReactorExchangeResponseFunction.initResponseEntityFunction(client, bodyParam, bodyAdapter, isSuspending);
                }
            } else {
                responseFunction = ReactorExchangeResponseFunction.initBodyFunction(client, actualParam, reactiveAdapter, isSuspending);
            }
            return new ReactorExchangeResponseFunction(responseFunction, reactiveAdapter, returnType.equals(Optional.class), client.getBlockTimeout());
        }

        private static Function<HttpRequestValues, Publisher<?>> initResponseEntityFunction(ReactorHttpExchangeAdapter client, MethodParameter methodParam, @Nullable ReactiveAdapter reactiveAdapter, boolean isSuspending) {
            if (reactiveAdapter == null) {
                return request -> client.exchangeForEntityMono(request, ParameterizedTypeReference.forType((Type)methodParam.getNestedGenericParameterType()));
            }
            Assert.isTrue((boolean)reactiveAdapter.isMultiValue(), (String)"ResponseEntity body must be a concrete value or a multi-value Publisher");
            ParameterizedTypeReference bodyType = ParameterizedTypeReference.forType((Type)(isSuspending ? methodParam.nested().getGenericParameterType() : methodParam.nested().getNestedGenericParameterType()));
            if (reactiveAdapter.getReactiveType().equals(Flux.class)) {
                return request -> client.exchangeForEntityFlux(request, bodyType);
            }
            return request -> client.exchangeForEntityFlux(request, bodyType).map(entity -> {
                Flux entityBody = (Flux)entity.getBody();
                Assert.state((entityBody != null ? 1 : 0) != 0, (String)"Entity body must not be null");
                Object body = reactiveAdapter.fromPublisher((Publisher)entityBody);
                return new ResponseEntity(body, entity.getHeaders(), entity.getStatusCode());
            });
        }

        private static Function<HttpRequestValues, Publisher<?>> initBodyFunction(ReactorHttpExchangeAdapter client, MethodParameter methodParam, @Nullable ReactiveAdapter reactiveAdapter, boolean isSuspending) {
            ParameterizedTypeReference bodyType = ParameterizedTypeReference.forType((Type)(isSuspending ? methodParam.getGenericParameterType() : methodParam.getNestedGenericParameterType()));
            return reactiveAdapter != null && reactiveAdapter.isMultiValue() ? request -> client.exchangeForBodyFlux(request, bodyType) : request -> client.exchangeForBodyMono(request, bodyType);
        }
    }

    private static interface ResponseFunction {
        public @Nullable Object execute(HttpRequestValues var1);
    }

    private record ExchangeResponseFunction(Function<HttpRequestValues, @Nullable Object> responseFunction) implements ResponseFunction
    {
        @Override
        public @Nullable Object execute(HttpRequestValues requestValues) {
            return this.responseFunction.apply(requestValues);
        }

        public static ResponseFunction create(HttpExchangeAdapter client, Method method) {
            Function<HttpRequestValues, Object> responseFunction;
            if (KotlinDetector.isSuspendingFunction((Method)method)) {
                throw new IllegalStateException("Kotlin Coroutines are only supported with reactive implementations");
            }
            MethodParameter param = new MethodParameter(method, -1).nestedIfOptional();
            Class paramType = param.getNestedParameterType();
            if (ClassUtils.isVoidType((Class)paramType)) {
                responseFunction = requestValues -> {
                    client.exchange(requestValues);
                    return null;
                };
            } else if (paramType.equals(HttpHeaders.class)) {
                responseFunction = request -> ExchangeResponseFunction.asOptionalIfNecessary(client.exchangeForHeaders(request), param);
            } else if (paramType.equals(ResponseEntity.class)) {
                MethodParameter bodyParam = param.nested();
                if (bodyParam.getNestedParameterType().equals(Void.class)) {
                    responseFunction = request -> ExchangeResponseFunction.asOptionalIfNecessary(client.exchangeForBodilessEntity(request), param);
                } else {
                    ParameterizedTypeReference bodyTypeRef = ParameterizedTypeReference.forType((Type)bodyParam.getNestedGenericParameterType());
                    responseFunction = request -> ExchangeResponseFunction.asOptionalIfNecessary(client.exchangeForEntity(request, bodyTypeRef), param);
                }
            } else {
                ParameterizedTypeReference bodyTypeRef = ParameterizedTypeReference.forType((Type)param.getNestedGenericParameterType());
                responseFunction = request -> ExchangeResponseFunction.asOptionalIfNecessary(client.exchangeForBody(request, bodyTypeRef), param);
            }
            return new ExchangeResponseFunction(responseFunction);
        }

        private static @Nullable Object asOptionalIfNecessary(@Nullable Object response, MethodParameter param) {
            return param.getParameterType().equals(Optional.class) ? Optional.ofNullable(response) : response;
        }
    }
}

