package com.kuma.boot.encrypt.encrypt2.advice;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.encrypt2.handler.SecurityHandler;
import com.kuma.boot.encrypt.encrypt2.handler.SensitiveHandler;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;

public abstract class AbstractSecurityAdvice implements BeanFactoryAware, InitializingBean {
   public int DEFAULT_CLEAN_DEPTH;
   @SuppressWarnings("rawtypes")
   public List STANDARD_CLASS = new ArrayList();
   protected ConfigurableListableBeanFactory beanFactory;
   protected SecurityHandler securityHandler;
   protected SensitiveHandler sensitiveHandler;

   public abstract String handleSecurity(String value, Annotation[] annotations);

   protected Object handleObject(int currentTime, int maxCleanDepth, Object result, Annotation[] annotations) throws Exception {
      if (Objects.isNull(result)) {
         return null;
      } else if (currentTime >= maxCleanDepth) {
         LogUtils.warn("currentTime：{}，maxCleanDepth：{}", new Object[]{currentTime, maxCleanDepth});
         return result;
      } else {
         int nextDepth = currentTime + 1;
         Class<?> resultClass = result.getClass();
         if (String.class.isAssignableFrom(resultClass)) {
            return this.handleSecurity((String)result, annotations);
         } else {
            if (resultClass.isArray()) {
               this.wrapperNewObjArray((Object[]) result, nextDepth, annotations);
            } else {
               if (List.class.isAssignableFrom(resultClass)) {
                  this.wrapperNewObjList((List)result, nextDepth, annotations);
                  return result;
               }

               if (Set.class.isAssignableFrom(resultClass)) {
                  this.wrapperNewObjSet((Set)result, nextDepth, annotations);
                  return result;
               }

               if (Map.class.isAssignableFrom(resultClass)) {
                  this.wrapperNewObjMap((Map)result, nextDepth, annotations);
                  return result;
               }

               if (this.isStandardClass(result)) {
                  Field[] declaredFields = this.findAllDeclaredFields(resultClass);

                  for(Field field : declaredFields) {
                     field.setAccessible(true);
                     Object value = field.get(result);
                     if (value != null) {
                        annotations = field.getDeclaredAnnotations();
                        Class<?> clazz = value.getClass();
                        if (String.class.isAssignableFrom(clazz)) {
                           field.set(result, this.handleSecurity((String)value, annotations));
                        } else if (clazz.isArray()) {
                           this.wrapperNewObjArray((Object[]) value, nextDepth, annotations);
                        } else if (List.class.isAssignableFrom(clazz)) {
                           this.wrapperNewObjList((List)value, nextDepth, annotations);
                        } else if (Set.class.isAssignableFrom(clazz)) {
                           this.wrapperNewObjSet((Set)value, nextDepth, annotations);
                        } else if (Map.class.isAssignableFrom(clazz)) {
                           this.wrapperNewObjMap((Map)value, nextDepth, annotations);
                        } else {
                           field.set(result, this.handleObject(nextDepth, maxCleanDepth, value, annotations));
                        }
                     }
                  }
               }
            }

            return result;
         }
      }
   }

   private Field[] findAllDeclaredFields(Class resultClass) {
      Set<Field> fields = new HashSet();
      Class<?> currentClass = resultClass;

      do {
         fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
         currentClass = currentClass.getSuperclass();
      } while(currentClass != null);

      return (Field[])fields.toArray(new Field[0]);
   }

   private void wrapperNewObjArray(Object[] valueList, int nextDepth, Annotation[] annotations) throws Exception {
      for(int i = 0; i < valueList.length; ++i) {
         try {
            Object value = valueList[i];
            valueList[i] = this.handleObject(nextDepth, this.DEFAULT_CLEAN_DEPTH, value, annotations);
         } catch (UnsupportedOperationException var6) {
            LogUtils.error("value:{} class:{} is unModify!", new Object[]{Arrays.toString(valueList), valueList.getClass().getSimpleName()});
            return;
         }
      }

   }

   private void wrapperNewObjList(List valueList, int nextDepth, Annotation[] annotations) throws Exception {
      for(int i = 0; i < valueList.size(); ++i) {
         try {
            Object value = valueList.get(i);
            valueList.set(i, this.handleObject(nextDepth, this.DEFAULT_CLEAN_DEPTH, value, annotations));
         } catch (UnsupportedOperationException var6) {
            LogUtils.error("value:{} class:{} is unModify!", new Object[]{valueList, valueList.getClass().getSimpleName()});
            return;
         }
      }

   }

   private void wrapperNewObjMap(Map objectMap, int nextDepth, Annotation[] annotations) throws Exception {
      for(Object key : objectMap.keySet()) {
         try {
            objectMap.put(key, this.handleObject(nextDepth, this.DEFAULT_CLEAN_DEPTH, objectMap.get(key), annotations));
         } catch (UnsupportedOperationException var7) {
            LogUtils.error("value:{} class:{} is unModify!", new Object[]{objectMap, objectMap.getClass().getSimpleName()});
         }
      }

   }

   private void wrapperNewObjSet(Set objectSet, int nextDepth, Annotation[] annotations) throws Exception {
      List<Object> objectList = new LinkedList();

      for(Object obj : objectSet) {
         objectList.add(this.handleObject(nextDepth, this.DEFAULT_CLEAN_DEPTH, obj, annotations));
      }

      try {
         objectSet.clear();
      } catch (UnsupportedOperationException var7) {
         LogUtils.error("value:{} class:{} is unModify!", new Object[]{objectSet, objectSet.getClass().getSimpleName()});
      }

      objectSet.addAll(objectList);
   }

   @SuppressWarnings("unchecked")
   boolean isStandardClass(Object result) {
      Class<?> clazz;
      if (result instanceof Class<?> c) {
         clazz = c;
      } else {
         clazz = result.getClass();
      }

      for(String standardClass : (List<String>) this.STANDARD_CLASS) {
         if (clazz.getName().startsWith(standardClass) || clazz.getName().matches(standardClass)) {
            return true;
         }
      }

      return false;
   }

   public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
      if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
         throw new IllegalArgumentException("AdvisorAutoProxyCreator requires a ConfigurableListableBeanFactory: " + String.valueOf(beanFactory));
      } else {
         this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
      }
   }

   private static void sortProcessors(List list, ConfigurableListableBeanFactory beanFactory) {
      Comparator<Object> comparatorToUse = null;
      if (beanFactory instanceof DefaultListableBeanFactory) {
         comparatorToUse = ((DefaultListableBeanFactory)beanFactory).getDependencyComparator();
      }

      if (comparatorToUse == null) {
         comparatorToUse = OrderComparator.INSTANCE;
      }

      list.sort(comparatorToUse);
   }

   public void afterPropertiesSet() throws Exception {
      List<SecurityHandler> securityHandlers = this.getBeanByType(SecurityHandler.class);
      List<SensitiveHandler> sensitiveHandlers = this.getBeanByType(SensitiveHandler.class);
      this.securityHandler = (SecurityHandler)securityHandlers.get(0);
      this.sensitiveHandler = (SensitiveHandler)sensitiveHandlers.get(0);
   }

   @SuppressWarnings("unchecked")
   private <T> List<T> getBeanByType(Class<T> tClass) {
      List<T> list = new ArrayList<>();
      String[] beanNames = this.beanFactory.getBeanNamesForType(tClass, true, false);
      List<String> orderedNames = new ArrayList<>();
      List<String> nonOrderedNames = new ArrayList<>();

      for(String beanName : beanNames) {
         if (this.beanFactory.isTypeMatch(beanName, Ordered.class)) {
            orderedNames.add(beanName);
         } else {
            nonOrderedNames.add(beanName);
         }
      }

      List<T> orderedBeans = new ArrayList<>(orderedNames.size());

      for(String oName : orderedNames) {
         T t = this.beanFactory.getBean(oName, tClass);
         orderedBeans.add(t);
      }

      sortProcessors(orderedBeans, this.beanFactory);
      List<T> nonOrderedBeans = new ArrayList<>(nonOrderedNames.size());

      for(String nName : nonOrderedNames) {
         T t = this.beanFactory.getBean(nName, tClass);
         nonOrderedBeans.add(t);
      }

      list.addAll(orderedBeans);
      list.addAll(nonOrderedBeans);
      return list;
   }
}
