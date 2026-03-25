package com.kuma.boot.encrypt.sign.advice;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import com.kuma.boot.encrypt.sign.annotation.FieldBody;
import com.kuma.boot.encrypt.sign.annotation.encrypt.AESEncryptBody;
import com.kuma.boot.encrypt.sign.annotation.encrypt.DESEncryptBody;
import com.kuma.boot.encrypt.sign.annotation.encrypt.EncryptBody;
import com.kuma.boot.encrypt.sign.annotation.encrypt.MD5EncryptBody;
import com.kuma.boot.encrypt.sign.annotation.encrypt.RSAEncryptBody;
import com.kuma.boot.encrypt.sign.annotation.encrypt.SHAEncryptBody;
import com.kuma.boot.encrypt.sign.autoconfigure.EncryptBodyProperties;
import com.kuma.boot.encrypt.sign.bean.EncryptAnnotationInfoBean;
import com.kuma.boot.encrypt.sign.enums.EncryptBodyMethod;
import com.kuma.boot.encrypt.sign.enums.SHAEncryptType;
import com.kuma.boot.encrypt.sign.exception.EncryptBodyFailException;
import com.kuma.boot.encrypt.sign.exception.EncryptMethodNotFoundException;
import com.kuma.boot.encrypt.sign.util.CommonUtils;
import com.kuma.boot.encrypt.sign.util.ShaEncryptUtil;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Order(1)
@RestControllerAdvice(
   basePackages = {"com.kuma.cloud.*.biz.api.controller", "com.kuma.cloud.*.facade.controller.**"}
)
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice {
   private final JsonMapper jsonMapper;
   private final EncryptBodyProperties config;

   public EncryptResponseBodyAdvice(JsonMapper jsonMapper, EncryptBodyProperties config) {
      this.jsonMapper = jsonMapper;
      this.config = config;
   }

   public boolean supports(MethodParameter returnType, Class converterType) {
      Class<?> declaringClass = returnType.getDeclaringClass();
      if (this.hasEncryptAnnotation(declaringClass)) {
         return true;
      } else {
         Method method = returnType.getMethod();
         if (method == null) {
            return false;
         } else {
            Class<?> returnValueType = method.getReturnType();
            return this.hasEncryptAnnotation(method) || this.hasEncryptAnnotation(returnValueType);
         }
      }
   }

   private boolean hasEncryptAnnotation(AnnotatedElement annotatedElement) {
      if (annotatedElement == null) {
         return false;
      } else {
         return annotatedElement.isAnnotationPresent(EncryptBody.class) || annotatedElement.isAnnotationPresent(AESEncryptBody.class) || annotatedElement.isAnnotationPresent(DESEncryptBody.class) || annotatedElement.isAnnotationPresent(RSAEncryptBody.class) || annotatedElement.isAnnotationPresent(MD5EncryptBody.class) || annotatedElement.isAnnotationPresent(SHAEncryptBody.class);
      }
   }

   public String beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
      if (body == null) {
         return null;
      } else {
         String str = CommonUtils.convertToStringOrJson(body, this.jsonMapper);
         response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
         Method method = returnType.getMethod();
         if (method != null) {
            EncryptAnnotationInfoBean methodAnnotation = this.getEncryptAnnotation(method);
            if (methodAnnotation != null) {
               return this.switchEncrypt(str, methodAnnotation);
            }

            Class<?> methodReturnType = method.getReturnType();
            if (methodReturnType.isAnnotationPresent(FieldBody.class)) {
               Object encryptResult = this.eachClassField(body, method.getReturnType());

               try {
                  return this.jsonMapper.writeValueAsString(encryptResult);
               } catch (JacksonException e) {
                  throw new EncryptBodyFailException(e.getMessage());
               }
            }

            EncryptAnnotationInfoBean returnTypeClassAnnotation = this.getEncryptAnnotation(methodReturnType);
            if (returnTypeClassAnnotation != null) {
               return this.switchEncrypt(str, returnTypeClassAnnotation);
            }
         }

         EncryptAnnotationInfoBean classAnnotation = this.getEncryptAnnotation(returnType.getDeclaringClass());
         if (classAnnotation != null) {
            return this.switchEncrypt(str, classAnnotation);
         } else {
            throw new EncryptBodyFailException();
         }
      }
   }

   private Object eachClassField(Object body, Class returnTypeClass) {
      Field[] fields = returnTypeClass.getDeclaredFields();

      for(Field field : fields) {
         field.setAccessible(true);
         Class<?> type = field.getType();
         EncryptAnnotationInfoBean encryptAnnotation = this.getEncryptAnnotation(field);
         if (encryptAnnotation != null) {
            Object fieldValue = ReflectUtil.getFieldValue(body, field);
            if (fieldValue != null) {
               String str = CommonUtils.convertToStringOrJson(fieldValue, this.jsonMapper);
               String encryptResult = this.switchEncrypt(str, encryptAnnotation);
               if (type.equals(String.class)) {
                  ReflectUtil.setFieldValue(body, field, encryptResult);
               } else {
                  FieldBody fieldBody = (FieldBody)field.getAnnotation(FieldBody.class);
                  if (fieldBody != null) {
                     Field setField = ReflectUtil.getField(returnTypeClass, fieldBody.field());
                     if (setField != null && setField.getType().equals(String.class)) {
                        ReflectUtil.setFieldValue(body, fieldBody.field(), encryptResult);
                        if (fieldBody.clearValue()) {
                           ReflectUtil.setFieldValue(body, field, (Object)null);
                        }
                     }
                  }
               }
            }
         } else if (!CommonUtils.isConvertToString(type)) {
            Object fieldValue = ReflectUtil.getFieldValue(body, field);
            if (fieldValue != null) {
               this.eachClassField(fieldValue, type);
            }
         }
      }

      return body;
   }

   private EncryptAnnotationInfoBean getEncryptAnnotation(AnnotatedElement annotatedElement) {
      if (annotatedElement == null) {
         return null;
      } else {
         if (annotatedElement.isAnnotationPresent(EncryptBody.class)) {
            EncryptBody encryptBody = (EncryptBody)annotatedElement.getAnnotation(EncryptBody.class);
            if (encryptBody != null) {
               return EncryptAnnotationInfoBean.builder().encryptBodyMethod(encryptBody.value()).key(encryptBody.otherKey()).shaEncryptType(encryptBody.shaType()).build();
            }
         }

         if (annotatedElement.isAnnotationPresent(MD5EncryptBody.class)) {
            return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.MD5).build();
         } else {
            if (annotatedElement.isAnnotationPresent(SHAEncryptBody.class)) {
               SHAEncryptBody encryptBody = (SHAEncryptBody)annotatedElement.getAnnotation(SHAEncryptBody.class);
               if (encryptBody != null) {
                  return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.SHA).shaEncryptType(encryptBody.value()).build();
               }
            }

            if (annotatedElement.isAnnotationPresent(DESEncryptBody.class)) {
               DESEncryptBody encryptBody = (DESEncryptBody)annotatedElement.getAnnotation(DESEncryptBody.class);
               if (encryptBody != null) {
                  return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.DES).key(encryptBody.key()).build();
               }
            }

            if (annotatedElement.isAnnotationPresent(AESEncryptBody.class)) {
               AESEncryptBody encryptBody = (AESEncryptBody)annotatedElement.getAnnotation(AESEncryptBody.class);
               if (encryptBody != null) {
                  return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.AES).key(encryptBody.key()).build();
               }
            }

            if (annotatedElement.isAnnotationPresent(RSAEncryptBody.class)) {
               RSAEncryptBody encryptBody = (RSAEncryptBody)annotatedElement.getAnnotation(RSAEncryptBody.class);
               if (encryptBody != null) {
                  return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.RSA).key(encryptBody.key()).rsaKeyType(encryptBody.type()).build();
               }
            }

            return null;
         }
      }
   }

   private String switchEncrypt(String formatStringBody, EncryptAnnotationInfoBean infoBean) {
      EncryptBodyMethod method = infoBean.getEncryptBodyMethod();
      if (method == null) {
         throw new EncryptMethodNotFoundException();
      } else if (method == EncryptBodyMethod.MD5) {
         return SecureUtil.md5().digestHex(formatStringBody);
      } else if (method == EncryptBodyMethod.SHA) {
         SHAEncryptType shaEncryptType = infoBean.getShaEncryptType();
         if (shaEncryptType == null) {
            shaEncryptType = SHAEncryptType.SHA256;
         }

         return ShaEncryptUtil.encrypt(formatStringBody, shaEncryptType);
      } else {
         String key = infoBean.getKey();
         if (method == EncryptBodyMethod.DES) {
            key = CommonUtils.checkAndGetKey(this.config.getDesKey(), key, "DES-KEY");
            return SecureUtil.des(key.getBytes()).encryptHex(formatStringBody);
         } else if (method == EncryptBodyMethod.AES) {
            key = CommonUtils.checkAndGetKey(this.config.getAesKey(), key, "AES-KEY");
            return SecureUtil.aes(key.getBytes()).encryptHex(formatStringBody);
         } else if (method == EncryptBodyMethod.RSA) {
            RSA rsa = CommonUtils.infoBeanToRsaInstance(infoBean);
            return rsa.encryptHex(formatStringBody, infoBean.getRsaKeyType().toolType);
         } else {
            throw new EncryptBodyFailException();
         }
      }
   }
}
