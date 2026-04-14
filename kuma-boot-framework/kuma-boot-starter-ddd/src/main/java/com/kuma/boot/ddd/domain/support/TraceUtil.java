package com.kuma.boot.ddd.domain.support;

import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class TraceUtil {
   private final ObjectProvider<Tracer> tracerProvider;

   public TraceUtil(ObjectProvider<Tracer> tracerProvider) {
      this.tracerProvider = tracerProvider;
   }

   public String getTraceId() {
      TraceContext context = this.getContext();
      return ObjectUtils.isNull(context) ? TraceUtils.getTraceId() : context.traceId();
   }

   public String getSpanId() {
      TraceContext context = this.getContext();
      return ObjectUtils.isNull(context) ? TraceUtils.getSpanId() : context.spanId();
   }

   private TraceContext getContext() {
      Tracer tracer = this.tracerProvider.getIfAvailable();
      if (tracer == null) {
         return null;
      }
      CurrentTraceContext current = tracer.currentTraceContext();
      return current == null ? null : current.context();
   }
}
