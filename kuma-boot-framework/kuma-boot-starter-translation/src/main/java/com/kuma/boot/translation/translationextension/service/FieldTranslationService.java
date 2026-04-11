package com.kuma.boot.translation.translationextension.service;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.translation.translationextension.annotaion.Translate;
import com.kuma.boot.translation.translationextension.annotaion.TranslationResult;
import com.kuma.boot.translation.translationextension.cache.TranslationCacheLocal;
import com.kuma.boot.translation.translationextension.cache.TranslationCacheService;
import com.kuma.boot.translation.translationextension.domain.ConvertInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class FieldTranslationService {
   private final TranslationCacheService translationCacheService;

   public FieldTranslationService(TranslationCacheService translationCacheService) {
      this.translationCacheService = translationCacheService;
   }

   public void translation(Collection<?> objects) {
      this.translationCacheService.initCache(objects);
      objects.forEach(this::translation0);
   }

   public void translation(Object object) {
      this.translationCacheService.initCache(object);
      this.translation0(object);
   }

   private void translation0(Object object) {
      if (!Objects.isNull(object)) {
         List<ConvertInfo> list = Arrays.stream(BeanUtil.getPropertyDescriptors(object.getClass())).map(this::initConvertInfo).toList();

         for(ConvertInfo translationResult : list.stream().filter((o) -> Objects.nonNull(o.getTranslationResult())).toList()) {
            Object fieldValue = BeanUtil.getProperty(object, translationResult.getName());
            if (Objects.nonNull(fieldValue)) {
               if (fieldValue instanceof Collection) {
                  ((Collection)fieldValue).forEach(this::translation0);
               }

               this.translation0(fieldValue);
            }
         }

         list.stream().filter((o) -> Objects.nonNull(o.getTranslate())).peek((convertInfo) -> this.isAndGetFieldType(convertInfo, object)).forEach((o) -> this.translation0(o, object));
      }
   }

   private Class<?> isAndGetFieldType(ConvertInfo convertInfo, Object convertObject) {
      Translate translate = convertInfo.getTranslate();
      if (StrUtil.isAllEmpty(new CharSequence[]{translate.source(), translate.target()})) {
         Object fieldValue = BeanUtil.getProperty(convertObject, convertInfo.getName());
         if (!Objects.isNull(fieldValue)) {
            Object value = this.getTranslationValue(translate, fieldValue);
            if (Objects.nonNull(value) && !ClassUtil.isAssignable(convertInfo.getField().getType(), value.getClass())) {
               return value.getClass();
            }
         }
      }

      return null;
   }

   private void translation0(ConvertInfo convertInfo, Object convertObject) {
      Translate translate = convertInfo.getTranslate();
      if (StrUtil.isAllEmpty(new CharSequence[]{translate.source(), translate.target()})) {
         Object fieldValue = BeanUtil.getProperty(convertObject, convertInfo.getName());
         if (!Objects.isNull(fieldValue)) {
            Object dictValue = this.getTranslationValue(translate, fieldValue);
            this.setFieldValue(convertObject, convertInfo.getName(), dictValue);
         }
      }

      if (StrUtil.isNotBlank(translate.source())) {
         Object fieldValue = BeanUtil.getProperty(convertObject, translate.source());
         if (!Objects.isNull(fieldValue)) {
            Object dictValue = this.getTranslationValue(translate, fieldValue);
            this.setFieldValue(convertObject, convertInfo.getName(), dictValue);
         }
      }

      if (StrUtil.isNotBlank(translate.target())) {
         Object fieldValue = BeanUtil.getProperty(convertObject, convertInfo.getName());
         if (!Objects.isNull(fieldValue)) {
            Object dictValue = this.getTranslationValue(translate, fieldValue);
            this.setFieldValue(convertObject, translate.target(), dictValue);
         }
      }

   }

   private ConvertInfo initConvertInfo(PropertyDescriptor descriptor) {
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

   public Collection<Map<String, Object>> translationToMap(Collection<?> objects) {
      this.translationCacheService.initCache(objects);
      return objects.stream().map((o) -> this.translationToMap0(o)).toList();
   }

   public Map<String, Object> translationToMap(Object object) {
      this.translationCacheService.initCache(object);
      return this.translationToMap0(object);
   }

   private Map<String, Object> translationToMap0(Object object) {
      if (Objects.isNull(object)) {
         return null;
      } else {
         this.translationCacheService.initCache(object);
         Map<String, Object> map = BeanUtil.beanToMap(object, new String[0]);
         Map<String, ConvertInfo> convertInfoMap = (Map)Arrays.stream(BeanUtil.getPropertyDescriptors(object.getClass())).map(this::initConvertInfo).collect(Collectors.toMap(ConvertInfo::getName, Function.identity(), (o1, o2) -> o2));
         convertInfoMap.values().stream().filter((o) -> Objects.nonNull(o.getTranslationResult())).forEach((o) -> {
            Object fieldValue = BeanUtil.getProperty(object, o.getName());
            if (Objects.nonNull(fieldValue)) {
               map.put(o.getName(), this.translationToMap0(fieldValue));
            }

         });
         convertInfoMap.values().stream().filter((o) -> Objects.nonNull(o.getTranslate())).forEach((o) -> this.translationToMap0(o, object, map));
         return map;
      }
   }

   private void translationToMap0(ConvertInfo convertInfo, Object convertObject, Map<String, Object> map) {
      Translate translate = convertInfo.getTranslate();
      if (StrUtil.isAllEmpty(new CharSequence[]{translate.source(), translate.target()})) {
         Object fieldValue = BeanUtil.getProperty(convertObject, convertInfo.getName());
         if (!Objects.isNull(fieldValue)) {
            Object dictValue = this.getTranslationValue(translate, fieldValue);
            map.put(convertInfo.getName(), dictValue);
         }
      }

      if (StrUtil.isNotBlank(translate.source())) {
         Object fieldValue = BeanUtil.getProperty(convertObject, translate.source());
         if (!Objects.isNull(fieldValue)) {
            Object dictValue = this.getTranslationValue(translate, fieldValue);
            map.put(convertInfo.getName(), dictValue);
         }
      }

      if (StrUtil.isNotBlank(translate.target())) {
         Object fieldValue = BeanUtil.getProperty(convertObject, convertInfo.getName());
         if (!Objects.isNull(fieldValue)) {
            Object dictValue = this.getTranslationValue(translate, fieldValue);
            map.put(translate.target(), dictValue);
         }
      }

   }

   private Object getTranslationValue(Translate translate, Object fieldValue) {
      if (fieldValue instanceof Collection) {
         return ((Collection)fieldValue).stream().map((o) -> this.getTranslationValue(translate, o)).collect(this.getCollectorType(fieldValue));
      } else {
         switch (translate.type()) {
            case DICT -> {
               return this.getDictValue(translate, fieldValue.toString());
            }
            case TABLE -> {
               return this.getTableValue(translate, fieldValue);
            }
            default -> {
               return null;
            }
         }
      }
   }

   private String getDictValue(Translate translate, String fieldValue) {
      TranslationCacheLocal.Cache cache = TranslationCacheLocal.get();
      String dicCode = translate.dicCode();
      return cache.getDictValue(dicCode, fieldValue);
   }

   private Object getTableValue(Translate translate, Object fieldValue) {
      TranslationCacheLocal.Cache cache = TranslationCacheLocal.get();
      return "cache.getTableValue()";
   }

   private void setFieldValue(Object bean, String fieldNameOrIndex, Object value) {
      try {
         BeanUtil.setProperty(bean, fieldNameOrIndex, value);
      } catch (Exception var5) {
         LogUtils.warn("\u7c7b {} \u7684 \u5b57\u6bb5\u540d\u79f0\u6216\u4e0b\u6807: {}\uff0c\u8d4b\u503c\u9519\u8bef\uff0c\u53ef\u80fd\u662f\u503c\u7c7b\u578b\u4e0e\u5b57\u6bb5\u7c7b\u578b\u4e0d\u5339\u914d", new Object[]{bean.getClass().getName(), fieldNameOrIndex});
      }

   }

   private Collector<Object, ?, ?> getCollectorType(Object fieldValue) {
      return fieldValue instanceof Set ? Collectors.toSet() : Collectors.toList();
   }
}
