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
            throw new FenixException("\u3010Fenix \u5f02\u5e38\u3011\u8981\u6620\u5c04\u4e3a\u3010" + super.resultClass.getName() + "\u3011\u5b9e\u4f53\u7684\u67e5\u8be2\u7ed3\u679c\u5217\u4e3a\u7a7a\uff0c\u8bf7\u68c0\u67e5\u5e76\u4fdd\u8bc1\u6bcf\u4e00\u4e2a\u67e5\u8be2\u7ed3\u679c\u5217\u90fd\u5fc5\u987b\u7528\u3010as\u3011\u540e\u52a0\u201c\u522b\u540d\u201d\u7684\u65b9\u5f0f\uff01");
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
