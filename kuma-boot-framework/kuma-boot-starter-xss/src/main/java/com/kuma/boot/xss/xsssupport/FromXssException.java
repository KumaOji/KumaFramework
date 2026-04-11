package com.kuma.boot.xss.xsssupport;

public class FromXssException extends IllegalStateException implements XssException {
   private final String input;

   public FromXssException(String input, String message) {
      super(message);
      this.input = input;
   }

   public String getName() {
      return XssException.super.getName();
   }

   public String getInput() {
      return null;
   }
}
