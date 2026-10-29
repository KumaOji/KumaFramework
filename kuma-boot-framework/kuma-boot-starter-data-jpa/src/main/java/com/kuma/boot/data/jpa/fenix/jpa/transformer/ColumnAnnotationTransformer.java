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
            throw new FenixException(StringHelper.format("【Fenix 异常】将查询结果转换为【{}】对象时，第【{}】个查询结果列为空，请检查你是否开启了【nativeQuery = true】的原生 SQL 选项或者就要使用【as】“别名”的方式来显示声明查询结果列的名称！", super.resultClass.getName(), i));
         }

         super.setResultPropertyValue(beanWrapper, (String)fieldsMap.get(column.toLowerCase()), tuple[i]);
      }

      return beanWrapper.getWrappedInstance();
   }
}
