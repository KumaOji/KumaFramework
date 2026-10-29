package com.kuma.boot.data.jpa.fenix.ar.repo;

import com.kuma.boot.data.jpa.fenix.ar.BaseModel;
import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PagingAndSortingModel<T, ID, R extends PagingAndSortingRepository<T, ID>> extends BaseModel<R> {
   default void validRepository(Object repository) {
      this.assertNotNullRepository(repository);
      if (!(repository instanceof PagingAndSortingRepository)) {
         throw new FenixException(StringHelper.format("【Fenix 异常】获取到的 Spring Data JPA 的 Repository 接口【{}】不是真正的 PagingAndSortingRepository 接口。", repository.getClass().getName()));
      }
   }
}
