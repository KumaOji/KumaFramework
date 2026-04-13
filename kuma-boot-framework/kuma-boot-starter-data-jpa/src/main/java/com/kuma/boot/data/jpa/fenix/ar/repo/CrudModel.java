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
         throw new FenixException(StringHelper.format("\u3010Fenix \u5f02\u5e38\u3011\u83b7\u53d6\u5230\u7684 Spring Data JPA \u7684 Repository \u63a5\u53e3\u3010{}\u3011\u4e0d\u662f\u771f\u6b63\u7684 CrudRepository \u63a5\u53e3\u3002", repository.getClass().getName()));
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
