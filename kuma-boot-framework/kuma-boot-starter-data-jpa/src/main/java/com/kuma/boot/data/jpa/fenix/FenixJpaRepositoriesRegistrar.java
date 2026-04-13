package com.kuma.boot.data.jpa.fenix;

import java.lang.annotation.Annotation;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

public class FenixJpaRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {
   public FenixJpaRepositoriesRegistrar() {
   }

   protected Class<? extends Annotation> getAnnotation() {
      return EnableFenix.class;
   }

   protected RepositoryConfigurationExtension getExtension() {
      return new JpaRepositoryConfigExtension();
   }
}
