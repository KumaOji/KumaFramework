package com.kuma.boot.data.jpa.fenix.ar.spec;

import com.kuma.boot.data.jpa.fenix.ar.BaseModel;
import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpecModel<T, R extends JpaSpecificationExecutor<T>> extends BaseModel<R> {
   default void validExecutor(Object repository) {
      this.assertNotNullRepository(repository);
      if (!(repository instanceof JpaSpecificationExecutor)) {
         throw new FenixException(StringHelper.format("\u3010Fenix \u5f02\u5e38\u3011\u83b7\u53d6\u5230\u7684 Spring Data JPA \u7684 Repository \u63a5\u53e3\u3010{}\u3011\u4e0d\u662f\u771f\u6b63\u7684 JpaSpecificationExecutor \u63a5\u53e3\u3002", repository.getClass().getName()));
      }
   }
}
