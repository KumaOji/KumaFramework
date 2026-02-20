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

package com.kuma.boot.web.mvc.interceptor;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截器： 在使用otlp时，  io.opentelemetry.context.ContextStorageWrappers由于mysql执行提前初始化
 * 导致OpenTelemetryAutoConfiguration.java中初始化失败， otelCurrentTraceContext
 * 主要是添加OtelTracer.EventPublisher失败   使用默认的ThreadLocalContextStorage 导致在mdc中获取不到traceId spanid等信息。
 * <p>
 * 在这里重新设置一下
 *
 * ServerHttpObservationFilter
 *
 * WebMvcObservationAutoConfiguration
 *
 * OpenTelemetryAutoConfiguration
 *
 *
 * io.micrometer.observation.notifyOnScopeOpened
 *
 * for (ObservationHandler handler : this.handlers) {
 * 	//FirstMatchingCompositeObservationHandler
 *
 *             handler.onScopeOpened(this.context);
 *         }
 *
 *
 * io.micrometer.tracing.handler.PropagatingReceiverTracingObservationHandler
 *
 *
 * io.micrometer.tracing.handler.TracingObservationHandler   onScopeOpened
 *
 * io.micrometer.tracing.handler.TracingObservationHandler   setMaybeScopeOnTracingContext
 *
 *
 * io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext maybeScope
 * io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext newScope
 *
 *
 *   default Scope makeCurrent() {
 *     return ContextStorage.get().attach(this);
 *   }
 * io.opentelemetry.context.Context              makeCurrent
 *
 *   default Scope makeCurrent() {
 *     return ContextStorage.get().attach(this);      //ThreadLocalContextStorage
 *   }
 *
 *
 * EventPublishingContextWrapper.  apply
 *  public ContextStorage apply(final ContextStorage contextStorage) {
 *     return new ContextStorage() {
 *       public Scope attach(Context context) {
 *         Context currentContext = Context.current();
 *         Scope scope = contextStorage.attach(context);
 *         if (scope == Scope.noop()) {
 *           return scope;
 *         } else {
 *           EventPublishingContextWrapper.this.publisher.publishEvent(new ScopeAttachedEvent(context));
 *           return () -> {
 *             scope.close();
 *             EventPublishingContextWrapper.this.publisher.publishEvent(new ScopeClosedEvent());
 *             EventPublishingContextWrapper.this.publisher.publishEvent(new ScopeRestoredEvent(currentContext));
 *           };
 *         }
 *       }
 *
 *       public Context current() {
 *         return contextStorage.current();
 *       }
 *     };
 *   }
 * io.micrometer.tracing.otel.bridge             apply
 *
 * <p>该拦截器要优先于系统中其他的业务拦截器
 *
 * <p>
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 22:01:25
 */
public class TraceMdcInterceptor implements HandlerInterceptor {

    @Autowired(required = false)
    private Tracer tracer;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String traceIdByRequest = TraceUtils.getTraceIdByRequest(request);
        if (StrUtil.isBlank(traceIdByRequest)) {
            if(tracer != null){
                Span currentSpan = tracer.currentSpan();
                if (currentSpan != null) {
                    traceIdByRequest = currentSpan.context().traceId();
                }
            }
//            traceIdByRequest =
//                    Objects.requireNonNull(tracer.currentTraceContext().context()).traceId();
        }
        TraceUtils.setTraceId(traceIdByRequest);

        String spanIdByRequest = TraceUtils.getSpanIdByRequest(request);
        if (StrUtil.isBlank(spanIdByRequest)) {
            if(tracer != null){
                Span currentSpan = tracer.currentSpan();
                if (currentSpan != null) {
                    spanIdByRequest = currentSpan.context().spanId();
                }
            }
//            spanIdByRequest =
//                    Objects.requireNonNull(tracer.currentTraceContext().context()).spanId();
        }
        TraceUtils.setSpanId(spanIdByRequest);

        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

        TraceUtils.removeTraceId();
        TraceUtils.removeSpanId();
    }
}
