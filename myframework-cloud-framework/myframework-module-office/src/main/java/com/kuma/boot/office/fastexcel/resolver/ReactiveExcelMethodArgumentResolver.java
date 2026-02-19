/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.bean.BeanUtils
 *  com.kuma.boot.common.utils.io.FileUtils
 *  org.jspecify.annotations.NonNull
 *  org.reactivestreams.Publisher
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ReactiveAdapter
 *  org.springframework.core.ReactiveAdapterRegistry
 *  org.springframework.core.ResolvableType
 *  org.springframework.http.ReactiveHttpInputMessage
 *  org.springframework.http.codec.multipart.Part
 *  org.springframework.web.reactive.BindingContext
 *  org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
 *  org.springframework.web.server.ServerWebExchange
 *  reactor.core.publisher.Mono
 */
package com.kuma.boot.office.fastexcel.resolver;

import com.kuma.boot.common.utils.bean.BeanUtils;
import com.kuma.boot.common.utils.io.FileUtils;
import com.kuma.boot.office.fastexcel.PartServerHttpRequest;
import com.kuma.boot.office.fastexcel.annotation.ExcelParam;
import com.kuma.boot.office.fastexcel.annotation.RequestExcel;
import com.kuma.boot.office.fastexcel.converter.ExcelHttpMessageReader;
import com.kuma.boot.office.fastexcel.listener.ExcelMapReadListener;
import java.util.HashMap;
import org.jspecify.annotations.NonNull;
import org.reactivestreams.Publisher;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ReactiveExcelMethodArgumentResolver
implements HandlerMethodArgumentResolver {
    private final ReactiveAdapterRegistry adapterRegistry = ReactiveAdapterRegistry.getSharedInstance();
    private final ExcelHttpMessageReader reader = new ExcelHttpMessageReader();

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasMethodAnnotation(RequestExcel.class) && parameter.hasParameterAnnotation(ExcelParam.class);
    }

    public @NonNull Mono<Object> resolveArgument(@NonNull MethodParameter parameter, @NonNull BindingContext bindingContext, @NonNull ServerWebExchange exchange) {
        Class resolvedType = ResolvableType.forMethodParameter((MethodParameter)parameter).resolve();
        ReactiveAdapter adapter = resolvedType != null ? this.adapterRegistry.getAdapter(resolvedType) : null;
        RequestExcel requestExcel = (RequestExcel)parameter.getMethodAnnotation(RequestExcel.class);
        ExcelParam excelParam = (ExcelParam)parameter.getParameterAnnotation(ExcelParam.class);
        ResolvableType resolvableType = ResolvableType.forMethodParameter((MethodParameter)parameter);
        Mono mono = Mono.empty();
        if (requestExcel != null && excelParam != null && this.reader.canRead(resolvableType, exchange.getRequest().getHeaders().getContentType())) {
            mono = FileUtils.getPartValues((String)excelParam.fileName(), (ServerWebExchange)exchange).flatMap(part -> {
                ExcelMapReadListener listener = (ExcelMapReadListener)BeanUtils.instantiateClass(requestExcel.parse());
                HashMap<String, Object> hints = new HashMap<String, Object>();
                hints.put("listener", listener);
                hints.put("requestExcel", requestExcel);
                PartServerHttpRequest request = new PartServerHttpRequest(exchange.getRequest(), (Part)part);
                return this.reader.readMono(resolvableType, (ReactiveHttpInputMessage)request, hints);
            });
        }
        return adapter != null ? Mono.just((Object)adapter.fromPublisher((Publisher)mono)) : Mono.from((Publisher)mono);
    }
}

