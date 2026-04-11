package com.kuma.boot.apollo.namespace;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

public class ApolloConfigInterceptRegistry implements ImportBeanDefinitionRegistrar {
   public ApolloConfigInterceptRegistry() {
   }

   public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
      AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableApolloConfig.class.getName()));
      if (attributes != null) {
         String[] namespaces = attributes.getStringArray("value");

         for(String namespace : namespaces) {
            NamespaceManager.addNamespace(namespace);
         }

      }
   }
}
