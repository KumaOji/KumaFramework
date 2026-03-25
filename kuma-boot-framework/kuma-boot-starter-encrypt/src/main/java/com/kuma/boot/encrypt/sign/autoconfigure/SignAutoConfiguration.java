package com.kuma.boot.encrypt.sign.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.sign.advice.DecryptRequestBodyAdvice;
import com.kuma.boot.encrypt.sign.advice.EncryptResponseBodyAdvice;
import com.kuma.boot.encrypt.sign.aspect.SignAspect;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Objects;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
@ConditionalOnProperty(
   prefix = "kuma.boot.sign",
   name = {"enabled"},
   havingValue = "true"
)
@Import({EncryptResponseBodyAdvice.class, DecryptRequestBodyAdvice.class, SignAspect.class})
@EnableConfigurationProperties({EncryptBodyProperties.class, EncryptProperties.class, SignProperties.class})
public class SignAutoConfiguration implements WebMvcConfigurer, InitializingBean {
   public void afterPropertiesSet() throws Exception {
      LogUtils.started(SignAutoConfiguration.class, "kuma-boot-starter-encrypt", new String[0]);
   }

   public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
      JacksonJsonHttpMessageConverter converter = this.mappingJackson2HttpMessageConverter();
      converter.setSupportedMediaTypes(new LinkedList() {
         {
            Objects.requireNonNull(SignAutoConfiguration.this);
            this.add(MediaType.TEXT_HTML);
            this.add(MediaType.APPLICATION_JSON);
         }
      });
      builder.addCustomConverter(new StringHttpMessageConverter());
   }

   public JacksonJsonHttpMessageConverter mappingJackson2HttpMessageConverter() {
      return new JacksonJsonHttpMessageConverter() {
         {
            Objects.requireNonNull(SignAutoConfiguration.this);
         }

         protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
            if (object instanceof String) {
               Charset charset = this.getDefaultCharset();
               if (charset == null) {
                  charset = StandardCharsets.UTF_8;
               }

               StreamUtils.copy((String)object, charset, outputMessage.getBody());
            } else {
               super.writeInternal(object, outputMessage);
            }

         }
      };
   }
}
