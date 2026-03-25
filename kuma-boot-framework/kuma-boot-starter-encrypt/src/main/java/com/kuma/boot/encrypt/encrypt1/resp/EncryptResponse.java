package com.kuma.boot.encrypt.encrypt1.resp;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.encrypt1.annotation.Encrypt;
import com.kuma.boot.encrypt.encrypt1.commonresponse.RespBean;
import com.kuma.boot.encrypt.encrypt1.properties.EncryptProperties;
import com.kuma.boot.encrypt.encrypt1.utils.AESUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.json.JsonMapper;

@EnableConfigurationProperties({EncryptProperties.class})
@ControllerAdvice
public class EncryptResponse implements ResponseBodyAdvice<Object> {
   private final JsonMapper jsonMapper = JsonMapper.builder().build();
   private final EncryptProperties encryptProperties;

   public EncryptResponse(EncryptProperties encryptProperties) {
      this.encryptProperties = encryptProperties;
   }

   @Override
   public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
      return returnType.hasMethodAnnotation(Encrypt.class);
   }

   @Override
   public Object beforeBodyWrite(Object bodyObj, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
      if (!(bodyObj instanceof RespBean body)) {
         return bodyObj;
      }
      byte[] keyBytes = this.encryptProperties.getKey().getBytes();

      try {
         if (body.getMsg() != null) {
            body.setMsg(AESUtils.encrypt(body.getMsg().getBytes(), keyBytes));
         }

         if (body.getObj() != null) {
            body.setObj(AESUtils.encrypt(this.jsonMapper.writeValueAsBytes(body.getObj()), keyBytes));
         }
      } catch (Exception e) {
         LogUtils.error(e);
      }

      return body;
   }
}
