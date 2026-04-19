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

import cn.hutool.db.sql.SqlFormatter;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.support.Collector;
import java.util.Objects;
import org.hibernate.HibernateException;
import org.hibernate.event.internal.DefaultDeleteEventListener;
import org.hibernate.event.internal.DefaultLoadEventListener;
import org.hibernate.event.internal.DefaultPersistEventListener;
import org.hibernate.event.internal.DefaultRefreshEventListener;
import org.hibernate.event.spi.DeleteEvent;
import org.hibernate.event.spi.LoadEvent;
import org.hibernate.event.spi.PersistContext;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.event.spi.RefreshContext;
import org.hibernate.event.spi.RefreshEvent;
import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 * HibernateInterceptor
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-10 17:10:27
 */
public class HibernateInspector implements StatementInspector {

   private String sql;

   /**
    * RefreshEventListener
    *
    * @author kuma
    * @version 2026.03
    * @since 2025-12-19 09:30:45
    */
   public static class RefreshEventListener extends DefaultRefreshEventListener {

      @Override
      public void onRefresh( RefreshEvent event ) throws HibernateException {
         Object object = event.getObject();
         String entityName = event.getEntityName();
         String sql = SqlContextHolder.getSql();

         LogUtils.info(
                 "Hibernate RefreshEventListener entityName: {} sql: {} object: {}",
                 entityName,
                 sql,
                 JacksonUtils.toJSONString(object));

         super.onRefresh(event);
      }

      @Override
      public void onRefresh( RefreshEvent event, RefreshContext refreshedAlready ) {
         Object object = event.getObject();
         String entityName = event.getEntityName();
         String sql = SqlContextHolder.getSql();

         LogUtils.info(
                 "Hibernate RefreshEventListener entityName: {} sql: {} refreshedAlready: {}"
                         + " object: {}",
                 entityName,
                 sql,
                 refreshedAlready,
                 JacksonUtils.toJSONString(object));

         super.onRefresh(event, refreshedAlready);
      }
   }

   /**
    * PersistEventListener
    *
    * @author kuma
    * @version 2026.03
    * @since 2025-12-19 09:30:45
    */
   public static class PersistEventListener extends DefaultPersistEventListener {

      @Override
      public void onPersist( PersistEvent event ) throws HibernateException {
         Object object = event.getObject();
         String sql = SqlContextHolder.getSql();
         String entityName = event.getEntityName();

         LogUtils.info(
                 "Hibernate PersistEventListener entityName: {} sql: {} object: {}",
                 entityName,
                 sql,
                 JacksonUtils.toJSONString(object));

         super.onPersist(event);
      }

      @Override
      public void onPersist( PersistEvent event, PersistContext createCache )
              throws HibernateException {
         Object object = event.getObject();
         String sql = SqlContextHolder.getSql();
         String entityName = event.getEntityName();

         LogUtils.info(
                 "Hibernate PersistEventListener entityName: {} sql: {}  result: {} createCache:"
                         + " {}",
                 entityName,
                 sql,
                 JacksonUtils.toJSONString(object),
                 createCache);

         super.onPersist(event, createCache);
      }
   }

//	public static class SaveOrUpdateListener extends DefaultSaveOrUpdateEventListener {
//
//		@Override
//		public void onSaveOrUpdate(SaveOrUpdateEvent event) {
//			Object object = event.getObject();
//			String sql = SqlContextHolder.getSql();
//			String entityName = event.getEntityName();
//			LogUtils.info(
//				"Hibernate SaveOrUpdateListener entityName: {} sql: {} object: {}",
//				entityName,
//				sql,
//				JsonUtils.toJSONString(object));
//
//			Collector collector = ContextUtils.getBean(Collector.class, true);
//			if (Objects.nonNull(collector)) {
//				try {
//					String replace =
//						StringUtils.nullToEmpty(SqlContextHolder.getSql())
//							.replace("\r", "")
//							.replace("\n", "");
//
//					collector
//						.hook("ttc.monitor.jpa.onSaveOrUpdate.sql.hook")
//						.run(
//							replace,
//							() -> {
//								try {
//									super.onSaveOrUpdate(event);
//								} catch (Exception e) {
//									throw new RuntimeException(e);
//								}
//							});
//				} finally {
//					SqlContextHolder.clear();
//				}
//			}
//		}
//	}

   /**
    * DeleteListener
    *
    * @author kuma
    * @version 2026.03
    * @since 2025-12-19 09:30:45
    */
   public static class DeleteListener extends DefaultDeleteEventListener {

      @Override
      public void onDelete( DeleteEvent event ) throws HibernateException {
         String sql = SqlContextHolder.getSql();
         Object object = event.getObject();
         String entityName = event.getEntityName();
         LogUtils.info(
                 "Hibernate DeleteListener entityName: {} sql: {} object:{}",
                 entityName,
                 sql,
                 JacksonUtils.toJSONString(object));

         Collector collector = ContextUtils.getBean(Collector.class, true);
         if (Objects.nonNull(collector)) {
            try {
               String replace =
                       StringUtils.nullToEmpty(sql).replace("\r", "").replace("\n", "");
               collector
                       .hook("ttc.monitor.jpa.delete.sql.hook")
                       .run(
                               replace,
                               () -> {
                                  try {
                                     super.onDelete(event);
                                  } catch (Exception e) {
                                     throw new RuntimeException(e);
                                  }
                               });
            } finally {
               SqlContextHolder.clear();
            }
         }
      }
   }

   /**
    * LoadListener
    *
    * @author kuma
    * @version 2026.03
    * @since 2025-12-19 09:30:45
    */
   public static class LoadListener extends DefaultLoadEventListener {

      @Override
      public void onLoad( LoadEvent event, LoadType loadType ) throws HibernateException {
         String sql = SqlContextHolder.getSql();
         String entityClassName = event.getEntityClassName();
         Object result = event.getResult();

         LogUtils.info(
                 "Hibernate LoadListener entityName: {} sql: {}, result: {}",
                 entityClassName,
                 sql,
                 JacksonUtils.toJSONString(result));

         Collector collector = ContextUtils.getBean(Collector.class, true);
         if (Objects.nonNull(collector)) {
            try {
               String replace =
                       StringUtils.nullToEmpty(sql).replace("\r", "").replace("\n", "");
               collector
                       .hook("ttc.monitor.jpa.load.sql.hook")
                       .run(
                               replace,
                               () -> {
                                  try {
                                     super.onLoad(event, loadType);
                                  } catch (Exception e) {
                                     throw new RuntimeException(e);
                                  }
                               });
            } finally {
               SqlContextHolder.clear();
            }
         }
      }
   }

   @Override
   public String inspect( String sql ) {
      this.sql = sql;

      LogUtils.info(SqlFormatter.format(sql));

      SqlContextHolder.setSql(sql);
      return sql;
   }

   public String getSql() {
      return sql;
   }

   public void setSql( String sql ) {
      this.sql = sql;
   }
}
