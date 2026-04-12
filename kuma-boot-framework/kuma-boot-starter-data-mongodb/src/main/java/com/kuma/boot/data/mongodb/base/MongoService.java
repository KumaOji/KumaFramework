package com.kuma.boot.data.mongodb.base;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface MongoService<E extends BaseMongoEntity, ID extends Serializable> {
   BaseMongoRepository<E, ID> getRepository();

   default E save(E domain) {
      return (E)(this.getRepository().save(domain));
   }

   default List<E> findAll() {
      return this.getRepository().findAll();
   }

   default Page<E> findByPage(Pageable pageable) {
      return this.getRepository().findAll(pageable);
   }

   default Page<E> findByPage(int pageNumber, int pageSize) {
      return this.findByPage(PageRequest.of(pageNumber, pageSize));
   }

   default Page<E> findByPage(int pageNumber, int pageSize, Sort sort) {
      return this.findByPage(PageRequest.of(pageNumber, pageSize, sort));
   }

   default Page<E> findByPage(int pageNumber, int pageSize, Sort.Direction direction, String... properties) {
      return this.findByPage(PageRequest.of(pageNumber, pageSize, direction, properties));
   }

   default List<E> findAll(Sort sort) {
      return this.getRepository().findAll(sort);
   }

   default E findById(ID id) {
      return (E)(this.getRepository().findById(id).orElse(null));
   }

   default void deleteById(ID id) {
      this.getRepository().deleteById(id);
   }

   default void delete(E domain) {
      this.getRepository().delete(domain);
   }
}
