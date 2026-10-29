package com.kuma.boot.data.jpa.fenix.ar.repo;

import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import com.kuma.boot.data.jpa.fenix.jpa.FenixJpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface FenixJpaModel<T, ID, R extends FenixJpaRepository<T, ID>> extends JpaModel<T, ID, R> {
   default void validRepository(Object repository) {
      this.assertNotNullRepository(repository);
      if (!(repository instanceof FenixJpaRepository)) {
         throw new FenixException(StringHelper.format("【Fenix 异常】获取到的 Spring Data JPA 的 Repository 接口【{}】不是真正的 FenixJpaRepository 接口。", repository.getClass().getName()));
      }
   }

   @Transactional
   default <S extends T> S saveOrUpdateByNotNullProperties() {
      return (S)((FenixJpaRepository)this.getRepository()).saveOrUpdateByNotNullProperties(this);
   }
}
