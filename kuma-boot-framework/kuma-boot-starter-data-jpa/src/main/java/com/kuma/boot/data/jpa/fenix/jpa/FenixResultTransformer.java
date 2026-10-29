package com.kuma.boot.data.jpa.fenix.jpa;

import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.convert.Jsr310Converters;

public class FenixResultTransformer extends AbstractResultTransformer {
   private static final long serialVersionUID = 4519223959994503529L;
   protected static final Map<String, Map<String, String>> classIgnoreCaseFieldsMap = new ConcurrentHashMap();
   private static final DefaultConversionService oldConversionService = new DefaultConversionService();

   public FenixResultTransformer() {
   }

   public void init() {
      Map<String, String> fieldsMap = (Map)classIgnoreCaseFieldsMap.get(this.resultClass.getName());
      if (fieldsMap == null) {
         PropertyDescriptor[] propDescriptors = BeanUtils.getPropertyDescriptors(this.resultClass);
         fieldsMap = new HashMap(propDescriptors.length);

         for(PropertyDescriptor propDescriptor : propDescriptors) {
            String propName = propDescriptor.getName();
            fieldsMap.put(propName.toLowerCase(), propName);
         }

         classIgnoreCaseFieldsMap.put(this.resultClass.getName(), fieldsMap);
      }

   }

   public Object transformTuple(Object[] tuple, String[] aliases) {
      BeanWrapper beanWrapper = super.newResultBeanWrapper();
      beanWrapper.setConversionService(oldConversionService);
      Map<String, String> fieldsMap = (Map)classIgnoreCaseFieldsMap.get(super.resultClass.getName());
      int i = 0;

      for(int len = aliases.length; i < len; ++i) {
         String column = aliases[i];
         if (StringHelper.isBlank(column)) {
            throw new FenixException("【Fenix 异常】要映射为【" + super.resultClass.getName() + "】实体的查询结果列为空，请检查并保证每一个查询结果列都必须用【as】后加“别名”的方式！");
         }

         super.setResultPropertyValue(beanWrapper, (String)fieldsMap.get(column.trim().toLowerCase()), tuple[i]);
      }

      return beanWrapper.getWrappedInstance();
   }

   static {
      for(Converter<?, ?> converter : Jsr310Converters.getConvertersToRegister()) {
         oldConversionService.addConverter(converter);
      }

      oldConversionService.addConverter(AbstractResultTransformer.ClobToStringConverter.INSTANCE);
      oldConversionService.addConverter(AbstractResultTransformer.BlobToStringConverter.INSTANCE);
      oldConversionService.addConverter(AbstractResultTransformer.LocalDateTimeToDateConverter.INSTANCE);
      oldConversionService.addConverter(AbstractResultTransformer.LocalDateToDateConverter.INSTANCE);
   }
}
