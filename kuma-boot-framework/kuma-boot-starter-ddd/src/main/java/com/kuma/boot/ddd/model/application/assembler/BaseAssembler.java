//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.model.application.assembler;

import com.kuma.boot.ddd.model.domain.ValueObjectEnum;
import java.beans.PropertyDescriptor;
import java.util.Objects;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;

public class BaseAssembler {
   private BaseAssembler() {
   }

   public static <T, R> R convert(T source, Class<R> targetClass) {
      R target = null;

      try {
         target = (R)targetClass.newInstance();
      } catch (InstantiationException e) {
         throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e);
      }

      BeanUtils.copyProperties(source, target);
      copyVoEnumValue(source, target);
      return target;
   }

   public static <T, R> R copyVoEnumValue(T source, R target) {
      BeanWrapperImpl sourceWrapper = new BeanWrapperImpl(source);
      BeanWrapperImpl targetWrapper = new BeanWrapperImpl(target);
      PropertyDescriptor[] sourcePropertyDescriptors = sourceWrapper.getPropertyDescriptors();
      PropertyDescriptor[] targetPropertyDescriptors = targetWrapper.getPropertyDescriptors();

      for(PropertyDescriptor sourcePropDesc : sourcePropertyDescriptors) {
         PropertyDescriptor targetPropDesc = matchVoEnumTypeAndPropName(sourcePropDesc, targetPropertyDescriptors);
         if (!Objects.isNull(targetPropDesc)) {
            String sourcePropName = sourcePropDesc.getName();
            ValueObjectEnum sourcePropVoEnumVal = (ValueObjectEnum)sourceWrapper.getPropertyValue(sourcePropName);
            if (!Objects.isNull(sourcePropVoEnumVal)) {
               targetWrapper.setPropertyValue(sourcePropName, sourcePropVoEnumVal.getValue());
            }
         }
      }

      return target;
   }

   private static PropertyDescriptor matchVoEnumTypeAndPropName(PropertyDescriptor sourcePropDesc, PropertyDescriptor[] targetPropDescs) {
      boolean sourcePropIsVoEnum = ValueObjectEnum.class.isAssignableFrom(sourcePropDesc.getPropertyType());
      if (!sourcePropIsVoEnum) {
         return null;
      } else {
         String sourcePropName = sourcePropDesc.getName();

         for(PropertyDescriptor targetPropDesc : targetPropDescs) {
            if (targetPropDesc.getName().equals(sourcePropName) && !sourcePropDesc.getPropertyType().equals(targetPropDesc.getPropertyType())) {
               return targetPropDesc;
            }
         }

         return null;
      }
   }
}
