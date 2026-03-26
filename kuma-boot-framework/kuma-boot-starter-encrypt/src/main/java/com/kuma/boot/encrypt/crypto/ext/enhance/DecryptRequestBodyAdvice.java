package com.kuma.boot.encrypt.crypto.ext.enhance;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.encrypt.crypto.ext.annotation.Crypto;
import com.kuma.boot.encrypt.crypto.ext.processor.HttpCryptoProcessor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.StringNode;

@Component("cryptoDecryptRequestBodyAdvice")
@RestControllerAdvice
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {
   private static final Logger log = LoggerFactory.getLogger(DecryptRequestBodyAdvice.class);
   private HttpCryptoProcessor httpCryptoProcessor;

   public void setInterfaceCryptoProcessor(HttpCryptoProcessor httpCryptoProcessor) {
      this.httpCryptoProcessor = httpCryptoProcessor;
   }

   public boolean supports(MethodParameter methodParameter, Type targetType, Class converterType) {
      String methodName = methodParameter.getMethod().getName();
      Crypto crypto = (Crypto)methodParameter.getMethodAnnotation(Crypto.class);
      boolean isSupports = ObjectUtils.isNotEmpty(crypto) && crypto.requestDecrypt();
      log.trace("Is DecryptRequestBodyAdvice supports method [{}] ? Status is [{}].", methodName, isSupports);
      return isSupports;
   }

   public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type targetType, Class converterType) throws IOException {
      String sessionKey = (String)httpInputMessage.getHeaders().get("session-key").get(0);
      if (StringUtils.isBlank(sessionKey)) {
         log.warn("Cannot find Herodotus Cloud custom session header. Use interface crypto founction need add X_HERODOTUS_SESSION to request header.");
         return httpInputMessage;
      } else {
         log.info("DecryptRequestBodyAdvice begin decrypt data.");
         String methodName = methodParameter.getMethod().getName();
         String className = methodParameter.getDeclaringClass().getName();
         String content = IoUtil.read(httpInputMessage.getBody()).toString();
         if (StringUtils.isNotBlank(content)) {
            String data = this.httpCryptoProcessor.decrypt(sessionKey, content);
            if (StringUtils.equals(data, content)) {
               data = this.decrypt(sessionKey, content);
            }

            log.debug("Decrypt request body for rest method [{}] in [{}] finished.", methodName, className);
            return new DecryptHttpInputMessage(httpInputMessage, StrUtil.utf8Str(data).getBytes());
         } else {
            return httpInputMessage;
         }
      }
   }

   private String decrypt(String sessionKey, String content) {
      JsonNode jsonNode = JacksonUtils.parse(content);
      if (ObjectUtils.isNotEmpty(jsonNode)) {
         this.decrypt(sessionKey, jsonNode);
         return JacksonUtils.toJson(jsonNode);
      } else {
         return content;
      }
   }

   private void decrypt(String sessionKey, JsonNode jsonNode) {
      Map.Entry<String, JsonNode> entry;
      if (jsonNode.isObject()) {
         for(Iterator<Map.Entry<String, JsonNode>> it = (Iterator)jsonNode.properties(); it.hasNext(); this.decrypt(sessionKey, (JsonNode)entry.getValue())) {
            entry = (Map.Entry)it.next();
            if (entry.getValue() instanceof StringNode && ((JsonNode)entry.getValue()).isValueNode()) {
               StringNode t = (StringNode)entry.getValue();
               String value = this.httpCryptoProcessor.decrypt(sessionKey, t.asString());
               entry.setValue(new StringNode(value));
            }
         }
      }

      if (jsonNode.isArray()) {
         for(JsonNode node : jsonNode) {
            this.decrypt(sessionKey, node);
         }
      }

   }

   public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class converterType) {
      return body;
   }

   public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class converterType) {
      return body;
   }

   public static class DecryptHttpInputMessage implements HttpInputMessage {
      private final HttpInputMessage httpInputMessage;
      private final byte[] data;

      public DecryptHttpInputMessage(HttpInputMessage httpInputMessage, byte[] data) {
         this.httpInputMessage = httpInputMessage;
         this.data = data;
      }

      public InputStream getBody() throws IOException {
         return new ByteArrayInputStream(this.data);
      }

      public HttpHeaders getHeaders() {
         return this.httpInputMessage.getHeaders();
      }
   }
}
