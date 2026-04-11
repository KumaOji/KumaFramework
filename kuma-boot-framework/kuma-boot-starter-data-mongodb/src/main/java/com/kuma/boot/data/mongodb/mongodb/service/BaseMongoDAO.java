package com.kuma.boot.data.mongodb.mongodb.service;

import com.kuma.boot.data.mongodb.mongodb.vo.Page;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public interface BaseMongoDAO<T> {
   T save(T entity);

   void deleteById(T t);

   void deleteByCondition(T t);

   void updateById(String id, T t);

   List<T> findByCondition(T t);

   List<T> find(Query query);

   T findOne(Query query);

   void update(Query query, Update update);

   T findById(String id);

   T findById(String id, String collectionName);

   Page<T> findPage(Page<T> page, Query query);

   long count(Query query);

   MongoTemplate getMongoTemplate();
}
