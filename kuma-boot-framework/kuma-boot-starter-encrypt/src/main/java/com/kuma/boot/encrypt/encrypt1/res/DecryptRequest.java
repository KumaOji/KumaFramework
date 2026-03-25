package com.kuma.boot.encrypt.encrypt1.res;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.encrypt1.annotation.Decrypt;
import com.kuma.boot.encrypt.encrypt1.properties.EncryptProperties;
import com.kuma.boot.encrypt.encrypt1.utils.AESUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Objects;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

@EnableConfigurationProperties({EncryptProperties.class})
@RestControllerAdvice
public class DecryptRequest extends RequestBodyAdviceAdapter {
   private final EncryptProperties encryptProperties;

   public DecryptRequest(EncryptProperties encryptProperties) {
      this.encryptProperties = encryptProperties;
   }

   public boolean supports(MethodParameter methodParameter, Type targetType, Class converterType) {
      return methodParameter.hasMethodAnnotation(Decrypt.class) || methodParameter.hasParameterAnnotation(Decrypt.class);
   }

   public HttpInputMessage beforeBodyRead(final HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class converterType) throws IOException {
      byte[] body = new byte[inputMessage.getBody().available()];
      inputMessage.getBody().read(body);

      try {
         byte[] decrypt = AESUtils.decrypt(body, this.encryptProperties.getKey().getBytes());
         final ByteArrayInputStream bais = new ByteArrayInputStream(decrypt);
         return new HttpInputMessage() {
            {
               Objects.requireNonNull(DecryptRequest.this);
            }

            public InputStream getBody() throws IOException {
               return bais;
            }

            public HttpHeaders getHeaders() {
               return inputMessage.getHeaders();
            }
         };
      } catch (Exception e) {
         LogUtils.error(e);
         return super.beforeBodyRead(inputMessage, parameter, targetType, converterType);
      }
   }
}
