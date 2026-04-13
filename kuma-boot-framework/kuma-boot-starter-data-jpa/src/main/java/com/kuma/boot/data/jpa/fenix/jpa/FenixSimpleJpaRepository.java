package com.kuma.boot.data.jpa.fenix.jpa;

import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.SingularAttribute;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class FenixSimpleJpaRepository<T, ID> extends SimpleJpaRepository<T, ID> implements FenixJpaRepository<T, ID> {
   private static final String ENTITIES_NULL_MSG = "Entities must not be null!";
   private final JpaEntityInformation<T, ?> entityInformation;
   private final EntityManager em;

   public FenixSimpleJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
      super(entityInformation, entityManager);
      this.entityInformation = entityInformation;
      this.em = entityManager;
   }

   public FenixSimpleJpaRepository(Class<T> domainClass, EntityManager em) {
      this(JpaEntityInformationSupport.getEntityInformation(domainClass, em), em);
   }

   @Transactional(
      rollbackFor = {RuntimeException.class}
   )
   public <S extends T> void saveBatch(Iterable<S> entities) {
      this.saveBatch(entities, 500);
   }

   @Transactional(
      rollbackFor = {RuntimeException.class}
   )
   public <S extends T> void saveBatch(Iterable<S> entities, int batchSize) {
      Assert.notNull(entities, "Entities must not be null!");
      int i = 0;

      for(S entity : entities) {
         this.em.persist(entity);
         ++i;
         if (i % batchSize == 0) {
            this.em.flush();
            this.em.clear();
         }
      }

   }

   @Transactional(
      rollbackFor = {RuntimeException.class}
   )
   public <S extends T> void updateBatch(Iterable<S> entities) {
      this.updateBatch(entities, 500);
   }

   @Transactional(
      rollbackFor = {RuntimeException.class}
   )
   public <S extends T> void updateBatch(Iterable<S> entities, int batchSize) {
      Assert.notNull(entities, "Entities must not be null!");
      int i = 0;
      Session session = (Session)this.em.unwrap(Session.class);

      for(S entity : entities) {
         session.merge(entity);
         ++i;
         if (i % batchSize == 0) {
            this.em.flush();
            this.em.clear();
         }
      }

   }

   @Transactional(
      rollbackFor = {RuntimeException.class}
   )
   public <S extends T> S saveOrUpdateByNotNullProperties(S entity) {
      Assert.notNull(entity, "Entity must not be null.");
      ID id = (ID)this.entityInformation.getId(entity);
      if (StringHelper.isEmptyObject(id)) {
         this.em.persist(entity);
         return entity;
      } else {
         Optional<T> entityOptional = super.findById(id);
         if (!entityOptional.isPresent()) {
            this.em.persist(entity);
            return entity;
         } else {
            T oldEntity = (T)entityOptional.get();
            BeanUtils.copyProperties(entity, oldEntity, this.getNullProperties(entity));
            this.em.merge(oldEntity);
            return entity;
         }
      }
   }

   @Transactional(
      rollbackFor = {RuntimeException.class}
   )
   public <S extends T> void saveOrUpdateAllByNotNullProperties(Iterable<S> entities) {
      Assert.notNull(entities, "Entities must not be null!");

      for(S entity : entities) {
         this.saveOrUpdateByNotNullProperties(entity);
      }

   }

   @Transactional(
      rollbackFor = {RuntimeException.class}
   )
   public void deleteByIds(Iterable<ID> ids) {
      Assert.notNull(ids, "The given ids must not be null!");

      for(ID id : ids) {
         super.deleteById(id);
      }

   }

   @Transactional(
      rollbackFor = {RuntimeException.class}
   )
   public void deleteBatchByIds(Iterable<ID> ids) {
      this.deleteBatchByIds(ids, 500);
   }

   @Transactional(
      rollbackFor = {RuntimeException.class}
   )
   public void deleteBatchByIds(Iterable<ID> ids, int batchSize) {
      Assert.notNull(ids, "The given ids must not be null!");
      Assert.isTrue(batchSize > 0, "The given batchSize must not be <= 0.");
      String entityName = this.entityInformation.getEntityName();
      SingularAttribute<? super T, ?> idAttribute = this.entityInformation.getIdAttribute();
      String idName = idAttribute == null ? "id" : idAttribute.getName();
      String sql = StringHelper.format("delete from {} where {} in :batch_ids", entityName, idName);
      int i = 0;
      List<ID> batchIds = new ArrayList();

      for(ID id : ids) {
         if (id != null) {
            batchIds.add(id);
            ++i;
            if (i % batchSize == 0 && !batchIds.isEmpty()) {
               this.doBatchDelete(sql, batchIds);
               batchIds.clear();
            }
         }
      }

      if (!batchIds.isEmpty()) {
         this.doBatchDelete(sql, batchIds);
      }

   }

   private void doBatchDelete(String sql, List<ID> batchIds) {
      this.em.createQuery(sql).setParameter("batch_ids", batchIds).executeUpdate();
   }

   private String[] getNullProperties(Object entity) {
      BeanWrapper beanWrapper = new BeanWrapperImpl(entity);
      PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
      List<String> nullProperties = new ArrayList();

      for(PropertyDescriptor propertyDescriptor : propertyDescriptors) {
         String propertyName = propertyDescriptor.getName();
         if (beanWrapper.getPropertyValue(propertyName) == null) {
            nullProperties.add(propertyName);
         }
      }

      return (String[])nullProperties.toArray(new String[0]);
   }
}
