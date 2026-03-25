package com.kuma.boot.encrypt.wrapper;

import com.kuma.boot.encrypt.handler.EncryptHandler;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

public class EncryptBodyRequestWrapper extends HttpServletRequestWrapper {
   private byte[] body;
   private EncryptHandler encryptService;

   public EncryptBodyRequestWrapper(HttpServletRequest request, EncryptHandler encryptService) throws IOException, ServletException {
      super(request);
      this.encryptService = encryptService;
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
      byte[] decode = this.encryptService.decode(this.body);
      final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decode);
      return new ServletInputStream() {
         {
            Objects.requireNonNull(EncryptBodyRequestWrapper.this);
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
