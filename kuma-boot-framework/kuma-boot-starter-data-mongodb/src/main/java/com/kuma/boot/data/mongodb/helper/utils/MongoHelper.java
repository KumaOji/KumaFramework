package com.kuma.boot.data.mongodb.helper.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mongodb.autoconfigure.properties.MongodbProperties;
import com.kuma.boot.data.mongodb.helper.bean.CreateTime;
import com.kuma.boot.data.mongodb.helper.bean.InitValue;
import com.kuma.boot.data.mongodb.helper.bean.Page;
import com.kuma.boot.data.mongodb.helper.bean.SlowQuery;
import com.kuma.boot.data.mongodb.helper.bean.SortBuilder;
import com.kuma.boot.data.mongodb.helper.bean.UpdateBuilder;
import com.kuma.boot.data.mongodb.helper.bean.UpdateTime;
import com.kuma.boot.data.mongodb.helper.config.Constant;
import com.kuma.boot.data.mongodb.helper.reflection.ReflectionUtil;
import com.kuma.boot.data.mongodb.helper.reflection.SerializableFunction;
import jakarta.annotation.PostConstruct;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.QueryMapper;
import org.springframework.data.mongodb.core.convert.UpdateMapper;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.query.Query;

public class MongoHelper {
   @Autowired
   protected MongoConverter mongoConverter;
   @Autowired
   private MongodbProperties mongodbProperties;
   protected QueryMapper queryMapper;
   protected UpdateMapper updateMapper;
   @Autowired
   protected MongoTemplate mongoTemplate;

   public MongoHelper() {
   }

   public MongoTemplate getMongoTemplate() {
      return this.mongoTemplate;
   }

   @PostConstruct
   public void init() {
      this.queryMapper = new QueryMapper(this.mongoConverter);
      this.updateMapper = new UpdateMapper(this.mongoConverter);
   }

   private void insertSlowQuery(String log, Long queryTime) {
      if (this.mongodbProperties.getSlowQuery()) {
         SlowQuery slowQuery = new SlowQuery();
         slowQuery.setQuery(log);
         slowQuery.setTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
         slowQuery.setQueryTime(queryTime);
         slowQuery.setSystem(SystemTool.getSystem());
         StackTraceElement[] stack = Thread.currentThread().getStackTrace();
         StringBuilder stackStr = new StringBuilder();

         for(StackTraceElement stackTraceElement : stack) {
            stackStr.append(stackTraceElement.getClassName()).append(".").append(stackTraceElement.getMethodName()).append(":").append(stackTraceElement.getLineNumber()).append("\n");
         }

         slowQuery.setStack(stackStr.toString());
         this.mongoTemplate.insert(slowQuery);
      }

   }

   private void logQuery(Class<?> clazz, Query query, Long startTime) {
      MongoPersistentEntity<?> entity = (MongoPersistentEntity)this.mongoConverter.getMappingContext().getPersistentEntity(clazz);
      Document mappedQuery = this.queryMapper.getMappedObject(query.getQueryObject(), entity);
      Document mappedField = this.queryMapper.getMappedObject(query.getFieldsObject(), entity);
      Document mappedSort = this.queryMapper.getMappedObject(query.getSortObject(), entity);
      String var10000 = this.getCollectionName(clazz);
      String log = "\ndb." + var10000 + ".find(";
      log = log + FormatUtils.bson(mappedQuery.toJson()) + ")";
      if (!query.getFieldsObject().isEmpty()) {
         log = log + ".projection(";
         log = log + FormatUtils.bson(mappedField.toJson()) + ")";
      }

      if (query.isSorted()) {
         log = log + ".sort(";
         log = log + FormatUtils.bson(mappedSort.toJson()) + ")";
      }

      if ((long)query.getLimit() != 0L) {
         log = log + ".limit(" + query.getLimit() + ")";
      }

      if (query.getSkip() != 0L) {
         log = log + ".skip(" + query.getSkip() + ")";
      }

      log = log + ";";
      Long queryTime = System.currentTimeMillis() - startTime;
      if (queryTime > this.mongodbProperties.getSlowTime()) {
         this.insertSlowQuery(log, queryTime);
      }

      if (this.mongodbProperties.getPrint()) {
         LogUtils.info(log + "\n\u6267\u884c\u65f6\u95f4:" + queryTime + "ms", new Object[0]);
      }

   }

   private String getCollectionName(Class<?> clazz) {
      org.springframework.data.mongodb.core.mapping.Document document = (org.springframework.data.mongodb.core.mapping.Document)clazz.getAnnotation(org.springframework.data.mongodb.core.mapping.Document.class);
      if (document != null) {
         if (StrUtil.isNotEmpty(document.value())) {
            return document.value();
         }

         if (StrUtil.isNotEmpty(document.collection())) {
            return document.collection();
         }
      }

      return StrUtil.lowerFirst(clazz.getSimpleName());
   }

   private void logCount(Class<?> clazz, Query query, Long startTime) {
      MongoPersistentEntity<?> entity = (MongoPersistentEntity)this.mongoConverter.getMappingContext().getPersistentEntity(clazz);
      Document mappedQuery = this.queryMapper.getMappedObject(query.getQueryObject(), entity);
      String log = "\ndb." + StrUtil.lowerFirst(clazz.getSimpleName()) + ".find(";
      log = log + FormatUtils.bson(mappedQuery.toJson()) + ")";
      log = log + ".count();";
      Long queryTime = System.currentTimeMillis() - startTime;
      if (queryTime > this.mongodbProperties.getSlowTime()) {
         this.insertSlowQuery(log, queryTime);
      }

      if (this.mongodbProperties.getPrint()) {
         LogUtils.info(log + "\n\u6267\u884c\u65f6\u95f4:" + queryTime + "ms", new Object[0]);
      }

   }

   private void logDelete(Class<?> clazz, Query query, Long startTime) {
      MongoPersistentEntity<?> entity = (MongoPersistentEntity)this.mongoConverter.getMappingContext().getPersistentEntity(clazz);
      Document mappedQuery = this.queryMapper.getMappedObject(query.getQueryObject(), entity);
      String log = "\ndb." + StrUtil.lowerFirst(clazz.getSimpleName()) + ".remove(";
      log = log + FormatUtils.bson(mappedQuery.toJson()) + ")";
      log = log + ";";
      Long queryTime = System.currentTimeMillis() - startTime;
      if (queryTime > this.mongodbProperties.getSlowTime()) {
         this.insertSlowQuery(log, queryTime);
      }

      if (this.mongodbProperties.getPrint()) {
         LogUtils.info(log + "\n\u6267\u884c\u65f6\u95f4:" + queryTime + "ms", new Object[0]);
      }

   }

   private void logUpdate(Class<?> clazz, Query query, UpdateBuilder updateBuilder, boolean multi, Long startTime) {
      MongoPersistentEntity<?> entity = (MongoPersistentEntity)this.mongoConverter.getMappingContext().getPersistentEntity(clazz);
      Document mappedQuery = this.queryMapper.getMappedObject(query.getQueryObject(), entity);
      Document mappedUpdate = this.updateMapper.getMappedObject(updateBuilder.toUpdate().getUpdateObject(), entity);
      String log = "\ndb." + StrUtil.lowerFirst(clazz.getSimpleName()) + ".update(";
      log = log + FormatUtils.bson(mappedQuery.toJson()) + ",";
      log = log + FormatUtils.bson(mappedUpdate.toJson()) + ",";
      log = log + FormatUtils.bson("{multi:" + multi + "})");
      log = log + ";";
      Long queryTime = System.currentTimeMillis() - startTime;
      if (queryTime > this.mongodbProperties.getSlowTime()) {
         this.insertSlowQuery(log, queryTime);
      }

      if (this.mongodbProperties.getPrint()) {
         LogUtils.info(log + "\n\u6267\u884c\u65f6\u95f4:" + queryTime + "ms", new Object[0]);
      }

   }

   private void logSave(Object object, LocalDateTime startTime, Boolean isInsert) {
      JSONObject jsonObject = JSONUtil.parseObj(object);
      if (isInsert) {
         jsonObject.remove("id");
      }

      String log = "\ndb." + StrUtil.lowerFirst(object.getClass().getSimpleName()) + ".save(";
      log = log + JSONUtil.toJsonPrettyStr(jsonObject);
      log = log + ");";
      Long queryTime = LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - startTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
      if (queryTime > this.mongodbProperties.getSlowTime()) {
         this.insertSlowQuery(log, queryTime);
      }

      if (this.mongodbProperties.getPrint()) {
         LogUtils.info(log + "\n\u6267\u884c\u65f6\u95f4:" + queryTime + "ms", new Object[0]);
      }

   }

   private void logSave(List<?> list, LocalDateTime startTime) {
      List<JSONObject> cloneList = new ArrayList();

      for(Object item : list) {
         JSONObject jsonObject = JSONUtil.parseObj(item);
         jsonObject.remove("id");
         cloneList.add(jsonObject);
      }

      Object object = list.get(0);
      String log = "\ndb." + StrUtil.lowerFirst(object.getClass().getSimpleName()) + ".save(";
      log = log + JSONUtil.toJsonPrettyStr(cloneList);
      log = log + ");";
      Long queryTime = LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - startTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
      if (queryTime > this.mongodbProperties.getSlowTime()) {
         this.insertSlowQuery(log, queryTime);
      }

      if (this.mongodbProperties.getPrint()) {
         LogUtils.info(log + "\n\u6267\u884c\u65f6\u95f4:" + queryTime + "ms", new Object[0]);
      }

   }

   public String insertOrUpdate(Object object) {
      LocalDateTime time = LocalDateTime.now();
      String id = (String)ReflectUtil.getFieldValue(object, "id");
      Object objectOrg = StrUtil.isNotEmpty(id) ? this.findById(id, object.getClass()) : null;
      if (objectOrg == null) {
         this.setCreateTime(object, time);
         this.setUpdateTime(object, time);
         this.setDefaultVaule(object);
         ReflectUtil.setFieldValue(object, "id", (Object)null);
         this.mongoTemplate.save(object);
         id = (String)ReflectUtil.getFieldValue(object, "id");
         this.logSave(object, time, true);
      } else {
         Field[] fields = ReflectUtil.getFields(object.getClass());

         for(Field field : fields) {
            if (!field.getName().equals("id") && ReflectUtil.getFieldValue(object, field) != null) {
               ReflectUtil.setFieldValue(objectOrg, field, ReflectUtil.getFieldValue(object, field));
            }
         }

         this.setUpdateTime(objectOrg, time);
         this.mongoTemplate.save(objectOrg);
         this.logSave(objectOrg, time, false);
      }

      return id;
   }

   public String insert(Object object) {
      ReflectUtil.setFieldValue(object, "id", (Object)null);
      this.insertOrUpdate(object);
      return (String)ReflectUtil.getFieldValue(object, "id");
   }

   public <T> void insertAll(List<T> list) {
      LocalDateTime time = LocalDateTime.now();

      for(Object object : list) {
         ReflectUtil.setFieldValue(object, "id", (Object)null);
         this.setCreateTime(object, time);
         this.setUpdateTime(object, time);
         this.setDefaultVaule(object);
      }

      this.mongoTemplate.insertAll(list);
      this.logSave(list, time);
   }

   private void setUpdateTime(Object object, LocalDateTime time) {
      Field[] fields = ReflectUtil.getFields(object.getClass());

      for(Field field : fields) {
         if (field.isAnnotationPresent(UpdateTime.class) && field.getType().equals(Long.class)) {
            ReflectUtil.setFieldValue(object, field, time);
         }
      }

   }

   private void setCreateTime(Object object, LocalDateTime time) {
      Field[] fields = ReflectUtil.getFields(object.getClass());

      for(Field field : fields) {
         if (field.isAnnotationPresent(CreateTime.class) && field.getType().equals(Long.class)) {
            ReflectUtil.setFieldValue(object, field, time);
         }
      }

   }

   public void updateById(Object object) {
      if (!StrUtil.isEmpty((String)ReflectUtil.getFieldValue(object, "id"))) {
         if (this.findById((String)ReflectUtil.getFieldValue(object, "id"), object.getClass()) != null) {
            this.insertOrUpdate(object);
         }
      }
   }

   public void updateAllColumnById(Object object) {
      if (!StrUtil.isEmpty((String)ReflectUtil.getFieldValue(object, "id"))) {
         if (this.findById((String)ReflectUtil.getFieldValue(object, "id"), object.getClass()) != null) {
            LocalDateTime time = LocalDateTime.now();
            this.setUpdateTime(object, time);
            this.mongoTemplate.save(object);
            this.logSave(object, time, false);
         }
      }
   }

   public void updateFirst(CriteriaWrapper criteriaWrapper, UpdateBuilder updateBuilder, Class<?> clazz) {
      Long time = System.currentTimeMillis();
      Query query = new Query(criteriaWrapper.build());
      this.mongoTemplate.updateFirst(query, updateBuilder.toUpdate(), clazz);
      this.logUpdate(clazz, query, updateBuilder, false, time);
   }

   public void updateMulti(CriteriaWrapper criteriaWrapper, UpdateBuilder updateBuilder, Class<?> clazz) {
      Long time = System.currentTimeMillis();
      Query query = new Query(criteriaWrapper.build());
      this.mongoTemplate.updateMulti(new Query(criteriaWrapper.build()), updateBuilder.toUpdate(), clazz);
      this.logUpdate(clazz, query, updateBuilder, true, time);
   }

   public void deleteById(String id, Class<?> clazz) {
      if (!StrUtil.isEmpty(id)) {
         this.deleteByQuery((new CriteriaAndWrapper()).eq(Constant::getId, id), clazz);
      }
   }

   public void deleteByIds(List<String> ids, Class<?> clazz) {
      if (ids != null && ids.size() != 0) {
         this.deleteByQuery((new CriteriaAndWrapper()).in(Constant::getId, ids), clazz);
      }
   }

   public void deleteByQuery(CriteriaWrapper criteriaWrapper, Class<?> clazz) {
      Long time = System.currentTimeMillis();
      Query query = new Query(criteriaWrapper.build());
      this.mongoTemplate.remove(query, clazz);
      this.logDelete(clazz, query, time);
   }

   private void setDefaultVaule(Object object) {
      Field[] fields = ReflectUtil.getFields(object.getClass());

      for(Field field : fields) {
         if (field.isAnnotationPresent(InitValue.class)) {
            InitValue defaultValue = (InitValue)field.getAnnotation(InitValue.class);
            String value = defaultValue.value();
            if (ReflectUtil.getFieldValue(object, field) == null) {
               Class<?> type = field.getType();
               if (type.equals(String.class)) {
                  ReflectUtil.setFieldValue(object, field, value);
               }

               if (type.equals(Short.class)) {
                  ReflectUtil.setFieldValue(object, field, Short.parseShort(value));
               }

               if (type.equals(Integer.class)) {
                  ReflectUtil.setFieldValue(object, field, Integer.parseInt(value));
               }

               if (type.equals(Long.class)) {
                  ReflectUtil.setFieldValue(object, field, Long.parseLong(value));
               }

               if (type.equals(Float.class)) {
                  ReflectUtil.setFieldValue(object, field, Float.parseFloat(value));
               }

               if (type.equals(Double.class)) {
                  ReflectUtil.setFieldValue(object, field, Double.parseDouble(value));
               }

               if (type.equals(Boolean.class)) {
                  ReflectUtil.setFieldValue(object, field, Boolean.parseBoolean(value));
               }
            }
         }
      }

   }

   public <R, E> void addCountById(String id, SerializableFunction<E, R> property, Number count, Class<?> clazz) {
      UpdateBuilder updateBuilder = (new UpdateBuilder()).inc(property, count);
      this.updateFirst((new CriteriaAndWrapper()).eq(Constant::getId, id), updateBuilder, clazz);
   }

   public <T> Page<T> findPage(CriteriaWrapper criteriaWrapper, Page<?> page, Class<T> clazz) {
      SortBuilder sortBuilder = new SortBuilder(Constant::getId, Direction.DESC);
      return this.<T>findPage(criteriaWrapper, sortBuilder, page, clazz);
   }

   public <T> Page<T> findPage(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Page<?> page, Class<T> clazz) {
      Page<T> pageResp = new Page<T>();
      pageResp.setCurr(page.getCurr());
      pageResp.setLimit(page.getLimit());
      if (page.getQueryCount()) {
         Long count = this.findCountByQuery(criteriaWrapper, clazz);
         pageResp.setCount(count);
      }

      Query query = new Query(criteriaWrapper.build());
      query.with(sortBuilder.toSort());
      query.skip((long)(page.getCurr() - 1) * (long)page.getLimit());
      query.limit(page.getLimit());
      Long systemTime = System.currentTimeMillis();
      List<T> list = this.mongoTemplate.find(query, clazz);
      this.logQuery(clazz, query, systemTime);
      pageResp.setList(list);
      return pageResp;
   }

   public <T> Page<T> findPage(SortBuilder sortBuilder, Page<?> page, Class<T> clazz) {
      return this.<T>findPage(new CriteriaAndWrapper(), sortBuilder, page, clazz);
   }

   public <T> Page<T> findPage(Page<?> page, Class<T> clazz) {
      return this.findPage((CriteriaWrapper)(new CriteriaAndWrapper()), page, clazz);
   }

   public <T> T findById(String id, Class<T> clazz) {
      if (StrUtil.isEmpty(id)) {
         return null;
      } else {
         Long systemTime = System.currentTimeMillis();
         T t = (T)this.mongoTemplate.findById(id, clazz);
         CriteriaAndWrapper criteriaAndWrapper = (new CriteriaAndWrapper()).eq(Constant::getId, id);
         this.logQuery(clazz, new Query(criteriaAndWrapper.build()), systemTime);
         return t;
      }
   }

   public <T> T findOneByQuery(CriteriaWrapper criteriaWrapper, Class<T> clazz) {
      SortBuilder sortBuilder = new SortBuilder(Constant::getId, Direction.DESC);
      return (T)this.findOneByQuery(criteriaWrapper, sortBuilder, clazz);
   }

   public <T> T findOneByQuery(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz) {
      Query query = new Query(criteriaWrapper.build());
      query.limit(1);
      query.with(sortBuilder.toSort());
      Long systemTime = System.currentTimeMillis();
      T t = (T)this.mongoTemplate.findOne(query, clazz);
      this.logQuery(clazz, query, systemTime);
      return t;
   }

   public <T> T findOneByQuery(SortBuilder sortBuilder, Class<T> clazz) {
      return (T)this.findOneByQuery(new CriteriaAndWrapper(), sortBuilder, clazz);
   }

   public <T> List<T> findListByQuery(CriteriaWrapper criteriaWrapper, Class<T> clazz) {
      SortBuilder sortBuilder = (new SortBuilder()).add(Constant::getId, Direction.DESC);
      return this.<T>findListByQuery(criteriaWrapper, sortBuilder, clazz);
   }

   public <T> List<T> findListByQuery(CriteriaWrapper criteriaWrapper, SortBuilder sortBuilder, Class<T> clazz) {
      Query query = new Query(criteriaWrapper.build());
      query.with(sortBuilder.toSort());
      Long systemTime = System.currentTimeMillis();
      List<T> list = this.mongoTemplate.find(query, clazz);
      this.logQuery(clazz, query, systemTime);
      return list;
   }

   public <T, R, E> List<T> findPropertiesByQuery(CriteriaWrapper criteriaWrapper, Class<?> documentClass, SerializableFunction<E, R> property, Class<T> propertyClass) {
      Query query = new Query(criteriaWrapper.build());
      query.fields().include(ReflectionUtil.getFieldName(property));
      Long systemTime = System.currentTimeMillis();
      List<?> list = this.mongoTemplate.find(query, documentClass);
      this.logQuery(documentClass, query, systemTime);
      List<T> propertyList = this.<T>extractProperty(list, ReflectionUtil.getFieldName(property), propertyClass);
      return propertyList;
   }

   public <R, E> List<String> findPropertiesByQuery(CriteriaWrapper criteriaWrapper, Class<?> documentClass, SerializableFunction<E, R> property) {
      return this.findPropertiesByQuery(criteriaWrapper, documentClass, property, String.class);
   }

   public <R, E> List<String> findPropertiesByIds(List<String> ids, Class<?> clazz, SerializableFunction<E, R> property) {
      CriteriaAndWrapper criteriaAndWrapper = (new CriteriaAndWrapper()).in(Constant::getId, ids);
      return this.findPropertiesByQuery(criteriaAndWrapper, clazz, property);
   }

   public List<String> findIdsByQuery(CriteriaWrapper criteriaWrapper, Class<?> clazz) {
      return this.findPropertiesByQuery(criteriaWrapper, clazz, Constant::getId);
   }

   public <T> List<T> findListByIds(Collection<String> ids, Class<T> clazz) {
      CriteriaAndWrapper criteriaAndWrapper = (new CriteriaAndWrapper()).in(Constant::getId, ids);
      return this.<T>findListByQuery(criteriaAndWrapper, clazz);
   }

   public <T> List<T> findListByIds(Collection<String> ids, SortBuilder sortBuilder, Class<T> clazz) {
      CriteriaAndWrapper criteriaAndWrapper = (new CriteriaAndWrapper()).in(Constant::getId, ids);
      return this.<T>findListByQuery(criteriaAndWrapper, sortBuilder, clazz);
   }

   public <T> List<T> findListByIds(String[] ids, SortBuilder sortBuilder, Class<T> clazz) {
      return this.findListByIds(Arrays.asList(ids), sortBuilder, clazz);
   }

   public <T> List<T> findListByIds(String[] ids, Class<T> clazz) {
      SortBuilder sortBuilder = new SortBuilder(Constant::getId, Direction.DESC);
      return this.findListByIds(ids, sortBuilder, clazz);
   }

   public <T> List<T> findAll(Class<T> clazz) {
      SortBuilder sortBuilder = new SortBuilder(Constant::getId, Direction.DESC);
      return this.<T>findListByQuery(new CriteriaAndWrapper(), sortBuilder, clazz);
   }

   public <T> List<T> findAll(SortBuilder sortBuilder, Class<T> clazz) {
      return this.<T>findListByQuery(new CriteriaAndWrapper(), sortBuilder, clazz);
   }

   public List<String> findAllIds(Class<?> clazz) {
      return this.findIdsByQuery(new CriteriaAndWrapper(), clazz);
   }

   public Long findCountByQuery(CriteriaWrapper criteriaWrapper, Class<?> clazz) {
      Long systemTime = System.currentTimeMillis();
      Long count = null;
      Query query = new Query(criteriaWrapper.build());
      if (query.getQueryObject().isEmpty()) {
         count = this.mongoTemplate.getCollection(this.mongoTemplate.getCollectionName(clazz)).estimatedDocumentCount();
      } else {
         count = this.mongoTemplate.count(query, clazz);
      }

      this.logCount(clazz, query, systemTime);
      return count;
   }

   public Long findAllCount(Class<?> clazz) {
      return this.findCountByQuery(new CriteriaAndWrapper(), clazz);
   }

   private <T> List<T> extractProperty(List<?> list, String property, Class<T> clazz) {
      Set<T> rs = new HashSet();

      for(Object object : list) {
         Object value = ReflectUtil.getFieldValue(object, property);
         if (value != null && value.getClass().equals(clazz)) {
            rs.add((T)value);
         }
      }

      return new ArrayList(rs);
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "getId":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/kuma/boot/data/mongodb/helper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/kuma/boot/data/mongodb/helper/config/Constant") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return (SerializableFunction<Constant, String>) Constant::getId;
            } else if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/kuma/boot/data/mongodb/helper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/kuma/boot/data/mongodb/helper/config/Constant") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return (SerializableFunction<Constant, String>) Constant::getId;
            } else if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/kuma/boot/data/mongodb/helper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/kuma/boot/data/mongodb/helper/config/Constant") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return (SerializableFunction<Constant, String>) Constant::getId;
            } else if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/kuma/boot/data/mongodb/helper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/kuma/boot/data/mongodb/helper/config/Constant") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return (SerializableFunction<Constant, String>) Constant::getId;
            } else if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/kuma/boot/data/mongodb/helper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/kuma/boot/data/mongodb/helper/config/Constant") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return (SerializableFunction<Constant, String>) Constant::getId;
            } else if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/kuma/boot/data/mongodb/helper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/kuma/boot/data/mongodb/helper/config/Constant") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return (SerializableFunction<Constant, String>) Constant::getId;
            } else if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/kuma/boot/data/mongodb/helper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/kuma/boot/data/mongodb/helper/config/Constant") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return (SerializableFunction<Constant, String>) Constant::getId;
            } else if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/kuma/boot/data/mongodb/helper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/kuma/boot/data/mongodb/helper/config/Constant") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return (SerializableFunction<Constant, String>) Constant::getId;
            } else if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/kuma/boot/data/mongodb/helper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/kuma/boot/data/mongodb/helper/config/Constant") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return (SerializableFunction<Constant, String>) Constant::getId;
            } else if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/kuma/boot/data/mongodb/helper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/kuma/boot/data/mongodb/helper/config/Constant") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return (SerializableFunction<Constant, String>) Constant::getId;
            } else if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/kuma/boot/data/mongodb/helper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/kuma/boot/data/mongodb/helper/config/Constant") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return (SerializableFunction<Constant, String>) Constant::getId;
            } else if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/kuma/boot/data/mongodb/helper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/kuma/boot/data/mongodb/helper/config/Constant") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return (SerializableFunction<Constant, String>) Constant::getId;
            } else if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("com/kuma/boot/data/mongodb/helper/reflection/SerializableFunction") && lambda.getFunctionalInterfaceMethodName().equals("apply") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("com/kuma/boot/data/mongodb/helper/config/Constant") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return (SerializableFunction<Constant, String>) Constant::getId;
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
