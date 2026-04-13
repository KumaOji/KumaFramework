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
            throw new NoSuchBeanDefinitionException(StringHelper.format("\u3010Fenix \u5f02\u5e38\u3011\u83b7\u53d6\u5b9e\u4f53\u7c7b\u3010{}\u3011\u6240\u5bf9\u5e94\u7684 Spring Data JPA \u7684 Repository Bean\u3010{}\u3011\u7684\u5b9e\u4f8b\u4e3a Null\uff0c\u8bf7\u5148\u5b9a\u4e49\u8be5\u5b9e\u4f53\u7c7b\u7684 Repository \u63a5\u53e3\uff0c\u5e76\u6807\u6ce8\u3010@Repository\u3011\u6ce8\u89e3\u3002", entityClassName, key));
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
