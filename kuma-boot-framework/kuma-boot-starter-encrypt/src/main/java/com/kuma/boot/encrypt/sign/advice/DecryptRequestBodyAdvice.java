package com.kuma.boot.encrypt.sign.advice;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import com.kuma.boot.encrypt.sign.annotation.FieldBody;
import com.kuma.boot.encrypt.sign.annotation.decrypt.AESDecryptBody;
import com.kuma.boot.encrypt.sign.annotation.decrypt.DESDecryptBody;
import com.kuma.boot.encrypt.sign.annotation.decrypt.DecryptBody;
import com.kuma.boot.encrypt.sign.annotation.decrypt.RSADecryptBody;
import com.kuma.boot.encrypt.sign.autoconfigure.EncryptBodyProperties;
import com.kuma.boot.encrypt.sign.bean.DecryptAnnotationInfoBean;
import com.kuma.boot.encrypt.sign.bean.DecryptHttpInputMessage;
import com.kuma.boot.encrypt.sign.enums.DecryptBodyMethod;
import com.kuma.boot.encrypt.sign.exception.DecryptBodyFailException;
import com.kuma.boot.encrypt.sign.exception.DecryptMethodNotFoundException;
import com.kuma.boot.encrypt.sign.util.CommonUtils;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import tools.jackson.databind.json.JsonMapper;

@Order(1)
@RestControllerAdvice(
   basePackages = {"com.kuma.cloud.*.biz.api.controller", "com.kuma.cloud.*.facade.controller.**"}
)
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {
   private final EncryptBodyProperties config;
   private final JsonMapper jsonMapper;

   public DecryptRequestBodyAdvice(JsonMapper jsonMapper, EncryptBodyProperties config) {
      this.jsonMapper = jsonMapper;
      this.config = config;
   }

   public boolean supports(MethodParameter methodParameter, Type targetType, Class converterType) {
      if (this.hasDecryptAnnotation(methodParameter.getDeclaringClass())) {
         return true;
      } else {
         Method method = methodParameter.getMethod();
         if (method != null) {
            if (this.hasDecryptAnnotation(method)) {
               return true;
            }

            Class<?>[] parameterTypes = method.getParameterTypes();

            for(Class parameterType : parameterTypes) {
               if (this.hasDecryptAnnotation(parameterType)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean hasDecryptAnnotation(AnnotatedElement annotatedElement) {
      return annotatedElement.isAnnotationPresent(DecryptBody.class) || annotatedElement.isAnnotationPresent(AESDecryptBody.class) || annotatedElement.isAnnotationPresent(DESDecryptBody.class) || annotatedElement.isAnnotationPresent(RSADecryptBody.class);
   }

   public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class converterType) {
      return body;
   }

   public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class converterType) {
      String body;
      try {
         body = IoUtil.read(inputMessage.getBody(), this.config.getEncoding());
      } catch (Exception var14) {
         throw new DecryptBodyFailException("Unable to get request body data, please check if the sending data body or request method is in compliance with the specification. (无法获取请求正文数据，请检查发送数据体或请求方法是否符合规范。)");
      }

      if (body != null && !StrUtil.isEmpty(body)) {
         Class<?> targetTypeClass;
         try {
            targetTypeClass = Class.forName(targetType.getTypeName());
         } catch (ClassNotFoundException e) {
            throw new DecryptBodyFailException(e.getMessage());
         }

         String decryptBody = null;
         DecryptAnnotationInfoBean methodAnnotation = this.getDecryptAnnotation(parameter.getMethod());
         if (methodAnnotation != null) {
            decryptBody = this.switchDecrypt(body, methodAnnotation);
         } else if (this.hasDecryptAnnotation(targetTypeClass)) {
            if (targetTypeClass.isAnnotationPresent(FieldBody.class)) {
               try {
                  Object bodyInstance = this.jsonMapper.readValue(body, targetTypeClass);
                  Object decryptBodyInstance = this.eachClassField(bodyInstance, targetTypeClass);
                  decryptBody = this.jsonMapper.writeValueAsString(decryptBodyInstance);
               } catch (Exception e) {
                  throw new DecryptBodyFailException(e.getMessage());
               }
            } else {
               DecryptAnnotationInfoBean classAnnotation = this.getDecryptAnnotation(targetTypeClass);
               if (classAnnotation != null) {
                  decryptBody = this.switchDecrypt(body, classAnnotation);
               }
            }
         } else {
            DecryptAnnotationInfoBean classAnnotation = this.getDecryptAnnotation(parameter.getDeclaringClass());
            if (classAnnotation != null) {
               decryptBody = this.switchDecrypt(body, classAnnotation);
            }
         }

         if (decryptBody == null) {
            throw new DecryptBodyFailException("Decryption error, please check if the selected source data is encrypted correctly. (解密错误，请检查选择的源数据的加密方式是否正确。)");
         } else {
            try {
               return new DecryptHttpInputMessage(IoUtil.toStream(decryptBody, this.config.getEncoding()), inputMessage.getHeaders());
            } catch (Exception var11) {
               throw new DecryptBodyFailException("The string is converted to a stream format exception. Please check if the format such as encoding is correct. (字符串转换成流格式异常，请检查编码等格式是否正确。)");
            }
         }
      } else {
         throw new DecryptBodyFailException("The request body is NULL or an empty string, so the decryption failed. (请求正文为NULL或为空字符串，因此解密失败。)");
      }
   }

   public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class converterType) {
      return body;
   }

   private Object eachClassField(Object body, Class clazz) {
      Field[] declaredFields = clazz.getDeclaredFields();

      for(Field field : declaredFields) {
         field.setAccessible(true);
         DecryptAnnotationInfoBean decryptAnnotation = this.getDecryptAnnotation(field);
         Class<?> type = field.getType();
         if (decryptAnnotation != null) {
            FieldBody fieldBody = (FieldBody)field.getAnnotation(FieldBody.class);
            if (fieldBody != null) {
               Field setField = ReflectUtil.getField(clazz, fieldBody.field());
               if (setField != null && setField.getType().equals(String.class)) {
                  Object fieldValue = ReflectUtil.getFieldValue(body, setField);
                  String decryptResult = this.switchDecrypt(String.valueOf(fieldValue), decryptAnnotation);
                  ReflectUtil.setFieldValue(body, field, decryptResult);
               }
            } else if (type.equals(String.class)) {
               String decryptResult = this.switchDecrypt(String.valueOf(ReflectUtil.getFieldValue(body, field)), decryptAnnotation);
               ReflectUtil.setFieldValue(body, field, decryptResult);
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

   private DecryptAnnotationInfoBean getDecryptAnnotation(AnnotatedElement annotatedElement) {
      if (annotatedElement == null) {
         return null;
      } else {
         if (annotatedElement.isAnnotationPresent(DecryptBody.class)) {
            DecryptBody decryptBody = (DecryptBody)annotatedElement.getAnnotation(DecryptBody.class);
            if (decryptBody != null) {
               return DecryptAnnotationInfoBean.builder().decryptBodyMethod(decryptBody.value()).key(decryptBody.otherKey()).build();
            }
         }

         if (annotatedElement.isAnnotationPresent(DESDecryptBody.class)) {
            DESDecryptBody decryptBody = (DESDecryptBody)annotatedElement.getAnnotation(DESDecryptBody.class);
            if (decryptBody != null) {
               return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.DES).key(decryptBody.key()).build();
            }
         }

         if (annotatedElement.isAnnotationPresent(AESDecryptBody.class)) {
            AESDecryptBody decryptBody = (AESDecryptBody)annotatedElement.getAnnotation(AESDecryptBody.class);
            if (decryptBody != null) {
               return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.AES).key(decryptBody.key()).build();
            }
         }

         if (annotatedElement.isAnnotationPresent(RSADecryptBody.class)) {
            RSADecryptBody decryptBody = (RSADecryptBody)annotatedElement.getAnnotation(RSADecryptBody.class);
            if (decryptBody != null) {
               return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.RSA).key(decryptBody.key()).rsaKeyType(decryptBody.type()).build();
            }
         }

         return null;
      }
   }

   private String switchDecrypt(String formatStringBody, DecryptAnnotationInfoBean infoBean) {
      DecryptBodyMethod method = infoBean.getDecryptBodyMethod();
      if (method == null) {
         throw new DecryptMethodNotFoundException();
      } else {
         String key = infoBean.getKey();
         if (method == DecryptBodyMethod.DES) {
            key = CommonUtils.checkAndGetKey(this.config.getDesKey(), key, "DES-KEY");
            return SecureUtil.des(key.getBytes()).decryptStr(formatStringBody);
         } else if (method == DecryptBodyMethod.AES) {
            key = CommonUtils.checkAndGetKey(this.config.getAesKey(), key, "AES-KEY");
            return SecureUtil.aes(key.getBytes()).decryptStr(formatStringBody);
         } else if (method == DecryptBodyMethod.RSA) {
            RSA rsa = CommonUtils.infoBeanToRsaInstance(infoBean);
            return rsa.decryptStr(formatStringBody, infoBean.getRsaKeyType().toolType);
         } else {
            throw new DecryptBodyFailException();
         }
      }
   }
}
