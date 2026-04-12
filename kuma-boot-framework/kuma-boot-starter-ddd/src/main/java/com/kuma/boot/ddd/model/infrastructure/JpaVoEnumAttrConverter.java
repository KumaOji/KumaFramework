package com.kuma.boot.ddd.model.infrastructure;

import com.kuma.boot.ddd.model.domain.ValueObjectEnum;
import com.kuma.boot.ddd.model.exception.SysException;
import jakarta.persistence.AttributeConverter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public abstract class JpaVoEnumAttrConverter implements AttributeConverter {
   protected Class voEnumClass;

   protected JpaVoEnumAttrConverter() {
      Type genType = this.getClass().getGenericSuperclass();
      if (!(genType instanceof ParameterizedType)) {
         throw new SysException("BaseJpaVoEnumAttrConverter init Exception - ", genType.getTypeName());
      } else {
         Type[] params = ((ParameterizedType)genType).getActualTypeArguments();
         this.voEnumClass = (Class)params[0];
      }
   }

   public Object convertToDatabaseColumn(ValueObjectEnum attribute) {
      return Optional.ofNullable(attribute).map(ValueObjectEnum::getValue).orElse((Object)null);
   }

   public ValueObjectEnum convertToEntityAttribute(Object dbData) {
      return ValueObjectEnum.of(dbData, this.voEnumClass);
   }
}
