package com.kuma.boot.grpc.grpcorigin.client;

import com.kuma.boot.grpc.grpcorigin.client.properties.GrpcClientProperties;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.AbstractAsyncStub;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractFutureStub;
import io.grpc.stub.AbstractStub;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.net.ssl.SSLException;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.CollectionUtils;

public class GrpcClientProvide {
   protected final GrpcClientProperties properties;
   protected final List interceptors;

   public GrpcClientProvide(GrpcClientProperties properties, List interceptors) {
      this.properties = properties;
      this.interceptors = interceptors;
   }

   public ManagedChannel channel() {
      return this.channel(this.properties.getHost(), this.properties.getPort());
   }

   public ManagedChannel channel(String host, Integer port) {
      return this.channel(host, port, (builder) -> builder);
   }

   public ManagedChannel channel(String host, Integer port, UnaryOperator operator) {
      if (port != null && port >= 0) {
         return this.channel(String.format("%s:%d", host, port), operator);
      } else {
         throw new IllegalArgumentException("kuma.boot.grpc.client.port值不能为: " + port);
      }
   }

   public ManagedChannel channel(String target, UnaryOperator operator) {
      ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forTarget(target);
      ManagedChannelBuilder<?> apply = (ManagedChannelBuilder)operator.apply(builder);
      return this.channel(apply);
   }

   public ManagedChannel channel(ManagedChannelBuilder builder) {
      if (this.properties.isEnableKeepAlive()) {
         builder.keepAliveTime(this.properties.getKeepAliveTime(), TimeUnit.MILLISECONDS).keepAliveTimeout(this.properties.getKeepAliveTimeout(), TimeUnit.MILLISECONDS);
      }

      if (this.properties.isUsePlaintext()) {
         builder.usePlaintext();
      }

      if (this.properties.isEnableRetry()) {
         builder.enableRetry();
      }

      if (!CollectionUtils.isEmpty(this.interceptors)) {
         this.interceptors.sort(AnnotationAwareOrderComparator.INSTANCE);
         builder.intercept(this.interceptors);
      }

      this.buildSsl(builder);
      return builder.build();
   }

   public AbstractStub stub(Channel channel, Function function) {
      return (AbstractStub)function.apply(channel);
   }

   public AbstractAsyncStub async(Channel channel, Function function) {
      return (AbstractAsyncStub)this.stub(channel, function);
   }

   public AbstractBlockingStub blocking(Channel channel, Function function) {
      return (AbstractBlockingStub)this.stub(channel, function);
   }

   public AbstractFutureStub future(Channel channel, Function function) {
      return (AbstractFutureStub)this.stub(channel, function);
   }

   protected void buildSsl(ManagedChannelBuilder builder) {
      if (!this.properties.isUsePlaintext() && this.properties.isDisableSsl() && builder instanceof NettyChannelBuilder) {
         SslContextBuilder sslContextBuilder = GrpcSslContexts.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE);

         try {
            ((NettyChannelBuilder)builder).sslContext(sslContextBuilder.build());
         } catch (SSLException e) {
            throw new RuntimeException(e);
         }
      }

   }
}
