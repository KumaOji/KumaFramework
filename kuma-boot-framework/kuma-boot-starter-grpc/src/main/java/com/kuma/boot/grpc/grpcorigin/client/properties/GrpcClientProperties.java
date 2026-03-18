package com.kuma.boot.grpc.grpcorigin.client.properties;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kuma.boot.grpc.client")
public class GrpcClientProperties {
   public static final String PREFIX = "kuma.boot.grpc.client";
   private String host;
   private Integer port = 80;
   private String traceIdKey = "TRACE_ID";
   private boolean usePlaintext = false;
   private boolean disableSsl = false;
   private boolean enableRetry = true;
   private boolean enableKeepAlive = true;
   private long keepAliveTime;
   private long keepAliveTimeout;

   public GrpcClientProperties() {
      this.keepAliveTime = TimeUnit.MINUTES.toMillis(30L);
      this.keepAliveTimeout = TimeUnit.SECONDS.toMillis(2L);
   }

   public String getHost() {
      return this.host;
   }

   public void setHost(String host) {
      this.host = host;
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

   public boolean isUsePlaintext() {
      return this.usePlaintext;
   }

   public void setUsePlaintext(boolean usePlaintext) {
      this.usePlaintext = usePlaintext;
   }

   public boolean isDisableSsl() {
      return this.disableSsl;
   }

   public void setDisableSsl(boolean disableSsl) {
      this.disableSsl = disableSsl;
   }

   public boolean isEnableRetry() {
      return this.enableRetry;
   }

   public void setEnableRetry(boolean enableRetry) {
      this.enableRetry = enableRetry;
   }

   public boolean isEnableKeepAlive() {
      return this.enableKeepAlive;
   }

   public void setEnableKeepAlive(boolean enableKeepAlive) {
      this.enableKeepAlive = enableKeepAlive;
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
