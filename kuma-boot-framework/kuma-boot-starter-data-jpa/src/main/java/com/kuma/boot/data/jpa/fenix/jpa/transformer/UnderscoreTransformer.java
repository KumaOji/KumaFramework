package com.kuma.boot.data.jpa.fenix.jpa.transformer;

import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import com.kuma.boot.data.jpa.fenix.jpa.AbstractResultTransformer;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;

public class UnderscoreTransformer extends AbstractResultTransformer {
   protected static final Map<String, Set<String>> classPropertiesMap = new ConcurrentHashMap();

   public UnderscoreTransformer() {
   }

   public void init() {
      Set<String> propertySet = (Set)classPropertiesMap.get(this.resultClass.getName());
      if (propertySet == null) {
         PropertyDescriptor[] propDescriptors = BeanUtils.getPropertyDescriptors(this.resultClass);
         propertySet = new HashSet(propDescriptors.length);

         for(PropertyDescriptor propDescriptor : propDescriptors) {
            propertySet.add(propDescriptor.getName());
         }

         classPropertiesMap.put(this.resultClass.getName(), propertySet);
      }

   }

   public Object transformTuple(Object[] tuple, String[] aliases) {
      BeanWrapper beanWrapper = super.newResultBeanWrapper();
      beanWrapper.setConversionService(defaultConversionService);
      Set<String> propertySet = (Set)classPropertiesMap.get(super.resultClass.getName());
      int i = 0;

      for(int len = aliases.length; i < len; ++i) {
         String column = aliases[i];
         if (StringHelper.isBlank(column)) {
            throw new FenixException(StringHelper.format("【Fenix 异常】将查询结果转换为【{}】对象时，第【{}】个查询结果列为空，请检查你是否开启了【nativeQuery = true】的原生 SQL 选项或者就要使用【as】“别名”的方式来显示声明查询结果列的名称！", super.resultClass.getName(), i));
         }

         String propertyName = this.toLowerCamelCase(column);
         if (propertySet.contains(propertyName)) {
            super.setResultPropertyValue(beanWrapper, propertyName, tuple[i]);
         }
      }

      return beanWrapper.getWrappedInstance();
   }

   protected String toLowerCamelCase(String name) {
      StringBuilder builder = new StringBuilder();
      boolean capitalize = false;
      int i = 0;

      for(int len = name.length(); i < len; ++i) {
         char c = name.charAt(i);
         if (c == '_') {
            capitalize = true;
         } else if (capitalize) {
            builder.append(Character.toUpperCase(c));
            capitalize = false;
         } else {
            builder.append(Character.toLowerCase(c));
         }
      }

      return builder.toString();
   }
}
