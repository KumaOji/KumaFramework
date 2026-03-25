package com.kuma.boot.encrypt.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.annotation.SeparateEncrypt;
import jakarta.servlet.FilterConfig;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class InitHandler {
   public static void handler(FilterConfig filterConfig, Set encryptCacheUri, AtomicBoolean isEncryptAnnotation) {
      WebApplicationContext servletContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
      Map<String, Object> restControllers = new HashMap();
      Map<String, Object> controllers = new HashMap();

      try {
         controllers = servletContext.getBeansWithAnnotation(Controller.class);
      } catch (BeanCreationException e) {
         LogUtils.error(e.getMessage(), new Object[0]);
      }

      try {
         restControllers = servletContext.getBeansWithAnnotation(RestController.class);
      } catch (BeanCreationException e) {
         LogUtils.error(e.getMessage(), new Object[0]);
      }

      if (restControllers.size() > 0) {
         List<Object> types = restControllers.values().stream().filter((v) -> AnnotationUtils.findAnnotation(v.getClass(), SeparateEncrypt.class) != null).toList();
         List<Object> notTypes = restControllers.values().stream().filter((v) -> AnnotationUtils.findAnnotation(v.getClass(), SeparateEncrypt.class) == null).toList();
         restcontrollerTypesHandler(types, encryptCacheUri);
         restcontrollerNotTypesHandler(notTypes, encryptCacheUri);
      }

      if (controllers.size() > 0) {
         List<Object> types = controllers.values().stream().filter((v) -> AnnotationUtils.findAnnotation(v.getClass(), SeparateEncrypt.class) != null).toList();
         List<Object> notTypes = controllers.values().stream().filter((v) -> AnnotationUtils.findAnnotation(v.getClass(), SeparateEncrypt.class) == null).toList();
         controllerTypesHandler(types, encryptCacheUri);
         controllerNotTypesHandler(notTypes, encryptCacheUri);
      }

      if (encryptCacheUri.size() > 0) {
         isEncryptAnnotation.set(true);
      }

   }

   private static void controllerNotTypesHandler(List types, Set cacheUrl) {
      if (types.size() > 0) {
         types.forEach((t) -> {
            Class<?> aClass = t.getClass();
            Method[] declaredMethods = aClass.getDeclaredMethods();
            String[] finalTypeUrl = typeUrl(aClass);
            List<Method> methods = Arrays.stream(declaredMethods).filter((d) -> AnnotationUtils.findAnnotation(d, SeparateEncrypt.class) != null).toList();
            if (methods.size() != 0) {
               methodHandler(methods, finalTypeUrl, cacheUrl);
            }
         });
      }

   }

   private static void controllerTypesHandler(List types, Set cacheUrl) {
      if (types.size() > 0) {
         types.forEach((t) -> {
            Class<?> aClass = t.getClass();
            Method[] declaredMethods = aClass.getDeclaredMethods();
            String[] finalTypeUrl = typeUrl(aClass);
            if (declaredMethods.length != 0) {
               List<Method> methods = Arrays.asList(declaredMethods);
               methodHandler(methods, finalTypeUrl, cacheUrl);
            }
         });
      }

   }

   private static void restcontrollerNotTypesHandler(List types, Set cacheUrl) {
      if (types.size() > 0) {
         types.forEach((t) -> {
            Class<?> aClass = t.getClass();
            Method[] declaredMethods = aClass.getDeclaredMethods();
            String[] finalTypeUrl = typeUrl(aClass);
            List<Method> methods = Arrays.stream(declaredMethods).filter((d) -> AnnotationUtils.findAnnotation(d, SeparateEncrypt.class) != null).toList();
            if (methods.size() != 0) {
               restMethodHandler(methods, finalTypeUrl, cacheUrl);
            }
         });
      }

   }

   private static void restcontrollerTypesHandler(List types, Set cacheUrl) {
      if (types.size() > 0) {
         types.forEach((t) -> {
            Class<?> aClass = t.getClass();
            Method[] declaredMethods = aClass.getDeclaredMethods();
            String[] finalTypeUrl = typeUrl(aClass);
            if (declaredMethods.length != 0) {
               List<Method> methods = Arrays.asList(declaredMethods);
               restMethodHandler(methods, finalTypeUrl, cacheUrl);
            }
         });
      }

   }

   private static String[] typeUrl(Class aClass) {
      String[] typeUrl = null;
      if (AnnotationUtils.findAnnotation(aClass, RequestMapping.class) != null) {
         typeUrl = ((RequestMapping)AnnotationUtils.findAnnotation(aClass, RequestMapping.class)).value();
      }

      return typeUrl;
   }

   @SuppressWarnings("unchecked")
   private static void restMethodHandler(List methods, String[] finalTypeUrl, Set cacheUrl) {
      ((List<Method>) methods).forEach((m) -> {
         if (AnnotationUtils.findAnnotation(m, PostMapping.class) != null || AnnotationUtils.findAnnotation(m, RequestMapping.class) != null && Arrays.stream(((RequestMapping)AnnotationUtils.findAnnotation(m, RequestMapping.class)).method()).allMatch((r) -> !r.equals(RequestMethod.GET))) {
            urlHandler(m, finalTypeUrl, cacheUrl);
         }

      });
   }

   @SuppressWarnings("unchecked")
   private static void methodHandler(List methods, String[] finalTypeUrl, Set cacheUrl) {
      ((List<Method>) methods).forEach((m) -> {
         if (AnnotationUtils.findAnnotation(m, PostMapping.class) != null && AnnotationUtils.findAnnotation(m, ResponseBody.class) != null || AnnotationUtils.findAnnotation(m, RequestMapping.class) != null && Arrays.stream(((RequestMapping)AnnotationUtils.findAnnotation(m, RequestMapping.class)).method()).allMatch((r) -> !r.equals(RequestMethod.GET)) && AnnotationUtils.findAnnotation(m, ResponseBody.class) != null) {
            urlHandler(m, finalTypeUrl, cacheUrl);
         }

      });
   }

   private static void urlHandler(Method m, String[] finalTypeUrl, Set cacheUrl) {
      String[] urls = null;
      if (AnnotationUtils.findAnnotation(m, PostMapping.class) != null) {
         urls = ((PostMapping)AnnotationUtils.findAnnotation(m, PostMapping.class)).value();
      } else {
         urls = ((RequestMapping)AnnotationUtils.findAnnotation(m, RequestMapping.class)).value();
      }

      Arrays.stream(urls).forEach((u) -> {
         String effectiveU = u.startsWith("/") ? u : "/".concat(u);

         if (finalTypeUrl != null && finalTypeUrl.length > 0) {
            Arrays.stream(finalTypeUrl).forEach((f) -> {
               String effectiveF = f.startsWith("/") ? f : "/".concat(f);

               String uri = effectiveF.concat(effectiveU).replaceAll("//+", "/");
               if (uri.endsWith("/")) {
                  uri = uri.substring(0, uri.length() - 1);
               }

               cacheUrl.add(uri);
            });
         } else {
            String uri = effectiveU.replaceAll("//+", "/");
            if (uri.endsWith("/")) {
               uri = uri.substring(0, uri.length() - 1);
            }

            cacheUrl.add(uri);
         }

      });
   }
}
