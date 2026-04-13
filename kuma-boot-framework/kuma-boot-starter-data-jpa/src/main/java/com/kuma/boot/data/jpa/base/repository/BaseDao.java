package com.kuma.boot.data.jpa.base.repository;

import jakarta.persistence.EntityManagerFactory;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

@Repository
public class BaseDao {
   protected final EntityManagerFactory emf;

   public BaseDao(EntityManagerFactory emf) {
      this.emf = emf;
   }

   public Session getSession() {
      return ((SessionFactory)this.emf.unwrap(SessionFactory.class)).getCurrentSession();
   }

   public Session getNewSession() {
      return ((SessionFactory)this.emf.unwrap(SessionFactory.class)).openSession();
   }

   public void flush() {
      this.getSession().flush();
   }

   public void clear() {
      this.getSession().clear();
   }

   public void close() {
      this.getSession().close();
   }

   public void save(final Object obj) {
      this.getSession().persist(obj);
   }

   public void update(final Object obj) {
      this.getSession().merge(obj);
   }

   public void saveOrUpdate(final Object obj) {
      this.getSession().persist(obj);
   }

   public void delete(final Serializable id, final Class<?> clazz) {
      this.getSession().remove(this.get(id, clazz));
   }

   public void delete(final Object obj) {
      this.getSession().remove(obj);
   }

   public int executeUpdate(final String hql, final Map<String, ?> props) {
      return this.createHQLQuery(hql, props).executeUpdate();
   }

   public int executeUpdate(final String hql, Object... props) {
      return this.createHQLQuery(hql, props).executeUpdate();
   }

   public int executeUpdateSQL(final String sql, final Map<String, ?> props) {
      return this.createSQLQuery(sql, props).executeUpdate();
   }

   public int executeUpdateSQL(final String sql, Object... props) {
      return this.createSQLQuery(sql, props).executeUpdate();
   }

   public Object get(final Serializable id, final Class<?> clazz) {
      return this.getSession().find(clazz, id);
   }

   public <T> T getById(final Serializable id, final Class<T> clazz) {
      return (T)this.getSession().find(clazz, id);
   }

   public void batchSave(List<?> models, int num) {
      int size = models.size();

      for(int i = 0; i < size; ++i) {
         this.save(models.get(i));
         if (i % num == 0 || i == size - 1) {
            this.flush();
            this.clear();
         }
      }

   }

   public void batchSaveOrUpdate(List<?> models, int num) {
      int size = models.size();

      for(int i = 0; i < size; ++i) {
         this.saveOrUpdate(models.get(i));
         if (i % num == 0 || i == size - 1) {
            this.flush();
            this.clear();
         }
      }

   }

   public void batchUpdate(List<?> models, int num) {
      int size = models.size();

      for(int i = 0; i < size; ++i) {
         this.update(models.get(i));
         if (i % num == 0 || i == size - 1) {
            this.flush();
            this.clear();
         }
      }

   }

   public Object findUnique(String hql, Map<String, ?> props) {
      return this.createHQLQuery(hql, props).uniqueResult();
   }

   public Object findUnique(String hql, Object... props) {
      return this.createHQLQuery(hql, props).uniqueResult();
   }

   public Object findSQLUnique(final String sql, Map<String, ?> props) {
      return this.createSQLQuery(sql, props).uniqueResult();
   }

   public Object findSQLUnique(final String sql, final Object... props) {
      return this.createSQLQuery(sql, props).uniqueResult();
   }

   public List<?> findByHQL(final String hql, final Map<String, ?> props) {
      return this.createHQLQuery(hql, props).list();
   }

   public List<?> findByHQL(final String hql, final Object... props) {
      return this.createHQLQuery(hql, props).list();
   }

   public List<?> findByHQLLimit(final String hql, int limit, final Map<String, ?> props) {
      return this.createHQLQuery(hql, props).setMaxResults(limit).list();
   }

   public List<?> findByHQLLimit(final String hql, int limit, final Object... props) {
      return this.createHQLQuery(hql, props).setMaxResults(limit).list();
   }

   public PageModel findPage(final int pageNum, final int pageSize, String hql, final Map<String, ?> props) {
      long total = ((Number)this.findUnique(toCount(hql), props)).longValue();
      List<?> rows = Collections.emptyList();
      if (total != 0L) {
         Query query = this.createHQLQuery(hql, props);
         query.setFirstResult(getFirst(pageNum, pageSize)).setMaxResults(pageSize);
         rows = query.list();
      }

      return new PageModel(total, rows);
   }

   public PageModel findPage(final int pageNum, final int pageSize, String hql, final Object... props) {
      long total = ((Number)this.findUnique(toCount(hql), props)).longValue();
      List<?> rows = Collections.emptyList();
      if (total != 0L) {
         Query query = this.createHQLQuery(hql, props);
         query.setFirstResult(getFirst(pageNum, pageSize)).setMaxResults(pageSize);
         rows = query.list();
      }

      return new PageModel(total, rows);
   }

   public PageModel findPage_(final int pageNum, final int pageSize, final String hql, final String countHql, final Map<String, ?> props) {
      long total = ((Number)this.findUnique(countHql, props)).longValue();
      List<?> rows = Collections.emptyList();
      if (total != 0L) {
         Query query = this.createHQLQuery(hql, props);
         query.setFirstResult(getFirst(pageNum, pageSize)).setMaxResults(pageSize);
         rows = query.list();
      }

      return new PageModel(total, rows);
   }

   public PageModel findPage_(final int pageNum, final int pageSize, final String hql, final String countHql, final Object... props) {
      long total = ((Number)this.findUnique(countHql, props)).longValue();
      List<?> rows = Collections.emptyList();
      if (total != 0L) {
         Query query = this.createHQLQuery(hql, props);
         query.setFirstResult(getFirst(pageNum, pageSize)).setMaxResults(pageSize);
         rows = query.list();
      }

      return new PageModel(total, rows);
   }

   public List<Map<String, Object>> findSQLMap(final String sql, final Map<String, ?> props) {
      return this.createSQLQuery(sql, props).setResultListTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
   }

   public List<Map<String, Object>> findSQLMap(final String sql, final Object... props) {
      return this.createSQLQuery(sql, props).setResultListTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
   }

   public List<?> findSQLEntity(final String sql, String[] names, final Class<?>... clazz) {
      Query query = this.createSQLQuery(sql);
      if (clazz != null) {
         int size = clazz.length;

         for(int i = 0; i < size; ++i) {
            query.setParameter(names[i], clazz[i]);
         }
      }

      return query.list();
   }

   public List<?> findSQLEntity(final String sql, final Map<String, ?> props, String[] names, final Class<?>... clazz) {
      Query query = this.createSQLQuery(sql, props);
      if (clazz != null) {
         int size = clazz.length;

         for(int i = 0; i < size; ++i) {
            query.setParameter(names[i], clazz[i]);
         }
      }

      return query.list();
   }

   public PageModel findSQLPage(final int pageNum, final int pageSize, String sql, final Map<String, ?> props) {
      long total = ((Number)this.createSQLQuery(toCount(sql), props).uniqueResult()).longValue();
      List<?> rows = Collections.emptyList();
      if (total != 0L) {
         Query query = this.createSQLQuery(sql, props);
         query.setFirstResult(getFirst(pageNum, pageSize)).setMaxResults(pageSize).setResultListTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
         rows = query.list();
      }

      return new PageModel(total, rows);
   }

   public PageModel findSQLPageNoCountTotal(final int pageNum, final int pageSize, String sql, final Map<String, ?> props) {
      List<?> rows = Collections.emptyList();
      Query query = this.createSQLQuery(sql, props);
      query.setFirstResult(getFirst(pageNum, pageSize)).setMaxResults(pageSize).setResultListTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
      rows = query.list();
      return new PageModel(0L, rows);
   }

   public Long getCountBySql(String sql, final Object... parms) {
      return ((Number)this.createSQLQuery(sql, parms).uniqueResult()).longValue();
   }

   public PageModel findSQLPage(final int pageNum, final int pageSize, String sql, final Object... props) {
      long total = ((Number)this.createSQLQuery(toCount(sql), props).uniqueResult()).longValue();
      List<?> rows = Collections.emptyList();
      if (total != 0L) {
         Query query = this.createSQLQuery(sql, props);
         query.setFirstResult(getFirst(pageNum, pageSize)).setMaxResults(pageSize).setResultListTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
         rows = query.list();
      }

      return new PageModel(total, rows);
   }

   public PageModel findSQLPageEntity(final int pageNum, final int pageSize, String sql, final Map<String, ?> props, final String[] names, final Class<?>... clazz) {
      long total = ((Number)this.createSQLQuery(toCount(sql), props).uniqueResult()).longValue();
      List<?> rows = Collections.emptyList();
      if (total != 0L) {
         Query query = this.createSQLQuery(sql, props);
         query.setFirstResult(getFirst(pageNum, pageSize)).setMaxResults(pageSize);
         if (clazz != null) {
            int size = clazz.length;

            for(int i = 0; i < size; ++i) {
               query.setParameter(names[i], clazz[i]);
            }
         }

         rows = query.list();
      }

      return new PageModel(total, rows);
   }

   public PageModel findSQLPage_(final int pageNum, final int pageSize, final String sql, final String countSql, final Map<String, ?> props) {
      long total = ((Number)this.createSQLQuery(countSql, props).uniqueResult()).longValue();
      List<?> rows = Collections.emptyList();
      if (total != 0L) {
         Query query = this.createSQLQuery(sql, props);
         query.setFirstResult(getFirst(pageNum, pageSize)).setMaxResults(pageSize).setResultListTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
         rows = query.list();
      }

      return new PageModel(total, rows);
   }

   public PageModel findSQLPage_(final int pageNum, final int pageSize, final String sql, final String countSql, final Object... props) {
      long total = ((Number)this.createSQLQuery(countSql, props).uniqueResult()).longValue();
      List<?> rows = Collections.emptyList();
      if (total != 0L) {
         Query query = this.createSQLQuery(sql, props);
         query.setFirstResult(getFirst(pageNum, pageSize)).setMaxResults(pageSize).setResultListTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
         rows = query.list();
      }

      return new PageModel(total, rows);
   }

   public PageModel findSQLPageEntity_(final int pageNum, final int pageSize, String sql, final String countSql, final Map<String, ?> props, final String[] names, final Class<?>... clazz) {
      long total = ((Number)this.createSQLQuery(countSql, props).uniqueResult()).longValue();
      List<?> rows = Collections.emptyList();
      if (total != 0L) {
         Query query = this.createSQLQuery(sql, props);
         query.setFirstResult(getFirst(pageNum, pageSize)).setMaxResults(pageSize);
         if (clazz != null) {
            int size = clazz.length;

            for(int i = 0; i < size; ++i) {
               query.setParameter(names[i], clazz[i]);
            }
         }

         rows = query.list();
      }

      return new PageModel(total, rows);
   }

   private Query createHQLQuery(final String queryString, final Map<String, ?> values) {
      Query query = this.getSession().createQuery(queryString);
      if (values != null) {
         query.setProperties(values);
      }

      return query;
   }

   private Query createSQLQuery(final String queryString, final Map<String, ?> values) {
      Query query = this.getSession().createQuery(queryString);
      if (values != null) {
         query.setProperties(values);
      }

      return query;
   }

   private Query createHQLQuery(final String queryString, final Object... values) {
      Query query = this.getSession().createQuery(queryString);
      if (values != null) {
         int size = values.length;

         for(int i = 0; i < size; ++i) {
            query.setParameter(i, values[i]);
         }
      }

      return query;
   }

   private Query createSQLQuery(final String queryString, final Object... values) {
      Query query = this.getSession().createQuery(queryString);
      if (values != null) {
         int size = values.length;

         for(int i = 0; i < size; ++i) {
            query.setParameter(i, values[i]);
         }
      }

      return query;
   }

   private static String toCount(String sql) {
      String countSql = sql.trim().replaceFirst("(?i)^.*?from", "select count(*) from").replaceAll("(?i)fetch", "").replaceFirst("(?i)order\\s+by((\\s*,)?\\s*\\S+\\s*((asc)|(desc))?)*$", "");
      return countSql;
   }

   private static int getFirst(int page, int max) {
      return (page - 1) * max;
   }

   public static class PageModel {
      private long total;
      private List<?> rows;

      public PageModel(long total, List<?> rows) {
         this.total = total;
         this.rows = rows;
      }

      public long getTotal() {
         return this.total;
      }

      public void setTotal(long total) {
         this.total = total;
      }

      public List<?> getRows() {
         return this.rows;
      }

      public void setRows(List<?> rows) {
         this.rows = rows;
      }
   }
}
