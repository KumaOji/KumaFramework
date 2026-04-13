package com.kuma.boot.data.jpa.fenix.ar.repo;

import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import com.kuma.boot.data.jpa.fenix.jpa.FenixJpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface FenixJpaModel<T, ID, R extends FenixJpaRepository<T, ID>> extends JpaModel<T, ID, R> {
   default void validRepository(Object repository) {
      this.assertNotNullRepository(repository);
      if (!(repository instanceof FenixJpaRepository)) {
         throw new FenixException(StringHelper.format("\u3010Fenix \u5f02\u5e38\u3011\u83b7\u53d6\u5230\u7684 Spring Data JPA \u7684 Repository \u63a5\u53e3\u3010{}\u3011\u4e0d\u662f\u771f\u6b63\u7684 FenixJpaRepository \u63a5\u53e3\u3002", repository.getClass().getName()));
      }
   }

   @Transactional
   default <S extends T> S saveOrUpdateByNotNullProperties() {
      return (S)((FenixJpaRepository)this.getRepository()).saveOrUpdateByNotNullProperties(this);
   }
}
