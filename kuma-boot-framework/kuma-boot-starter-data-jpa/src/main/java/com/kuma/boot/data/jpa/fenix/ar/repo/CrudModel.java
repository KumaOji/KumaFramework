package com.kuma.boot.data.jpa.fenix.ar.repo;

import com.kuma.boot.data.jpa.fenix.ar.BaseModel;
import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CrudModel<T, ID, R extends CrudRepository<T, ID>> extends BaseModel<R> {
   ID getId();

   default void validRepository(Object repository) {
      this.assertNotNullRepository(repository);
      if (!(repository instanceof CrudRepository)) {
         throw new FenixException(StringHelper.format("【Fenix 异常】获取到的 Spring Data JPA 的 Repository 接口【{}】不是真正的 CrudRepository 接口。", repository.getClass().getName()));
      }
   }

   @Transactional
   default T save() {
      return (T)((CrudRepository)this.getRepository()).save(this);
   }

   default Optional<T> findById() {
      return ((CrudRepository)this.getRepository()).findById(this.getId());
   }

   default boolean existsById() {
      return ((CrudRepository)this.getRepository()).existsById(this.getId());
   }

   @Transactional
   default void delete() {
      ((CrudRepository)this.getRepository()).delete(this);
   }

   @Transactional
   default void deleteById() {
      ((CrudRepository)this.getRepository()).deleteById(this.getId());
   }
}
