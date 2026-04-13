package com.kuma.boot.data.jpa.listener;

import com.kuma.boot.common.utils.log.LogUtils;
import java.io.Serializable;
import java.util.Iterator;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.metamodel.RepresentationMode;
import org.hibernate.type.Type;

public class HibernateInterceptor implements Interceptor, Serializable {
   public HibernateInterceptor() {
   }

   public void onDelete(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> onDelete", new Object[0]);
   }

   public boolean onFlushDirty(Object entity, Object id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> onFlushDirty", new Object[0]);
      return true;
   }

   public boolean onLoad(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> onLoad", new Object[0]);
      return true;
   }

   public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> onSave", new Object[0]);
      return true;
   }

   public void postFlush(Iterator entities) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> postFlush", new Object[0]);
   }

   public void preFlush(Iterator entities) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> preFlush", new Object[0]);
   }

   public Boolean isTransient(Object entity) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> isTransient", new Object[0]);
      return true;
   }

   public Object instantiate(String entityName, RepresentationMode representationMode, Object id) throws CallbackException {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> instantiate", new Object[0]);
      return super.instantiate(entityName, representationMode, id);
   }

   public int[] findDirty(Object entity, Object id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> findDirty", new Object[0]);
      return null;
   }

   public String getEntityName(Object object) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> getEntityName", new Object[0]);
      return null;
   }

   public Object getEntity(String entityName, Object id) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> getEntity", new Object[0]);
      return null;
   }

   public void afterTransactionBegin(Transaction tx) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> afterTransactionBegin", new Object[0]);
   }

   public void afterTransactionCompletion(Transaction tx) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> afterTransactionCompletion", new Object[0]);
   }

   public void beforeTransactionCompletion(Transaction tx) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> beforeTransactionCompletion", new Object[0]);
   }

   public void onCollectionRemove(Object collection, Object key) throws CallbackException {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> onCollectionRemove", new Object[0]);
   }

   public void onCollectionRecreate(Object collection, Object key) throws CallbackException {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> onCollectionRecreate", new Object[0]);
   }

   public void onCollectionUpdate(Object collection, Object key) throws CallbackException {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> onCollectionUpdate", new Object[0]);
   }
}
