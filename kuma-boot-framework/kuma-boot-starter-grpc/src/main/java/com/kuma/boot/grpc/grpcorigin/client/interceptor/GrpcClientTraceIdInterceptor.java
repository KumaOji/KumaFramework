package com.kuma.boot.grpc.grpcorigin.client.interceptor;

import com.kuma.boot.grpc.grpcorigin.client.properties.GrpcClientProperties;
import com.kuma.boot.grpc.grpcorigin.client.sample.SimpleForwardingClientCall;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Metadata.Key;
import java.util.Objects;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

@Order(Integer.MIN_VALUE)
public class GrpcClientTraceIdInterceptor implements ClientInterceptor {
   private final Metadata.Key traceIdKey;

   public GrpcClientTraceIdInterceptor(GrpcClientProperties properties) {
      this.traceIdKey = Key.of(properties.getTraceIdKey(), Metadata.ASCII_STRING_MARSHALLER);
   }

   protected String traceId() {
      return MDC.get("TRACE_ID");
   }

   public ClientCall interceptCall(MethodDescriptor method, CallOptions callOptions, Channel next) {
      final String traceId = this.traceId();
      ClientCall<S, R> call = next.newCall(method, callOptions);
      return new SimpleForwardingClientCall(call) {
         {
            Objects.requireNonNull(GrpcClientTraceIdInterceptor.this);
         }

         public void onStartBefore(ClientCall.Listener responseListener, Metadata headers) {
            if (StringUtils.hasText(traceId)) {
               headers.put(GrpcClientTraceIdInterceptor.this.traceIdKey, traceId);
            }

         }
      };
   }
}
