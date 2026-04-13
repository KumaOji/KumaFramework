package com.kuma.boot.data.jpa.fenix.jpa.transformer;

import com.kuma.boot.data.jpa.fenix.exception.FenixException;
import com.kuma.boot.data.jpa.fenix.helper.FieldHelper;
import com.kuma.boot.data.jpa.fenix.helper.StringHelper;
import com.kuma.boot.data.jpa.fenix.jpa.AbstractResultTransformer;
import jakarta.persistence.Column;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeanWrapper;

public class ColumnAnnotationTransformer extends AbstractResultTransformer {
   protected static final Map<String, Map<String, String>> classFieldsMap = new ConcurrentHashMap();

   public ColumnAnnotationTransformer() {
   }

   public void init() {
      Map<String, String> fieldsMap = (Map)classFieldsMap.get(this.resultClass.getName());
      if (fieldsMap == null) {
         List<Field> fields = FieldHelper.getAllFieldsList(this.resultClass);
         fieldsMap = new HashMap(fields.size());

         for(Field field : fields) {
            Column columnAnnotation = (Column)field.getAnnotation(Column.class);
            if (columnAnnotation != null) {
               fieldsMap.put(columnAnnotation.name().toLowerCase(), field.getName());
            }
         }

         classFieldsMap.put(this.resultClass.getName(), fieldsMap);
      }

   }

   public Object transformTuple(Object[] tuple, String[] aliases) {
      BeanWrapper beanWrapper = super.newResultBeanWrapper();
      beanWrapper.setConversionService(defaultConversionService);
      Map<String, String> fieldsMap = (Map)classFieldsMap.get(super.resultClass.getName());
      int i = 0;

      for(int len = aliases.length; i < len; ++i) {
         String column = aliases[i];
         if (StringHelper.isBlank(column)) {
            throw new FenixException(StringHelper.format("\u3010Fenix \u5f02\u5e38\u3011\u5c06\u67e5\u8be2\u7ed3\u679c\u8f6c\u6362\u4e3a\u3010{}\u3011\u5bf9\u8c61\u65f6\uff0c\u7b2c\u3010{}\u3011\u4e2a\u67e5\u8be2\u7ed3\u679c\u5217\u4e3a\u7a7a\uff0c\u8bf7\u68c0\u67e5\u4f60\u662f\u5426\u5f00\u542f\u4e86\u3010nativeQuery = true\u3011\u7684\u539f\u751f SQL \u9009\u9879\u6216\u8005\u5c31\u8981\u4f7f\u7528\u3010as\u3011\u201c\u522b\u540d\u201d\u7684\u65b9\u5f0f\u6765\u663e\u793a\u58f0\u660e\u67e5\u8be2\u7ed3\u679c\u5217\u7684\u540d\u79f0\uff01", super.resultClass.getName(), i));
         }

         super.setResultPropertyValue(beanWrapper, (String)fieldsMap.get(column.toLowerCase()), tuple[i]);
      }

      return beanWrapper.getWrappedInstance();
   }
}
