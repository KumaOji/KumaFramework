package com.kuma.boot.data.jpa.simplestjpa;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.kuma.boot.data.jpa.simplestjpa.config.TenantProperties;
import com.kuma.boot.data.jpa.simplestjpa.exception.UpdateException;
import com.kuma.boot.data.jpa.simplestjpa.plugin.TenantFactory;
import jakarta.persistence.EntityManager;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.transaction.annotation.Transactional;

public interface IService<T, ID extends Serializable> {
   int DEFAULT_BATCH_SIZE = 1000;

   BaseRepository<T, ID> getRepository();

   JPAQueryFactory queryChain();

   JPAUpdateClause updateChain(EntityPath<T> entityPath);

   EntityManager getEntityManager();

   default T save(T entity) {
      TenantProperties tenantProperties = (TenantProperties)SpringUtil.getBean(TenantProperties.class);
      if (tenantProperties != null && tenantProperties.getEnableTenant() && !tenantProperties.getTables().isEmpty()) {
         String tenant = StrUtil.toCamelCase(tenantProperties.getTenantIdColumn());
         TenantFactory tenantFactory = (TenantFactory)SpringUtil.getBean("tenantFactory");

         try {
            Field field = entity.getClass().getDeclaredField(tenant);
            field.setAccessible(true);
            if (tenantFactory != null) {
               field.set(entity, tenantFactory.getTenantId());
            }
         } catch (Exception var6) {
         }
      }

      return (T)this.getRepository().save(entity);
   }

   default List<T> saveBatch(Collection<T> entities) {
      return this.getRepository().saveAll(entities);
   }

   @Transactional
   default T update(T entity) {
      JpaEntityInformation<T, ?> entityInformation = JpaEntityInformationSupport.getEntityInformation(this.getEntityClass(), this.getEntityManager());
      entityInformation.getRequiredId(entity);
      return (T)this.getEntityManager().merge(entity);
   }

   @Transactional
   default T update(T entity, Boolean ignore) {
      JpaEntityInformation<T, ?> entityInformation = JpaEntityInformationSupport.getEntityInformation(this.getEntityClass(), this.getEntityManager());
      entityInformation.getRequiredId(entity);
      if (!ignore) {
         return (T)this.getEntityManager().merge(entity);
      } else {
         Object id = entityInformation.getId(entity);
         T existingEntity = (T)this.getRepository().findById((Serializable)id).orElseThrow(() -> new IllegalArgumentException("Entity not found"));
         CopyOptions copyOptions = new CopyOptions();
         copyOptions.setIgnoreCase(false);
         copyOptions.setIgnoreNullValue(true);
         Map<String, Object> stringObjectMap = BeanUtil.beanToMap(entity, true, true);
         String[] IgnoreProperties = (String[])stringObjectMap.keySet().toArray(new String[0]);
         copyOptions.setIgnoreProperties(IgnoreProperties);
         BeanUtil.copyProperties(existingEntity, entity, copyOptions);
         return (T)this.getEntityManager().merge(entity);
      }
   }

   @Transactional
   default T update(T entity, Boolean ignore, String[] ignoreProperties) {
      JpaEntityInformation<T, ?> entityInformation = JpaEntityInformationSupport.getEntityInformation(this.getEntityClass(), this.getEntityManager());
      entityInformation.getRequiredId(entity);
      if (!ignore) {
         return (T)this.getEntityManager().merge(entity);
      } else {
         Object id = entityInformation.getId(entity);
         T existingEntity = (T)this.getRepository().findById((Serializable)id).orElseThrow(() -> new IllegalArgumentException("Entity not found"));
         CopyOptions copyOptions = new CopyOptions();
         copyOptions.setIgnoreCase(false);
         copyOptions.setIgnoreNullValue(true);
         Map<String, Object> stringObjectMap = BeanUtil.beanToMap(entity, true, true);
         String[] entityIgnoreProperties = (String[])stringObjectMap.keySet().toArray(new String[0]);
         String[] customIgnoreProperties = (String[])ArrayUtil.addAll(new String[][]{entityIgnoreProperties, ignoreProperties});
         copyOptions.setIgnoreProperties(customIgnoreProperties);
         BeanUtil.copyProperties(existingEntity, entity, copyOptions);
         return (T)this.getEntityManager().merge(entity);
      }
   }

   @Transactional
   default T saveOrUpdate(T entity) {
      JpaEntityInformation<T, ?> entityInformation = JpaEntityInformationSupport.getEntityInformation(this.getEntityClass(), this.getEntityManager());
      if (entityInformation.isNew(entity)) {
         this.getEntityManager().persist(entity);
         return entity;
      } else {
         return (T)this.getEntityManager().merge(entity);
      }
   }

   @Transactional
   default T saveOrUpdateSelective(T entity) {
      JpaEntityInformation<T, ?> entityInformation = JpaEntityInformationSupport.getEntityInformation(this.getEntityClass(), this.getEntityManager());
      if (entityInformation.isNew(entity)) {
         this.getEntityManager().persist(entity);
         return entity;
      } else {
         return (T)this.update(entity, true);
      }
   }

   @Transactional
   default boolean update(JPAUpdateClause query) {
      long count = query.execute();
      return count > 0L;
   }

   default long count() {
      return this.getRepository().count();
   }

   default long count(Example<T> example) {
      return this.getRepository().count(example);
   }

   default T getById(ID id) {
      return (T)this.getRepository().findById(id).orElseThrow(() -> new RuntimeException("\u6839\u636e\u67e5\u8be2\u6761\u4ef6\u67e5\u8be2\u4e00\u6761\u6570\u636e\u5f02\u5e38"));
   }

   default Optional<T> getByIdOpt(ID id) {
      return this.getRepository().findById(id);
   }

   default Optional<T> getOneOpt(Example<T> example) {
      return this.getRepository().findOne(example);
   }

   default T getOne(Example<T> example) {
      return (T)this.getRepository().findOne(example).orElseThrow(() -> new RuntimeException("\u6839\u636e\u67e5\u8be2\u6761\u4ef6\u67e5\u8be2\u4e00\u6761\u6570\u636e\u5f02\u5e38"));
   }

   default T getOne(Predicate query) {
      return (T)this.getRepository().findOne(query).orElseThrow(() -> new RuntimeException("\u6839\u636e\u67e5\u8be2\u6761\u4ef6\u67e5\u8be2\u4e00\u6761\u6570\u636e\u5f02\u5e38"));
   }

   default long count(Predicate query) {
      return this.getRepository().count(query);
   }

   default boolean exists(Predicate query) {
      return this.getRepository().exists(query);
   }

   default boolean exists(Example<T> condition) {
      return this.getRepository().exists(condition);
   }

   default boolean existsById(ID id) {
      return this.getRepository().existsById(id);
   }

   default List<T> list() {
      return this.getRepository().findAll();
   }

   default List<T> list(Predicate query) {
      return (List)StreamSupport.stream(this.getRepository().findAll(query).spliterator(), false).collect(Collectors.toList());
   }

   default List<T> list(Example<T> query) {
      return this.getRepository().findAll(query);
   }

   default List<T> listByIds(Collection<ID> ids) {
      return this.getRepository().findAllById(ids);
   }

   default Page<T> page(Pageable page) {
      return this.getRepository().findAll(page);
   }

   default Page<T> page(Pageable page, Predicate query) {
      return this.getRepository().findAll(query, page);
   }

   default boolean removeById(ID id) {
      boolean success = true;
      if (StrUtil.isBlankIfStr(id)) {
         return false;
      } else {
         try {
            this.getRepository().deleteById(id);
            return success;
         } catch (Exception e) {
            success = false;
            throw new UpdateException(ExceptionUtil.stacktraceToString(e));
         }
      }
   }

   default boolean removeByIds(Collection<? extends ID> ids) {
      boolean success = true;
      if (CollUtil.isEmpty(ids)) {
         return false;
      } else {
         try {
            this.getRepository().deleteAllById(ids);
            return success;
         } catch (Exception e) {
            success = false;
            throw new UpdateException(ExceptionUtil.stacktraceToString(e));
         }
      }
   }

   default boolean remove(T entity) {
      boolean success = true;

      try {
         this.getRepository().delete(entity);
         return success;
      } catch (Exception e) {
         success = false;
         throw new UpdateException(ExceptionUtil.stacktraceToString(e));
      }
   }

   default boolean remove(Collection<T> entities) {
      boolean success = true;
      if (CollUtil.isEmpty(entities)) {
         return false;
      } else {
         try {
            this.getRepository().deleteAllInBatch(entities);
            return success;
         } catch (Exception e) {
            throw new UpdateException(ExceptionUtil.stacktraceToString(e));
         }
      }
   }

   default boolean removeAll(Collection<? extends T> entities) {
      boolean success = true;
      if (CollUtil.isEmpty(entities)) {
         return false;
      } else {
         try {
            this.getRepository().deleteAll(entities);
            return success;
         } catch (Exception e) {
            throw new UpdateException(ExceptionUtil.stacktraceToString(e));
         }
      }
   }

   default boolean remove() {
      try {
         this.getRepository().deleteAll();
         return true;
      } catch (Exception e) {
         throw new UpdateException(ExceptionUtil.stacktraceToString(e));
      }
   }

   default Class<T> getEntityClass() {
      Class<T> clazz = (Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
      return clazz;
   }
}
