package com.kuma.boot.encrypt.wrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

public class CacheRequestWrapper extends HttpServletRequestWrapper {
   private byte[] body;

   public byte[] getBody() {
      return this.body;
   }

   public CacheRequestWrapper(HttpServletRequest request) throws IOException, ServletException {
      super(request);
      ServletInputStream inputStream = request.getInputStream();
      String header = request.getHeader("Content-Length");
      if (header != null) {
         int contentLength = Integer.parseInt(header);
         byte[] bytes = new byte[contentLength];

         for(int readCount = 0; readCount < contentLength; readCount += inputStream.read(bytes, readCount, contentLength - readCount)) {
         }

         this.body = bytes;
      }
   }

   public ServletInputStream getInputStream() throws IOException {
      final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.body);
      return new ServletInputStream() {
         {
            Objects.requireNonNull(CacheRequestWrapper.this);
         }

         public boolean isFinished() {
            return byteArrayInputStream.available() == 0;
         }

         public boolean isReady() {
            return true;
         }

         public void setReadListener(ReadListener readListener) {
         }

         public int read() throws IOException {
            return byteArrayInputStream.read();
         }
      };
   }
}
