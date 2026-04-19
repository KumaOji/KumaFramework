/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.data.jpa.listener;

import com.kuma.boot.common.utils.log.LogUtils;
import java.io.Serializable;
import java.util.Iterator;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.metamodel.RepresentationMode;
import org.hibernate.type.Type;

/**
 * HibernateInterceptor
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-10 17:10:27
 */
public class HibernateInterceptor implements Interceptor, Serializable {

   @Override
   public void onDelete(
           Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> onDelete");
   }

   @Override
   public boolean onFlushDirty(
           Object entity,
           Object id,
           Object[] currentState,
           Object[] previousState,
           String[] propertyNames,
           Type[] types) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> onFlushDirty");
      return true;
   }

   @Override
   public boolean onLoad(
           Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> onLoad");
      return true;
   }

   @Override
   public boolean onSave(
           Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> onSave");
      return true;
   }

   @Override
   public void postFlush(Iterator entities) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> postFlush");
   }

   @Override
   public void preFlush(Iterator entities) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> preFlush");
   }

   @Override
   public Boolean isTransient(Object entity) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> isTransient");
      return true;
   }

   @Override
   public Object instantiate(String entityName, RepresentationMode representationMode, Object id)
           throws CallbackException {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> instantiate");
      return Interceptor.super.instantiate(entityName, representationMode, id);
   }

   @Override
   public int[] findDirty(
           Object entity,
           Object id,
           Object[] currentState,
           Object[] previousState,
           String[] propertyNames,
           Type[] types) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> findDirty");
      return null;
   }

   @Override
   public String getEntityName(Object object) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> getEntityName");
      return null;
   }

   @Override
   public Object getEntity(String entityName, Object id) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> getEntity");
      return null;
   }

   @Override
   public void afterTransactionBegin(Transaction tx) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> afterTransactionBegin");
   }

   @Override
   public void afterTransactionCompletion(Transaction tx) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> afterTransactionCompletion");
   }

   @Override
   public void beforeTransactionCompletion(Transaction tx) {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> beforeTransactionCompletion");
   }

   // @Override
   // public String onPrepareStatement(String sql) {
   // 	LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> onPrepareStatement: {}",
   // 		SqlFormatter.format(sql));
   //
   // 	return super.onPrepareStatement(sql);
   // }

   @Override
   public void onCollectionRemove(Object collection, Object key) throws CallbackException {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> onCollectionRemove");
   }

   @Override
   public void onCollectionRecreate(Object collection, Object key) throws CallbackException {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> onCollectionRecreate");
   }

   @Override
   public void onCollectionUpdate(Object collection, Object key) throws CallbackException {
      LogUtils.info("HibernateInterceptor >>>>>>>>>>>>>>>>>>>>>>>>> onCollectionUpdate");
   }
}
