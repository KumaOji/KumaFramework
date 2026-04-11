package com.kuma.boot.translation.translationextension.handler.impl;

import com.kuma.boot.translation.translationextension.annotaion.TranslationResult;
import com.kuma.boot.translation.translationextension.handler.TranslationHandler;
import com.kuma.boot.translation.translationextension.service.FieldTranslationService;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.springframework.stereotype.Component;

@Component
public class ResultPageTranslationHandler implements TranslationHandler {
   private final FieldTranslationService translationService;

   public ResultPageTranslationHandler(FieldTranslationService translationService) {
      this.translationService = translationService;
   }

   public boolean adaptation(Type type) {
      if (type instanceof ParameterizedType parameterizedType) {
         Type rawType = parameterizedType.getRawType();
         if (rawType instanceof ParameterizedType) {
            Type var4 = ((ParameterizedType)rawType).getActualTypeArguments()[0];
         }
      }

      return false;
   }

   public void translation(Object object, Type type, TranslationResult translationResult) {
   }
}
