package com.kuma.boot.xss.xsssupport;

public class JacksonXssException extends RuntimeException implements XssException {
   private final String name;
   private final String input;

   public JacksonXssException(String name, String input, String message) {
      super(message);
      this.name = name;
      this.input = input;
   }

   public String getName() {
      return XssException.super.getName();
   }

   public String getInput() {
      return null;
   }
}
