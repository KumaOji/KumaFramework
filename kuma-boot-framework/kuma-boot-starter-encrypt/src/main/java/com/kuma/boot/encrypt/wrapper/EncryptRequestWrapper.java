package com.kuma.boot.encrypt.wrapper;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.encrypt.handler.EncryptHandler;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class EncryptRequestWrapper extends HttpServletRequestWrapper {
   private byte[] body;
   private EncryptHandler encryptHandler;

   public EncryptRequestWrapper(HttpServletRequest request, EncryptHandler encryptHandler) throws IOException, ServletException {
      super(request);
      this.encryptHandler = encryptHandler;
      if (!request.getContentType().equalsIgnoreCase("application/json") && !request.getContentType().equalsIgnoreCase("application/json")) {
         throw new ServletException("contentType error");
      } else {
         ServletInputStream inputStream = request.getInputStream();
         int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
         byte[] bytes = new byte[contentLength];

         for(int readCount = 0; readCount < contentLength; readCount += inputStream.read(bytes, readCount, contentLength - readCount)) {
         }

         this.body = bytes;
      }
   }

   public ServletInputStream getInputStream() throws IOException {
      LogUtils.info("接收到的请求密文：" + new String(this.body), new Object[0]);
      byte[] decode = this.encryptHandler.decode(this.body);
      String urlDecodeStr = URLDecoder.decode(new String(decode), StandardCharsets.UTF_8);
      LogUtils.info("解密后的报文：" + urlDecodeStr, new Object[0]);
      final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(urlDecodeStr.getBytes());
      return new ServletInputStream() {
         {
            Objects.requireNonNull(EncryptRequestWrapper.this);
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
