package com.kuma.boot.grpc.spring.annotation;

import com.kuma.boot.common.utils.lang.ObjectUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.grpc.client.GrpcClientFactory;
import org.springframework.util.ReflectionUtils;

public record GrpcClientBeanPostProcessor(GrpcClientFactory grpcClientFactory, DiscoveryClient discoveryClient) implements BeanPostProcessor {
   public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
      Class<?> clazz = bean.getClass();

      do {
         this.setFields(bean, clazz);
         this.setMethods(bean, clazz);
         clazz = clazz.getSuperclass();
      } while(ObjectUtils.isNotNull(clazz));

      return bean;
   }

   private void setFields(Object bean, Class clazz) {
      for(Field field : clazz.getDeclaredFields()) {
         GrpcClient grpcClient = (GrpcClient)AnnotationUtils.findAnnotation(field, GrpcClient.class);
         if (ObjectUtils.isNotNull(grpcClient)) {
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, bean, this.getClient(field.getType(), grpcClient));
         }
      }

   }

   private void setMethods(Object bean, Class clazz) {
      for(Method method : clazz.getDeclaredMethods()) {
         GrpcClient grpcClient = (GrpcClient)AnnotationUtils.findAnnotation(method, GrpcClient.class);
         if (ObjectUtils.isNotNull(grpcClient)) {
            ReflectionUtils.makeAccessible(method);
            ReflectionUtils.invokeMethod(method, bean, new Object[]{this.getClient(method.getParameterTypes()[0], grpcClient)});
         }
      }

   }

   @SuppressWarnings("unchecked")
   private Object getClient(Class<?> type, @NonNull GrpcClient grpcClient) {
      String target = String.format("discovery://%s", grpcClient.serviceId());
      return this.grpcClientFactory.getClient(target, type, (Class<?>) null);
   }
}
