package com.kuma.boot.data.jpa.fenix.ar;

import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import org.springframework.util.Assert;

public interface BaseModel<R> {
   default R getRepository() {
      return (R)RepositoryModelContext.getRepositoryObject(this.getRepositoryBeanName(), this.getClass().getName(), this::validRepository, this::validExecutor);
   }

   default void validRepository(Object repository) {
   }

   default void validExecutor(Object specExecutor) {
   }

   default void assertNotNullRepository(Object repository) {
      Assert.notNull(repository, StringHelper.format("【Fenix 异常】获取到 Spring 容器中 Spring Data JPA 的 Repository 接口的 Bean【{}】为 Null。", this.getRepositoryBeanName()));
   }

   default String getRepositoryBeanName() {
      String entityName = this.getClass().getSimpleName();
      String var10000 = entityName.substring(0, 1).toLowerCase();
      return var10000 + entityName.substring(1) + "Repository";
   }
}
