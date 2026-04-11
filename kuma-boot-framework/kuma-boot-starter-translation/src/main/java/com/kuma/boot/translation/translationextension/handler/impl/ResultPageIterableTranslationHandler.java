package com.kuma.boot.translation.translationextension.handler.impl;

import cn.hutool.core.util.ClassUtil;
import com.kuma.boot.translation.translationextension.annotaion.TranslationResult;
import com.kuma.boot.translation.translationextension.handler.TranslationHandler;
import com.kuma.boot.translation.translationextension.service.FieldTranslationService;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import org.springframework.stereotype.Component;

@Component
public class ResultPageIterableTranslationHandler implements TranslationHandler {
   private final FieldTranslationService translationService;

   public ResultPageIterableTranslationHandler(FieldTranslationService translationService) {
      this.translationService = translationService;
   }

   public boolean adaptation(Type type) {
      if (type instanceof ParameterizedType parameterizedType) {
         Type rawType = parameterizedType.getRawType();
         if (rawType instanceof ParameterizedType) {
            Type actualType = ((ParameterizedType)rawType).getActualTypeArguments()[0];
            if (actualType instanceof Class && ClassUtil.isAssignable((Class)actualType, Collection.class)) {
               return true;
            }
         }
      }

      return false;
   }

   public void translation(Object object, Type type, TranslationResult translationResult) {
   }
}
