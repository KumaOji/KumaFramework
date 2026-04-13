package com.kuma.boot.data.jpa.listener;

import cn.hutool.db.sql.SqlFormatter;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.json.JacksonUtils;
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
import org.hibernate.event.spi.LoadEventListener;
import org.hibernate.event.spi.PersistContext;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.event.spi.RefreshContext;
import org.hibernate.event.spi.RefreshEvent;
import org.hibernate.resource.jdbc.spi.StatementInspector;

public class HibernateInspector implements StatementInspector {
   private String sql;

   public HibernateInspector() {
   }

   public String inspect(String sql) {
      this.sql = sql;
      LogUtils.info(SqlFormatter.format(sql), new Object[0]);
      SqlContextHolder.setSql(sql);
      return sql;
   }

   public String getSql() {
      return this.sql;
   }

   public void setSql(String sql) {
      this.sql = sql;
   }

   public static class RefreshEventListener extends DefaultRefreshEventListener {
      public RefreshEventListener() {
      }

      public void onRefresh(RefreshEvent event) throws HibernateException {
         Object object = event.getObject();
         String entityName = event.getEntityName();
         String sql = SqlContextHolder.getSql();
         LogUtils.info("Hibernate RefreshEventListener entityName: {} sql: {} object: {}", new Object[]{entityName, sql, JacksonUtils.toJSONString(object)});
         super.onRefresh(event);
      }

      public void onRefresh(RefreshEvent event, RefreshContext refreshedAlready) {
         Object object = event.getObject();
         String entityName = event.getEntityName();
         String sql = SqlContextHolder.getSql();
         LogUtils.info("Hibernate RefreshEventListener entityName: {} sql: {} refreshedAlready: {} object: {}", new Object[]{entityName, sql, refreshedAlready, JacksonUtils.toJSONString(object)});
         super.onRefresh(event, refreshedAlready);
      }
   }

   public static class PersistEventListener extends DefaultPersistEventListener {
      public PersistEventListener() {
      }

      public void onPersist(PersistEvent event) throws HibernateException {
         Object object = event.getObject();
         String sql = SqlContextHolder.getSql();
         String entityName = event.getEntityName();
         LogUtils.info("Hibernate PersistEventListener entityName: {} sql: {} object: {}", new Object[]{entityName, sql, JacksonUtils.toJSONString(object)});
         super.onPersist(event);
      }

      public void onPersist(PersistEvent event, PersistContext createCache) throws HibernateException {
         Object object = event.getObject();
         String sql = SqlContextHolder.getSql();
         String entityName = event.getEntityName();
         LogUtils.info("Hibernate PersistEventListener entityName: {} sql: {}  result: {} createCache: {}", new Object[]{entityName, sql, JacksonUtils.toJSONString(object), createCache});
         super.onPersist(event, createCache);
      }
   }

   public static class DeleteListener extends DefaultDeleteEventListener {
      public DeleteListener() {
      }

      public void onDelete(DeleteEvent event) throws HibernateException {
         String sql = SqlContextHolder.getSql();
         Object object = event.getObject();
         String entityName = event.getEntityName();
         LogUtils.info("Hibernate DeleteListener entityName: {} sql: {} object:{}", new Object[]{entityName, sql, JacksonUtils.toJSONString(object)});
         Collector collector = (Collector)ContextUtils.getBean(Collector.class, true);
         if (Objects.nonNull(collector)) {
            try {
               String replace = StringUtils.nullToEmpty(sql).replace("\r", "").replace("\n", "");
               collector.hook("ttc.monitor.jpa.delete.sql.hook").run(replace, () -> {
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

   public static class LoadListener extends DefaultLoadEventListener {
      public LoadListener() {
      }

      public void onLoad(LoadEvent event, LoadEventListener.LoadType loadType) throws HibernateException {
         String sql = SqlContextHolder.getSql();
         String entityClassName = event.getEntityClassName();
         Object result = event.getResult();
         LogUtils.info("Hibernate LoadListener entityName: {} sql: {}, result: {}", new Object[]{entityClassName, sql, JacksonUtils.toJSONString(result)});
         Collector collector = (Collector)ContextUtils.getBean(Collector.class, true);
         if (Objects.nonNull(collector)) {
            try {
               String replace = StringUtils.nullToEmpty(sql).replace("\r", "").replace("\n", "");
               collector.hook("ttc.monitor.jpa.load.sql.hook").run(replace, () -> {
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
}
