package com.kuma.boot.grpc.grpcorigin.server;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ReflectUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.runtime.processor.ContextComponent;
import com.kuma.boot.grpc.grpcorigin.server.properties.GrpcServerProperties;
import io.grpc.BindableService;
import io.grpc.MethodDescriptor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptor;
import io.grpc.ServerMethodDefinition;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

public class GrpcServer implements ContextComponent {
   private final Server server;
   private final Map serviceNameMap;
   private final Map fullMethodNameMap;

   public GrpcServer(GrpcServerProperties properties, List interceptors, List services) {
      this(ServerBuilder.forPort(properties.getPort()), properties, interceptors, services);
   }

   public GrpcServer(ServerBuilder builder, GrpcServerProperties properties, List interceptors, List services) {
      builder.maxInboundMessageSize((int)properties.getMessageSize().toBytes()).keepAliveTime(properties.getKeepAliveTime(), TimeUnit.MILLISECONDS).keepAliveTimeout(properties.getKeepAliveTimeout(), TimeUnit.MILLISECONDS);
      interceptors.sort(AnnotationAwareOrderComparator.INSTANCE);
      ListIterator<ServerInterceptor> iterator = interceptors.listIterator(interceptors.size());

      while(iterator.hasPrevious()) {
         builder.intercept((ServerInterceptor)iterator.previous());
      }

      this.serviceNameMap = new HashMap();
      this.fullMethodNameMap = new HashMap();

      for(BindableService service : services) {
         builder.addService(service);
         Class<? extends BindableService> cls = service.getClass();
         ServerServiceDefinition serverServiceDefinition = service.bindService();
         ServiceDescriptor serviceDescriptor = serverServiceDefinition.getServiceDescriptor();
         this.serviceNameMap.put(serviceDescriptor.getName(), cls);

         for(ServerMethodDefinition serverMethodDefinition : serverServiceDefinition.getMethods()) {
            MethodDescriptor<?, ?> methodDescriptor = serverMethodDefinition.getMethodDescriptor();
            String fullMethodName = methodDescriptor.getFullMethodName();
            this.fullMethodNameMap.put(fullMethodName, this.resolve(methodDescriptor, cls));
         }
      }

      this.server = builder.build();
   }

   public boolean isRunning() {
      return !this.server.isShutdown() && !this.server.isTerminated();
   }

   public int port() {
      return this.server.getPort();
   }

   public Class findClass(ServiceDescriptor descriptor) {
      return (Class)this.serviceNameMap.get(descriptor.getName());
   }

   public Class findClass(MethodDescriptor descriptor) {
      Method method = this.findMethod(descriptor);
      return method.getDeclaringClass();
   }

   public Method findMethod(MethodDescriptor descriptor) {
      return (Method)this.fullMethodNameMap.get(descriptor.getFullMethodName());
   }

   protected Method resolve(MethodDescriptor descriptor, Class cls) {
      String bareMethodName = descriptor.getBareMethodName();

      for(Method method : ReflectUtil.getMethods(cls)) {
         if (Objects.equals(method.getName(), bareMethodName)) {
            return method;
         }
      }

      return null;
   }

   public void onApplicationStart() {
      try {
         this.server.start();
         LogUtils.debug("grpc服务启动. 端口: {}", new Object[]{this.server.getPort()});
         ThreadUtil.execute(() -> {
            try {
               this.server.awaitTermination();
            } catch (InterruptedException e) {
               throw new RuntimeException(e);
            }
         });
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public void onApplicationStop() {
      LogUtils.warn("grpc服务开始关闭", new Object[0]);
      this.server.shutdownNow();
      LogUtils.warn("grpc服务关闭", new Object[0]);
   }
}
