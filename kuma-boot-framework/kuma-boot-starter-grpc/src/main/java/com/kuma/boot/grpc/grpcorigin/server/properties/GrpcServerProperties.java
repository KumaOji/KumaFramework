package com.kuma.boot.grpc.grpcorigin.server.properties;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties("kuma.boot.grpc.server")
public class GrpcServerProperties {
   public static final String PREFIX = "kuma.boot.grpc.server";
   private Integer port;
   private String traceIdKey = "TRACE_ID";
   private DataSize messageSize = DataSize.ofMegabytes(512L);
   private long keepAliveTime;
   private long keepAliveTimeout;

   public GrpcServerProperties() {
      this.keepAliveTime = TimeUnit.HOURS.toMillis(2L);
      this.keepAliveTimeout = TimeUnit.SECONDS.toMillis(20L);
   }

   public Integer getPort() {
      return this.port;
   }

   public void setPort(Integer port) {
      this.port = port;
   }

   public String getTraceIdKey() {
      return this.traceIdKey;
   }

   public void setTraceIdKey(String traceIdKey) {
      this.traceIdKey = traceIdKey;
   }

   public DataSize getMessageSize() {
      return this.messageSize;
   }

   public void setMessageSize(DataSize messageSize) {
      this.messageSize = messageSize;
   }

   public long getKeepAliveTime() {
      return this.keepAliveTime;
   }

   public void setKeepAliveTime(long keepAliveTime) {
      this.keepAliveTime = keepAliveTime;
   }

   public long getKeepAliveTimeout() {
      return this.keepAliveTimeout;
   }

   public void setKeepAliveTimeout(long keepAliveTimeout) {
      this.keepAliveTimeout = keepAliveTimeout;
   }
}
