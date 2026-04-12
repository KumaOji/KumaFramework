package com.kuma.boot.sensitive.sensitivelog.core.api;

import com.alibaba.fastjson2.JSON;
import com.kuma.boot.common.support.deepcopy.DeepCopy;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.reflect.ClassTypeUtils;
import com.kuma.boot.common.utils.reflect.ClassUtils;
import com.kuma.boot.sensitive.sensitivelog.annotation.metadata.SensitiveCondition;
import com.kuma.boot.sensitive.sensitivelog.annotation.metadata.SensitiveStrategy;
import com.kuma.boot.sensitive.sensitivelog.api.Condition;
import com.kuma.boot.sensitive.sensitivelog.api.Sensitive;
import com.kuma.boot.sensitive.sensitivelog.api.SensitiveConfig;
import com.kuma.boot.sensitive.sensitivelog.api.Strategy;
import com.kuma.boot.sensitive.sensitivelog.api.impl.SensitiveStrategyBuiltIn;
import com.kuma.boot.sensitive.sensitivelog.core.api.context.SensitiveContext;
import com.kuma.boot.sensitive.sensitivelog.core.exception.SensitiveRuntimeException;
import com.kuma.boot.sensitive.sensitivelog.core.util.entry.SensitiveEntryUtil;
import com.kuma.boot.sensitive.sensitivelog.core.util.strategy.SensitiveStrategyBuiltInUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SensitiveService<T> implements Sensitive<T> {
   public SensitiveService() {
   }

   public T desCopy(T object, final SensitiveConfig config) {
      Class clazz = object.getClass();
      SensitiveContext context = new SensitiveContext();
      DeepCopy deepCopy = config.deepCopy();
      T copyObject = (T)deepCopy.deepCopy(object);
      this.handleClassField(context, copyObject, clazz);
      return copyObject;
   }

   public String desJson(final T object, final SensitiveConfig config) {
      if (ObjectUtils.isNull(object)) {
         return JSON.toJSONString(object);
      } else {
         new SensitiveContext();
         return JSON.toJSONString(object);
      }
   }

   private void handleClassField(final SensitiveContext context, final Object copyObject, final Class clazz) {
      Field[] fieldList = clazz.getFields();
      context.setAllFieldList(List.of(fieldList));
      context.setCurrentObject(copyObject);

      try {
         for(Field field : fieldList) {
            Class fieldTypeClass = field.getType();
            context.setCurrentField(field);
            if (!SensitiveEntryUtil.hasSensitiveEntry(field)) {
               this.handleSensitive(context, copyObject, field);
            } else if (ClassTypeUtils.isJavaBean(fieldTypeClass)) {
               Object fieldNewObject = field.get(copyObject);
               this.handleClassField(context, fieldNewObject, fieldTypeClass);
            } else if (ClassTypeUtils.isArray(fieldTypeClass)) {
               Object[] arrays = (Object[]) field.get(copyObject);
               if (ArrayUtils.isNotEmpty(arrays)) {
                  Object firstArrayEntry = arrays[0];
                  Class entryFieldClass = firstArrayEntry.getClass();
                  if (this.needHandleEntryType(entryFieldClass)) {
                     for(Object arrayEntry : arrays) {
                        this.handleClassField(context, arrayEntry, entryFieldClass);
                     }
                  } else {
                     int arrayLength = arrays.length;
                     Object newArray = Array.newInstance(entryFieldClass, arrayLength);

                     for(int i = 0; i < arrayLength; ++i) {
                        Object entry = arrays[i];
                        Object result = this.handleSensitiveEntry(context, entry, field);
                        Array.set(newArray, i, result);
                     }

                     field.set(copyObject, newArray);
                  }
               }
            } else if (!ClassTypeUtils.isCollection(fieldTypeClass)) {
               this.handleSensitive(context, copyObject, field);
            } else {
               Collection<Object> entryCollection = (Collection)field.get(copyObject);
               if (CollectionUtils.isNotEmpty(entryCollection)) {
                  Object firstCollectionEntry = entryCollection.iterator().next();
                  Class collectionEntryClass = firstCollectionEntry.getClass();
                  if (this.needHandleEntryType(collectionEntryClass)) {
                     for(Object collectionEntry : entryCollection) {
                        this.handleClassField(context, collectionEntry, collectionEntryClass);
                     }
                  } else {
                     List<Object> newResultList = new ArrayList(entryCollection.size());

                     for(Object entry : entryCollection) {
                        Object result = this.handleSensitiveEntry(context, entry, field);
                        newResultList.add(result);
                     }

                     field.set(copyObject, newResultList);
                  }
               }
            }
         }

      } catch (IllegalAccessException e) {
         throw new SensitiveRuntimeException(e);
      }
   }

   private Object handleSensitiveEntry(final SensitiveContext context, final Object entry, final Field field) {
      try {
         com.kuma.boot.sensitive.sensitivelog.annotation.Sensitive sensitive = (com.kuma.boot.sensitive.sensitivelog.annotation.Sensitive)field.getAnnotation(com.kuma.boot.sensitive.sensitivelog.annotation.Sensitive.class);
         if (ObjectUtils.isNotNull(sensitive)) {
            Class<? extends Condition> conditionClass = sensitive.condition();
            Condition condition = (Condition)conditionClass.getDeclaredConstructor().newInstance();
            if (condition.valid(context)) {
               Class<? extends Strategy> strategyClass = sensitive.strategy();
               Strategy strategy = (Strategy)strategyClass.getDeclaredConstructor().newInstance();
               return strategy.des(entry, context);
            }
         }

         Annotation[] annotations = field.getAnnotations();
         if (ArrayUtils.isNotEmpty(annotations)) {
            Condition condition = this.getCondition(annotations);
            if (ObjectUtils.isNull(condition) || condition.valid(context)) {
               Strategy strategy = this.getStrategy(annotations);
               if (ObjectUtils.isNotNull(strategy)) {
                  return strategy.des(entry, context);
               }
            }
         }

         return entry;
      } catch (IllegalAccessException | InstantiationException e) {
         throw new SensitiveRuntimeException(e);
      } catch (NoSuchMethodException | InvocationTargetException e) {
         LogUtils.error(e);
         throw new SensitiveRuntimeException("");
      }
   }

   private void handleSensitive(final SensitiveContext context, final Object copyObject, final Field field) {
      try {
         com.kuma.boot.sensitive.sensitivelog.annotation.Sensitive sensitive = (com.kuma.boot.sensitive.sensitivelog.annotation.Sensitive)field.getAnnotation(com.kuma.boot.sensitive.sensitivelog.annotation.Sensitive.class);
         if (sensitive != null) {
            Class<? extends Condition> conditionClass = sensitive.condition();
            Condition condition = (Condition)conditionClass.getDeclaredConstructor().newInstance();
            if (condition.valid(context)) {
               Class<? extends Strategy> strategyClass = sensitive.strategy();
               Strategy strategy = (Strategy)strategyClass.getDeclaredConstructor().newInstance();
               Object originalFieldVal = field.get(copyObject);
               Object result = strategy.des(originalFieldVal, context);
               field.set(copyObject, result);
            }
         }

         Annotation[] annotations = field.getAnnotations();
         if (ArrayUtils.isNotEmpty(annotations)) {
            Condition condition = this.getCondition(annotations);
            if (ObjectUtils.isNull(condition) || condition.valid(context)) {
               Strategy strategy = this.getStrategy(annotations);
               if (ObjectUtils.isNotNull(strategy)) {
                  Object originalFieldVal = field.get(copyObject);
                  Object result = strategy.des(originalFieldVal, context);
                  field.set(copyObject, result);
               }
            }
         }
      } catch (IllegalAccessException | InstantiationException e) {
         throw new SensitiveRuntimeException(e);
      } catch (InvocationTargetException e) {
         LogUtils.error(e);
      } catch (NoSuchMethodException e) {
         LogUtils.error(e);
      }

   }

   private Strategy getStrategy(final Annotation[] annotations) {
      for(Annotation annotation : annotations) {
         SensitiveStrategy sensitiveStrategy = (SensitiveStrategy)annotation.annotationType().getAnnotation(SensitiveStrategy.class);
         if (ObjectUtils.isNotNull(sensitiveStrategy)) {
            Class<? extends Strategy> clazz = sensitiveStrategy.value();
            if (SensitiveStrategyBuiltIn.class.equals(clazz)) {
               return SensitiveStrategyBuiltInUtil.require(annotation.annotationType());
            }

            return (Strategy)ClassUtils.newInstance(clazz);
         }
      }

      return null;
   }

   private Condition getCondition(final Annotation[] annotations) {
      for(Annotation annotation : annotations) {
         SensitiveCondition sensitiveCondition = (SensitiveCondition)annotation.annotationType().getAnnotation(SensitiveCondition.class);
         if (ObjectUtils.isNotNull(sensitiveCondition)) {
            Class<? extends Condition> customClass = sensitiveCondition.value();
            return (Condition)ClassUtils.newInstance(customClass);
         }
      }

      return null;
   }

   private boolean needHandleEntryType(final Class fieldTypeClass) {
      if (!ClassTypeUtils.isBase(fieldTypeClass) && !ClassTypeUtils.isMap(fieldTypeClass)) {
         return ClassTypeUtils.isJavaBean(fieldTypeClass) || ClassTypeUtils.isArray(fieldTypeClass) || ClassTypeUtils.isCollection(fieldTypeClass);
      } else {
         return false;
      }
   }
}
