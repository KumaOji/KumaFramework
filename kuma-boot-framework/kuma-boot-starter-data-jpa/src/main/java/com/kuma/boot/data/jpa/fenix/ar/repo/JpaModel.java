package com.kuma.boot.data.jpa.fenix.ar.repo;

import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface JpaModel<T, ID, R extends JpaRepository<T, ID>> extends PagingAndSortingModel<T, ID, R>, CrudModel<T, ID, R> {
   default void validRepository(Object repository) {
      this.assertNotNullRepository(repository);
      if (!(repository instanceof JpaRepository)) {
         throw new FenixException(StringHelper.format("\u3010Fenix \u5f02\u5e38\u3011\u83b7\u53d6\u5230\u7684 Spring Data JPA \u7684 Repository \u63a5\u53e3\u3010{}\u3011\u4e0d\u662f\u771f\u6b63\u7684 JpaRepository \u63a5\u53e3\u3002", repository.getClass().getName()));
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
