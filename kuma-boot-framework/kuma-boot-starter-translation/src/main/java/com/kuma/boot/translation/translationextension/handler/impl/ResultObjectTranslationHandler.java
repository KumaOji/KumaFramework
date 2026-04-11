package com.kuma.boot.translation.translationextension.handler.impl;

import cn.hutool.core.util.ClassUtil;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.translation.translationextension.annotaion.TranslationResult;
import com.kuma.boot.translation.translationextension.handler.TranslationHandler;
import com.kuma.boot.translation.translationextension.service.FieldTranslationService;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ResultObjectTranslationHandler implements TranslationHandler {
   private final FieldTranslationService translationService;

   public ResultObjectTranslationHandler(FieldTranslationService translationService) {
      this.translationService = translationService;
   }

   public boolean adaptation(Type type) {
      if (type instanceof ParameterizedType parameterizedType) {
         Type rawType = parameterizedType.getRawType();
         if (rawType instanceof Class && ClassUtil.isAssignable((Class)rawType, Result.class)) {
            return true;
         }
      }

      return false;
   }

   public void translation(Object object, Type type, TranslationResult translationResult) {
      Result<?> resResult = (Result)object;
      Object data = resResult.getData();
      if (translationResult.convertType() == TranslationResult.ConvertType.OBJECT) {
         this.translationService.translation(data);
      } else {
         Map<String, Object> stringObjectMap = this.translationService.translationToMap(data);
         resResult.setData(stringObjectMap);
      }

   }
}
