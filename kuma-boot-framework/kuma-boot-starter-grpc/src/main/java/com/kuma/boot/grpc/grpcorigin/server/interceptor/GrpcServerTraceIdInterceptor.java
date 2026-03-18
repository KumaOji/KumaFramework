package com.kuma.boot.grpc.grpcorigin.server.interceptor;

import com.kuma.boot.common.utils.servlet.TraceUtils;
import com.kuma.boot.grpc.grpcorigin.server.properties.GrpcServerProperties;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Metadata.Key;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

@Order(Integer.MIN_VALUE)
public class GrpcServerTraceIdInterceptor implements ServerInterceptor {
   private final Metadata.Key<String> traceIdKey;

   public GrpcServerTraceIdInterceptor(GrpcServerProperties properties) {
      this.traceIdKey = Key.of(properties.getTraceIdKey(), Metadata.ASCII_STRING_MARSHALLER);
   }

   protected String traceId(Metadata headers) {
      String traceId = null;
      if (headers.containsKey(this.traceIdKey)) {
         traceId = headers.get(this.traceIdKey);
      }

      return StringUtils.hasText(traceId) ? traceId : TraceUtils.getTraceId();
   }

   public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
      String traceId = this.traceId(headers);
      MDC.put("TRACE_ID", traceId);

      ServerCall.Listener var5;
      try {
         headers.put(this.traceIdKey, traceId);
         var5 = next.startCall(call, headers);
      } finally {
         MDC.remove("TRACE_ID");
      }

      return var5;
   }
}
