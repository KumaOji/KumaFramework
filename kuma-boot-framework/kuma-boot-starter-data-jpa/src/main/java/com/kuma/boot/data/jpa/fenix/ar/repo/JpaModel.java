package com.kuma.boot.data.jpa.fenix.ar.repo;

import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface JpaModel<T, ID, R extends JpaRepository<T, ID>> extends PagingAndSortingModel<T, ID, R>, CrudModel<T, ID, R> {
   default void validRepository(Object repository) {
      this.assertNotNullRepository(repository);
      if (!(repository instanceof JpaRepository)) {
         throw new FenixException(StringHelper.format("【Fenix 异常】获取到的 Spring Data JPA 的 Repository 接口【{}】不是真正的 JpaRepository 接口。", repository.getClass().getName()));
      }
   }

   @Transactional
   default void flush() {
      ((JpaRepository)this.getRepository()).flush();
   }

   @Transactional
   default <S extends T> S saveAndFlush() {
      return (S)((JpaRepository)this.getRepository()).saveAndFlush(this);
   }

   default T getById() {
      return (T)((JpaRepository)this.getRepository()).findById(this.getId()).orElse((Object)null);
   }
}
