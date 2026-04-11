package com.kuma.boot.data.mongodb.base;

import com.kuma.boot.common.model.result.Result;
import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public interface MongoController<E extends BaseMongoEntity, ID extends Serializable> {
   MongoService<E, ID> getMongoService();

   default Result<Page<E>> findByPage(Integer pageNumber, Integer pageSize) {
      Page<E> pages = this.getMongoService().findByPage(pageNumber, pageSize);
      return Result.success(pages);
   }

   default Result<Page<E>> findByPage(Integer pageNumber, Integer pageSize, Sort.Direction direction, String... properties) {
      Page<E> pages = this.getMongoService().findByPage(pageNumber, pageSize, direction, properties);
      return Result.success(pages);
   }

   default Result<List<E>> findAll() {
      List<E> domains = this.getMongoService().findAll();
      return Result.success(domains);
   }

   default Result<E> findById(ID id) {
      E domain = this.getMongoService().findById(id);
      return Result.success(domain);
   }

   default Result<E> saveOrUpdate(E domain) {
      E savedDomain = this.getMongoService().save(domain);
      return Result.success(savedDomain);
   }

   default Result<String> delete(ID id) {
      Result<String> result = Result.success(String.valueOf(id));
      this.getMongoService().deleteById(id);
      return result;
   }
}
