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
            throw new FenixException(StringHelper.format("\u3010Fenix \u5f02\u5e38\u3011\u5c06\u67e5\u8be2\u7ed3\u679c\u8f6c\u6362\u4e3a\u3010{}\u3011\u5bf9\u8c61\u65f6\uff0c\u7b2c\u3010{}\u3011\u4e2a\u67e5\u8be2\u7ed3\u679c\u5217\u4e3a\u7a7a\uff0c\u8bf7\u68c0\u67e5\u4f60\u662f\u5426\u5f00\u542f\u4e86\u3010nativeQuery = true\u3011\u7684\u539f\u751f SQL \u9009\u9879\u6216\u8005\u5c31\u8981\u4f7f\u7528\u3010as\u3011\u201c\u522b\u540d\u201d\u7684\u65b9\u5f0f\u6765\u663e\u793a\u58f0\u660e\u67e5\u8be2\u7ed3\u679c\u5217\u7684\u540d\u79f0\uff01", super.resultClass.getName(), i));
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
