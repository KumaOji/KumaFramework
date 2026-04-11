package com.kuma.boot.sensitive.sensitivemvc.util;

import cn.hutool.core.annotation.AnnotationUtil;
import java.lang.annotation.Annotation;
import java.util.Objects;
import org.springframework.web.method.HandlerMethod;

public class AnnotationUtils {
   public AnnotationUtils() {
   }

   public static <T extends Annotation> T getAnnotation(HandlerMethod handlerMethod, Class<T> annotationType) {
      Class<?> beanType = handlerMethod.getBeanType();
      T annotation = AnnotationUtil.getAnnotation(beanType, annotationType);
      if (Objects.isNull(annotation)) {
         annotation = handlerMethod.getMethodAnnotation(annotationType);
      }

      return annotation;
   }
}
