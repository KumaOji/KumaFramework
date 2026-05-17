package com.kuma.boot.eventbus.disruptor.tmp;

import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.core.utils.reflect.AnnotationUtils;
import com.kuma.boot.eventbus.disruptor.tmp.annotation.DisruptorEvent;
import com.kuma.boot.eventbus.disruptor.tmp.annotation.EnableDisruptor;
import com.kuma.boot.eventbus.disruptor.tmp.exception.DisruptorRegistrarException;
import java.util.Arrays;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

public class DisruptorRegistrar implements ImportBeanDefinitionRegistrar {
   public DisruptorRegistrar() {
   }

   public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry, @NonNull BeanNameGenerator beanNameGenerator) {
      AnnotationAttributes attributes = AnnotationUtils.attributesFor(importingClassMetadata, EnableDisruptor.class);
      String[] basePackages = this.getBasePackages(attributes);
      if (ObjectUtils.isEmpty(basePackages)) {
         throw new DisruptorRegistrarException(EnableDisruptor.class.getName() + " required basePackages or basePackageClasses");
      } else {
         ClassPathDisruptorScanner scanner = new ClassPathDisruptorScanner(registry, beanNameGenerator);
         scanner.registerFilters(DisruptorEvent.class);
         scanner.scan(basePackages);
      }
   }

   private String[] getBasePackages(AnnotationAttributes attributes) {
      String[] basePackages = attributes.getStringArray("basePackages");
      if (ObjectUtils.isEmpty(basePackages)) {
         Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
         basePackages = (String[])Arrays.stream(basePackageClasses).map(Class::getPackageName).distinct().toArray((x$0) -> new String[x$0]);
      }

      return basePackages;
   }
}
