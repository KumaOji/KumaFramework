package com.kuma.boot.translation.translationextension.cache;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.kuma.boot.translation.translationextension.annotaion.Translate;
import com.kuma.boot.translation.translationextension.annotaion.TranslationResult;
import com.kuma.boot.translation.translationextension.domain.ConvertInfo;
import com.kuma.boot.translation.translationextension.service.DictTranslationService;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class TranslationCacheService {
   private final DictTranslationService dictTranslationService;

   public TranslationCacheService(DictTranslationService dictTranslationService) {
      this.dictTranslationService = dictTranslationService;
   }

   public void initCache(Collection<?> objects) {
      objects.forEach(this::initCache0);
      this.cacheTranslation();
   }

   public void initCache(Object object) {
      this.initCache0(object);
      this.cacheTranslation();
   }

   private void initCache0(Object object) {
      if (!Objects.isNull(object)) {
         List<ConvertInfo> list = Arrays.stream(BeanUtil.getPropertyDescriptors(object.getClass())).map(this::initCache0).toList();

         for(ConvertInfo translationResult : list.stream().filter((o) -> Objects.nonNull(o.getTranslationResult())).toList()) {
            Object fieldValue = BeanUtil.getProperty(object, translationResult.getName());
            if (Objects.nonNull(fieldValue)) {
               if (fieldValue instanceof Collection) {
                  ((Collection)fieldValue).forEach(this::initCache0);
               }

               this.initCache0(fieldValue);
            }
         }

         for(ConvertInfo convertInfo : list) {
            Object fieldValue = BeanUtil.getProperty(object, convertInfo.getField().getName());
            Translate translate = convertInfo.getTranslate();
            if (!Objects.isNull(fieldValue) && !Objects.isNull(translate)) {
               if (fieldValue instanceof Collection) {
                  ((Collection)fieldValue).forEach((o) -> this.addCacheInfo(translate, o));
               } else {
                  this.addCacheInfo(translate, fieldValue);
               }
            }
         }

      }
   }

   private void addCacheInfo(Translate translate, Object fieldValue) {
      TranslationCacheLocal.Cache cache = TranslationCacheLocal.get();
      switch (translate.type()) {
         case DICT:
            cache.addDict(translate.dicCode(), fieldValue.toString());
         case TABLE:
         default:
      }
   }

   private ConvertInfo initCache0(PropertyDescriptor descriptor) {
      Field field = ReflectUtil.getField(descriptor.getReadMethod().getDeclaringClass(), descriptor.getName());
      Translate translate = (Translate)AnnotationUtil.getAnnotation(field, Translate.class);
      TranslationResult translationResult = (TranslationResult)AnnotationUtil.getAnnotation(field, TranslationResult.class);
      ConvertInfo convertInfo = new ConvertInfo();
      convertInfo.setName(descriptor.getName());
      convertInfo.setField(field);
      convertInfo.setTranslate(translate);
      convertInfo.setTranslationResult(translationResult);
      return convertInfo;
   }

   private void cacheTranslation() {
      this.dictTranslationService.initDictTranslationCache();
   }
}
