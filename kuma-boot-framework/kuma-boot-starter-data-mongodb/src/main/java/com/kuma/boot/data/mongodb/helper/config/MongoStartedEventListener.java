package com.kuma.boot.data.mongodb.helper.config;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.mongodb.client.result.UpdateResult;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mongodb.helper.bean.IgnoreDocument;
import com.kuma.boot.data.mongodb.helper.bean.InitValue;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.boot.web.server.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.ClassUtils;

@AutoConfiguration
public class MongoStartedEventListener {
   @Autowired(
      required = false
   )
   private MongoTemplate mongoTemplate;
   @Autowired(
      required = false
   )
   private MongoMappingContext mongoMappingContext;

   public MongoStartedEventListener() {
   }

   @Async
   @Order(2147483646)
   @EventListener({WebServerInitializedEvent.class})
   public void afterStart(WebServerInitializedEvent event) {
      WebServerApplicationContext context = event.getApplicationContext();
      Environment environment = context.getEnvironment();
      Map<String, Object> beansWithAnnotation = ContextUtils.getApplicationContext().getBeansWithAnnotation(SpringBootApplication.class);
      LogUtils.info(beansWithAnnotation.toString(), new Object[0]);
      if (Objects.nonNull(this.mongoMappingContext) && Objects.nonNull(this.mongoTemplate)) {
         Object cla = beansWithAnnotation.values().stream().findFirst().get();

         for(Class<?> clazz : ClassUtil.scanPackage(ClassUtils.getPackageName(cla.getClass()))) {
            IgnoreDocument ignoreDocument = (IgnoreDocument)clazz.getAnnotation(IgnoreDocument.class);
            if (ignoreDocument == null) {
               Document document = (Document)clazz.getAnnotation(Document.class);
               if (document != null) {
                  if (!this.mongoTemplate.collectionExists(clazz)) {
                     this.mongoTemplate.createCollection(clazz);
                     LogUtils.info("\u521b\u5efa\u4e86" + clazz.getSimpleName() + "\u8868", new Object[0]);
                  }

                  IndexOperations indexOps = this.mongoTemplate.indexOps(clazz);
                  IndexResolver resolver = new MongoPersistentEntityIndexResolver(this.mongoMappingContext);
                  Iterable var10000 = resolver.resolveIndexFor(clazz);
                  Objects.requireNonNull(indexOps);
                  ((Iterable<org.springframework.data.mongodb.core.index.IndexDefinition>)var10000).forEach(indexOps::createIndex);
                  Field[] fields = ReflectUtil.getFields(clazz);

                  for(Field field : fields) {
                     if (field.isAnnotationPresent(InitValue.class)) {
                        InitValue initValue = (InitValue)field.getAnnotation(InitValue.class);
                        if (initValue.value() != null) {
                           Query query = new Query();
                           query.addCriteria(Criteria.where(field.getName()).is((Object)null));
                           long count = this.mongoTemplate.count(query, clazz);
                           if (count > 0L) {
                              Object value = null;
                              Class<?> type = field.getType();
                              if (type.equals(String.class)) {
                                 value = initValue.value();
                              }

                              if (type.equals(Short.class)) {
                                 value = Short.parseShort(initValue.value());
                              }

                              if (type.equals(Integer.class)) {
                                 value = Integer.parseInt(initValue.value());
                              }

                              if (type.equals(Long.class)) {
                                 value = Long.parseLong(initValue.value());
                              }

                              if (type.equals(Float.class)) {
                                 value = Float.parseFloat(initValue.value());
                              }

                              if (type.equals(Double.class)) {
                                 value = Double.parseDouble(initValue.value());
                              }

                              if (type.equals(Boolean.class)) {
                                 value = Boolean.parseBoolean(initValue.value());
                              }

                              Update update = (new Update()).set(field.getName(), value);
                              UpdateResult updateResult = this.mongoTemplate.updateMulti(query, update, clazz);
                              LogUtils.info(clazz.getSimpleName() + "\u8868\u66f4\u65b0\u4e86" + updateResult.getModifiedCount() + "\u6761\u9ed8\u8ba4\u503c", new Object[0]);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }
}
