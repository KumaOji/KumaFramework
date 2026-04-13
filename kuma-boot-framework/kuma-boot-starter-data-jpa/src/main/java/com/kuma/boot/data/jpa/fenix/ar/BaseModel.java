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
      Assert.notNull(repository, StringHelper.format("\u3010Fenix \u5f02\u5e38\u3011\u83b7\u53d6\u5230 Spring \u5bb9\u5668\u4e2d Spring Data JPA \u7684 Repository \u63a5\u53e3\u7684 Bean\u3010{}\u3011\u4e3a Null\u3002", this.getRepositoryBeanName()));
   }

   default String getRepositoryBeanName() {
      String entityName = this.getClass().getSimpleName();
      String var10000 = entityName.substring(0, 1).toLowerCase();
      return var10000 + entityName.substring(1) + "Repository";
   }
}
