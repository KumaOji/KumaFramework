package com.kuma.boot.data.mongodb.mongodb.service;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mongodb.mongodb.annotation.QueryField;
import com.kuma.boot.data.mongodb.mongodb.util.ReflectionUtil;
import com.kuma.boot.data.mongodb.mongodb.vo.Page;
import java.lang.reflect.Field;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public abstract class MongoDaoSupport<T> implements BaseMongoDAO<T> {
   @Autowired
   @Qualifier("mongoTemplate")
   protected MongoTemplate mongoTemplate;

   public MongoDaoSupport() {
   }

   public T save(T bean) {
      this.mongoTemplate.save(bean);
      return bean;
   }

   public void deleteById(T t) {
      this.mongoTemplate.remove(t);
   }

   public void deleteByCondition(T t) {
      Query query = this.buildBaseQuery(t);
      this.mongoTemplate.remove(query, this.getEntityClass());
   }

   public void updateById(String id, T t) {
      Query query = new Query();
      query.addCriteria(Criteria.where("id").is(id));
      Update update = this.buildBaseUpdate(t);
      this.update(query, update);
   }

   public List<T> findByCondition(T t) {
      Query query = this.buildBaseQuery(t);
      return this.mongoTemplate.find(query, this.getEntityClass());
   }

   public List<T> find(Query query) {
      return this.mongoTemplate.find(query, this.getEntityClass());
   }

   public T findOne(Query query) {
      return (T)this.mongoTemplate.findOne(query, this.getEntityClass());
   }

   public void update(Query query, Update update) {
      this.mongoTemplate.updateMulti(query, update, this.getEntityClass());
   }

   public T findById(String id) {
      return (T)this.mongoTemplate.findById(id, this.getEntityClass());
   }

   public T findById(String id, String collectionName) {
      return (T)this.mongoTemplate.findById(id, this.getEntityClass(), collectionName);
   }

   public Page<T> findPage(Page<T> page, Query query) {
      query = query == null ? new Query(Criteria.where("_id").exists(true)) : query;
      long count = this.count(query);
      page.setTotalCount((int)count);
      int currentPage = page.getCurrentPage();
      int pageSize = page.getPageSize();
      query.skip((long)((currentPage - 1) * pageSize)).limit(pageSize);
      List<T> rows = this.find(query);
      page.build(rows);
      return page;
   }

   public long count(Query query) {
      return this.mongoTemplate.count(query, this.getEntityClass());
   }

   private Query buildBaseQuery(T t) {
      Query query = new Query();
      Field[] fields = t.getClass().getDeclaredFields();

      for(Field field : fields) {
         field.setAccessible(true);

         try {
            Object value = field.get(t);
            if (value != null) {
               QueryField queryField = (QueryField)field.getAnnotation(QueryField.class);
               if (queryField != null) {
                  query.addCriteria(queryField.type().buildCriteria(queryField, field, value));
               }
            }
         } catch (IllegalArgumentException e) {
            LogUtils.error(e);
         } catch (IllegalAccessException e) {
            LogUtils.error(e);
         }
      }

      return query;
   }

   private Update buildBaseUpdate(T t) {
      Update update = new Update();
      Field[] fields = t.getClass().getDeclaredFields();

      for(Field field : fields) {
         field.setAccessible(true);

         try {
            Object value = field.get(t);
            if (value != null) {
               update.set(field.getName(), value);
            }
         } catch (Exception e) {
            LogUtils.error(e);
         }
      }

      return update;
   }

   protected Class<T> getEntityClass() {
      return ReflectionUtil.<T>getSuperClassGenricType(this.getClass());
   }

   public MongoTemplate getMongoTemplate() {
      return this.mongoTemplate;
   }
}
