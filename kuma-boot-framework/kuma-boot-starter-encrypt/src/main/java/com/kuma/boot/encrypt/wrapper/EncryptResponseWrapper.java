package com.kuma.boot.encrypt.wrapper;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class EncryptResponseWrapper extends HttpServletResponseWrapper {
   private ServletOutputStream filterOutput;
   private ByteArrayOutputStream output = new ByteArrayOutputStream();

   public EncryptResponseWrapper(HttpServletResponse response) {
      super(response);
   }

   public ServletOutputStream getOutputStream() throws IOException {
      if (this.filterOutput == null) {
         this.filterOutput = new ServletOutputStream() {
            {
               Objects.requireNonNull(EncryptResponseWrapper.this);
            }

            public void write(int b) throws IOException {
               EncryptResponseWrapper.this.output.write(b);
            }

            public boolean isReady() {
               return false;
            }

            public void setWriteListener(WriteListener writeListener) {
            }
         };
      }

      return this.filterOutput;
   }

   public byte[] getResponseData() {
      return this.output.toByteArray();
   }
}
