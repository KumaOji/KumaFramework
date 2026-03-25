package com.kuma.boot.encrypt.sign.bean;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

public class DecryptHttpInputMessage implements HttpInputMessage {
   private InputStream body;
   private HttpHeaders headers;

   public DecryptHttpInputMessage() {
   }

   public DecryptHttpInputMessage(InputStream body, HttpHeaders headers) {
      this.body = body;
      this.headers = headers;
   }

   public InputStream getBody() throws IOException {
      return this.body;
   }

   public HttpHeaders getHeaders() {
      return this.headers;
   }
}
