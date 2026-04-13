package com.kuma.boot.data.jpa.fenix.jpa;

import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import java.lang.reflect.InvocationTargetException;
import java.sql.Blob;
import java.sql.Clob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.descriptor.java.BlobJavaType;
import org.hibernate.type.descriptor.java.DataHelper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

public abstract class AbstractResultTransformer implements ResultTransformer {
   protected static final DefaultConversionService defaultConversionService = new DefaultConversionService();
   protected Class<?> resultClass;

   public AbstractResultTransformer() {
   }

   public void setResultClass(Class<?> resultClass) {
      this.resultClass = resultClass;
   }

   public void init() {
   }

   protected BeanWrapper newResultBeanWrapper() {
      Object resultObject;
      try {
         resultObject = this.resultClass.getDeclaredConstructor().newInstance();
      } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
         throw new FenixException(e, "\u3010Fenix \u5f02\u5e38\u3011\u5b9e\u4f8b\u5316\u3010{}\u3011\u7c7b\u51fa\u9519\uff0c\u8bf7\u68c0\u67e5\u8be5\u7c7b\u662f\u5426\u5305\u542b\u53ef\u516c\u5f00\u8bbf\u95ee\u7684\u65e0\u53c2\u6784\u9020\u65b9\u6cd5\uff01", new Object[]{this.resultClass.getName()});
      }

      return PropertyAccessorFactory.forBeanPropertyAccess(resultObject);
   }

   protected void setResultPropertyValue(BeanWrapper beanWrapper, String propertyName, Object value) {
      if (StringHelper.isNotBlank(propertyName)) {
         try {
            beanWrapper.setPropertyValue(propertyName, value);
         } catch (TypeMismatchException | NotWritablePropertyException e) {
            throw new FenixException(e, "\u3010Fenix \u5f02\u5e38\u3011\u8bbe\u7f6e\u7ed3\u679c\u7c7b\u3010{}\u3011\u7684\u3010{}\u3011\u5c5e\u6027\u503c\u4e3a\u3010{}\u3011\u65f6\u5f02\u5e38\uff0c\u8bf7\u68c0\u67e5\u8be5\u5c5e\u6027\u662f\u5426\u5b58\u5728\u6216\u8005\u662f\u5426\u6709 public \u578b\u7684 Setter \u65b9\u6cd5\uff0c\u6216\u8005\u68c0\u67e5\u5b57\u6bb5\u7c7b\u578b\u662f\u5426\u652f\u6301 JPA \u7684\u9ed8\u8ba4\u8f6c\u6362\uff01", new Object[]{beanWrapper.getWrappedClass().getName(), propertyName, value});
         }
      }

   }

   public List<?> transformList(List list) {
      return list;
   }

   static {
      defaultConversionService.addConverter(AbstractResultTransformer.ClobToStringConverter.INSTANCE);
      defaultConversionService.addConverter(AbstractResultTransformer.BlobToStringConverter.INSTANCE);
      defaultConversionService.addConverter(AbstractResultTransformer.LocalDateTimeToDateConverter.INSTANCE);
      defaultConversionService.addConverter(AbstractResultTransformer.LocalDateToDateConverter.INSTANCE);
   }

   protected static enum ClobToStringConverter implements Converter<Clob, String> {
      INSTANCE;

      private ClobToStringConverter() {
      }

      public String convert(Clob source) {
         return DataHelper.extractString(source);
      }

      // $FF: synthetic method
      private static ClobToStringConverter[] $values() {
         return new ClobToStringConverter[]{INSTANCE};
      }
   }

   protected static enum BlobToStringConverter implements Converter<Blob, String> {
      INSTANCE;

      private BlobToStringConverter() {
      }

      public String convert(Blob source) {
         return BlobJavaType.INSTANCE.toString(source);
      }

      // $FF: synthetic method
      private static BlobToStringConverter[] $values() {
         return new BlobToStringConverter[]{INSTANCE};
      }
   }

   protected static enum LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {
      INSTANCE;

      private LocalDateTimeToDateConverter() {
      }

      public Date convert(LocalDateTime source) {
         return Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
      }

      // $FF: synthetic method
      private static LocalDateTimeToDateConverter[] $values() {
         return new LocalDateTimeToDateConverter[]{INSTANCE};
      }
   }

   protected static enum LocalDateToDateConverter implements Converter<LocalDate, Date> {
      INSTANCE;

      private LocalDateToDateConverter() {
      }

      public Date convert(LocalDate source) {
         return Date.from(source.atStartOfDay(ZoneId.systemDefault()).toInstant());
      }

      // $FF: synthetic method
      private static LocalDateToDateConverter[] $values() {
         return new LocalDateToDateConverter[]{INSTANCE};
      }
   }
}
