package com.kuma.boot.data.jpa.fenix.ar;

import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

public final class RepositoryModelContext {
   private static final Map<String, Object> repositoryMap = new ConcurrentHashMap();
   private static ApplicationContext applicationContext;

   public RepositoryModelContext() {
   }

   public static void setApplicationContext(ApplicationContext applicationContext) {
      RepositoryModelContext.applicationContext = applicationContext;
   }

   public static Object getRepositoryObject(String repositoryBeanName, String entityClassName, Consumer<Object> repoValidConsumer, Consumer<Object> executorValidConsumer) {
      return repositoryMap.computeIfAbsent(repositoryBeanName, (key) -> {
         if (!applicationContext.containsBean(key)) {
            throw new NoSuchBeanDefinitionException(StringHelper.format("【Fenix 异常】获取实体类【{}】所对应的 Spring Data JPA 的 Repository Bean【{}】的实例为 Null，请先定义该实体类的 Repository 接口，并标注【@Repository】注解。", entityClassName, key));
         } else {
            Object repository = applicationContext.getBean(key);
            if (repoValidConsumer != null) {
               repoValidConsumer.accept(repository);
            }

            if (executorValidConsumer != null) {
               executorValidConsumer.accept(repository);
            }

            return repository;
         }
      });
   }
}
