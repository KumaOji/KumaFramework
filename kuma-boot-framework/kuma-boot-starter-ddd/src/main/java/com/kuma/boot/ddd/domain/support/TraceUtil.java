package com.kuma.boot.ddd.domain.support;

import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.servlet.TraceUtils;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.springframework.stereotype.Component;

@Component
public class TraceUtil {
   private final Tracer tracer;

   public TraceUtil(Tracer tracer) {
      this.tracer = tracer;
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
      return this.tracer.currentTraceContext().context();
   }
}
