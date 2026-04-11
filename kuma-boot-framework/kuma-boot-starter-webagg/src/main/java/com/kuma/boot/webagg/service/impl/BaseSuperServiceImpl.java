package com.kuma.boot.webagg.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.querydsl.core.types.Predicate;
import com.kuma.boot.cache.redis.model.CacheKey;
import com.kuma.boot.cache.redis.model.CacheKeyBuilder;
import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.exception.IdempotencyException;
import com.kuma.boot.common.exception.LockException;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.jpa.base.repository.JpaSuperRepository;
import com.kuma.boot.data.mybatis.mybatisplus.base.mapper.MpSuperMapper;
import com.kuma.boot.lock.support.DistributedLock;
import com.kuma.boot.webagg.entity.SuperEntity;
import com.kuma.boot.webagg.service.AbstractBaseSuperService;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class BaseSuperServiceImpl<T extends SuperEntity<T, I>, I extends Serializable, M extends MpSuperMapper<T, I>, R extends JpaSuperRepository<T, I>> extends AbstractBaseSuperService<T, I, M> {
   protected static final int MAX_BATCH_KEY_SIZE = 20;
   @Autowired
   private R repository;
   @Resource
   private JdbcClient jdbcClient;
   @Resource
   private JdbcTemplate jdbcTemplate;
   @Resource
   private RedisRepository redisRepository;
   public static final int DEFAULT_BATCH_SIZE = 1000;

   public BaseSuperServiceImpl() {
   }

   public MpSuperMapper<T, I> mapper() {
      return (MpSuperMapper)super.getBaseMapper();
   }

   public JpaSuperRepository<T, I> respository() {
      return this.repository;
   }

   public JdbcTemplate jdbcTemplate() {
      return this.jdbcTemplate;
   }

   public JdbcClient jdbcClient() {
      return this.jdbcClient;
   }

   protected CacheKeyBuilder cacheKeyBuilder() {
      return () -> super.getEntityClass().getSimpleName();
   }

   public void refreshCache() {
      this.list().forEach(this::setCache);
   }

   public void clearCache() {
      this.list().forEach(this::delCache);
   }

   public T getByIdCache(I id) {
      CacheKey cacheKey = this.cacheKeyBuilder().key(new Object[]{id});
      return (T)(this.redisRepository.get(cacheKey, (k) -> (SuperEntity)super.getById(id), new boolean[0]));
   }

   @Transactional(
      readOnly = true
   )
   public T getByKey(CacheKey key, Function<CacheKey, Object> loader) {
      Object id = this.redisRepository.get(key, loader, new boolean[0]);
      return (T)(id == null ? null : this.getByIdCache(Convert.toLong(id)));
   }

   @Transactional(
      readOnly = true
   )
   public List<T> findByIds(@NonNull Collection<? extends Serializable> ids, Function<Collection<? extends Serializable>, Collection<T>> loader) {
      if (ids.isEmpty()) {
         return Collections.emptyList();
      } else {
         Stream var10000 = ids.stream();
         CacheKeyBuilder var10001 = this.cacheKeyBuilder();
         Objects.requireNonNull(var10001);
         CacheKeyBuilder var4 = var10001;
         List<CacheKey> keys = var10000.map((xva$0) -> var4.key(new Object[]{xva$0})).toList();
         List<List<CacheKey>> partitionKeys = Lists.partition(keys, 20);
         List<T> valueList = partitionKeys.stream().map((ks) -> (List)this.redisRepository.findByListCacheKey(ks)).flatMap(Collection::stream).toList();
         List<Serializable> keysList = Lists.newArrayList(ids);
         Set<Serializable> missedKeys = Sets.newLinkedHashSet();
         List<T> allList = new ArrayList();

         for(int i = 0; i < valueList.size(); ++i) {
            T v = (T)(valueList.get(i));
            Serializable k = (Serializable)keysList.get(i);
            if (v == null) {
               missedKeys.add(k);
            } else {
               allList.add(v);
            }
         }

         if (CollUtil.isNotEmpty(missedKeys)) {
            if (loader == null) {
               loader = this::listByIds;
            }

            Collection<T> missList = (Collection)loader.apply(missedKeys);
            missList.forEach(this::setCache);
            allList.addAll(missList);
         }

         return allList;
      }
   }

   public boolean saveIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate, Wrapper<T> countWrapper, String msg) {
      if (lock == null) {
         throw new LockException("\u5206\u5e03\u5f0f\u9501\u4e3a\u7a7a");
      } else if (StrUtil.isEmpty(lockKey)) {
         throw new LockException("\u9501\u7684key\u4e3a\u7a7a");
      } else {
         boolean var9;
         try {
            lock.lock(lockKey);
            if (!Objects.nonNull(predicate)) {
               if (!Objects.nonNull(countWrapper)) {
                  return true;
               }

               long count = super.count(countWrapper);
               if (count == 0L) {
                  var9 = super.save(entity);
                  return var9;
               }

               throw new IdempotencyException(StrUtil.isEmpty(msg) ? "\u6570\u636e\u5df2\u5b58\u5728" : msg);
            }

            long count = this.repository.count(predicate);
            if (count != 0L) {
               throw new IdempotencyException(StrUtil.isEmpty(msg) ? "\u6570\u636e\u5df2\u5b58\u5728" : msg);
            }

            this.repository.save(entity);
            var9 = true;
         } catch (Exception e) {
            LogUtils.error(e);
            return true;
         } finally {
            lock.unlock();
         }

         return var9;
      }
   }

   public boolean saveIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate, Wrapper<T> countWrapper) {
      return this.saveIdempotency(entity, lock, lockKey, predicate, countWrapper, (String)null);
   }

   public boolean saveOrUpdateIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate, Wrapper<T> countWrapper, String msg) {
      if (null != entity) {
         Class<?> cls = entity.getClass();
         TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
         if (null != tableInfo && StrUtil.isNotEmpty(tableInfo.getKeyProperty())) {
            Object idVal = ReflectionKit.getFieldValue(entity, tableInfo.getKeyProperty());
            return !StringUtils.checkValNull(idVal) && !Objects.isNull(this.getById((Serializable)idVal)) ? this.updateById(entity) : this.saveIdempotency(entity, lock, lockKey, predicate, countWrapper, StrUtil.isEmpty(msg) ? "\u6570\u636e\u5df2\u5b58\u5728" : msg);
         } else {
            throw ExceptionUtils.mpe("\u6267\u884c\u9519\u8bef,\u672a\u627e\u5230@TableId.", new Object[0]);
         }
      } else {
         return false;
      }
   }

   public boolean saveOrUpdateIdempotency(T entity, DistributedLock lock, String lockKey, Predicate predicate, Wrapper<T> countWrapper) {
      return this.saveOrUpdateIdempotency(entity, lock, lockKey, predicate, countWrapper, (String)null);
   }

   protected void setCache(T model) {
      Object id = this.getId(model);
      if (id != null) {
         CacheKey key = this.cacheKeyBuilder().key(new Object[]{id});
         this.redisRepository.set(key, model, new boolean[0]);
      }

   }

   protected Object getId(T model) {
      if (model instanceof SuperEntity) {
         return ((SuperEntity)model).getId();
      } else {
         TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
         if (tableInfo == null) {
            return null;
         } else {
            Class<?> keyType = tableInfo.getKeyType();
            if (keyType == null) {
               return null;
            } else {
               String keyProperty = tableInfo.getKeyProperty();
               Field idField = ReflectUtil.getField(this.getEntityClass(), keyProperty);
               return ReflectUtil.getFieldValue(model, idField);
            }
         }
      }
   }

   protected void delCache(Serializable... ids) {
      this.delCache((Collection)Arrays.asList(ids));
   }

   protected void delCache(Collection<?> idList) {
      CacheKey[] keys = (CacheKey[])idList.stream().map((id) -> this.cacheKeyBuilder().key(new Object[]{id})).toArray((x$0) -> new CacheKey[x$0]);
      this.redisRepository.del(keys);
   }

   protected void delCache(T model) {
      Object id = this.getId(model);
      if (id != null) {
         CacheKey key = this.cacheKeyBuilder().key(new Object[]{id});
         this.redisRepository.del(new CacheKey[]{key});
      }

   }

   public Class<T> getEntityClass() {
      return super.getEntityClass();
   }

   protected String getKeyProperty() {
      TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
      Assert.notNull(tableInfo, "\u9519\u8bef:\u65e0\u6cd5\u6267\u884c.\u56e0\u4e3a\u627e\u4e0d\u5230\u5b9e\u4f53\u7684 TableInfo \u7f13\u5b58!");
      String keyProperty = tableInfo.getKeyProperty();
      Assert.notNull(keyProperty, "\u9519\u8bef:\u65e0\u6cd5\u6267\u884c.\u56e0\u4e3a\u65e0\u6cd5\u4ece\u5b9e\u4f53\u4e2d\u627e\u5230\u4e3b\u952e\u7684\u5217!");
      return keyProperty;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public List<T> saveAll(List<T> list) {
      if (CollUtil.isNotEmpty(list)) {
         this.saveBatch(list, 1000);
      }

      return list;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public boolean updateAllById(Collection<T> entityList) {
      return this.updateBatchById(entityList, 1000);
   }

   public boolean updateByField(T t, SFunction<T, ?> field, Object fieldValue) {
      return ((LambdaUpdateChainWrapper)this.lambdaUpdate().eq(field, fieldValue)).update(t);
   }

   public List<T> findAll() {
      return this.lambdaQuery().list();
   }

   public Optional<T> findById(Serializable id) {
      return Optional.ofNullable((SuperEntity)((MpSuperMapper)this.baseMapper).selectById(id));
   }

   public Optional<T> findByField(SFunction<T, ?> field, Object fieldValue) {
      return ((LambdaQueryChainWrapper)this.lambdaQuery().eq(field, fieldValue)).oneOpt();
   }

   public List<T> findAllByIds(Collection<? extends Serializable> idList) {
      return (List<T>)(CollUtil.isEmpty(idList) ? new ArrayList(0) : ((MpSuperMapper)this.baseMapper).selectByIds(idList));
   }

   public List<T> findAllByField(SFunction<T, ?> field, Object fieldValue) {
      return ((LambdaQueryChainWrapper)this.lambdaQuery().eq(field, fieldValue)).list();
   }

   public List<T> findAllByFields(SFunction<T, ?> field, Collection<? extends Serializable> fieldValues) {
      return (List<T>)(CollUtil.isEmpty(fieldValues) ? new ArrayList(0) : ((LambdaQueryChainWrapper)this.lambdaQuery().in(field, fieldValues)).list());
   }

   public boolean existedById(Serializable id) {
      String keyProperty = this.getKeyProperty();
      return ((QueryChainWrapper)this.query().eq(keyProperty, id)).exists();
   }

   public boolean existedByField(SFunction<T, ?> field, Object fieldValue) {
      return ((LambdaQueryChainWrapper)this.lambdaQuery().eq(field, fieldValue)).exists();
   }

   public boolean existedByField(SFunction<T, ?> field, Object fieldValue, Serializable id) {
      String keyProperty = this.getKeyProperty();
      return ((QueryChainWrapper)((QueryChainWrapper)this.query().eq(this.getColumnName(field), fieldValue)).ne(keyProperty, id)).exists();
   }

   public Long countByField(SFunction<T, ?> field, Object fieldValue) {
      return ((LambdaQueryChainWrapper)this.lambdaQuery().eq(field, fieldValue)).count();
   }

   public boolean deleteById(Serializable id) {
      return SqlHelper.retBool(((MpSuperMapper)this.baseMapper).deleteById(id));
   }

   public boolean deleteByIds(Collection<? extends Serializable> idList) {
      return CollUtil.isNotEmpty(idList) ? SqlHelper.retBool(((MpSuperMapper)this.baseMapper).deleteByIds(idList)) : false;
   }

   public boolean deleteByField(SFunction<T, ?> field, Object fieldValue) {
      return ((LambdaUpdateChainWrapper)this.lambdaUpdate().eq(field, fieldValue)).remove();
   }

   public boolean deleteByFields(SFunction<T, ?> field, Collection<?> fieldValues) {
      return CollUtil.isEmpty(fieldValues) ? false : ((LambdaUpdateChainWrapper)this.lambdaUpdate().in(field, fieldValues)).remove();
   }

   public String getColumnName(SFunction<T, ?> function) {
      LambdaMeta meta = LambdaUtils.extract(function);
      Map<String, ColumnCache> columnMap = LambdaUtils.getColumnMap(meta.getInstantiatedClass());
      Assert.notEmpty(columnMap, "\u9519\u8bef:\u65e0\u6cd5\u6267\u884c.\u56e0\u4e3a\u65e0\u6cd5\u83b7\u53d6\u5230\u5b9e\u4f53\u7c7b\u7684\u8868\u5bf9\u5e94\u7f13\u5b58!");
      String fieldName = PropertyNamer.methodToProperty(meta.getImplMethodName());
      ColumnCache columnCache = (ColumnCache)columnMap.get(LambdaUtils.formatKey(fieldName));
      return columnCache.getColumn();
   }
}
