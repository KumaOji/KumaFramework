package com.kuma.boot.data.jpa.fenix.ar.spec;

import com.kuma.boot.data.jpa.fenix.ar.BaseModel;
import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpecModel<T, R extends JpaSpecificationExecutor<T>> extends BaseModel<R> {
   default void validExecutor(Object repository) {
      this.assertNotNullRepository(repository);
      if (!(repository instanceof JpaSpecificationExecutor)) {
         throw new FenixException(StringHelper.format("【Fenix 异常】获取到的 Spring Data JPA 的 Repository 接口【{}】不是真正的 JpaSpecificationExecutor 接口。", repository.getClass().getName()));
      }
   }
}
