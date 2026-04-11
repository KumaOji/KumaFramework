package com.kuma.boot.xss.xsssupport;

public enum XssType {
   FORM {
      public RuntimeException getXssException(String name, String input, String message) {
         return new FromXssException(input, message);
      }
   },
   JACKSON {
      public RuntimeException getXssException(String name, String input, String message) {
         return new JacksonXssException(name, input, message);
      }
   };

   private XssType() {
   }

   public abstract RuntimeException getXssException(String name, String input, String message);

   // $FF: synthetic method
   private static XssType[] $values() {
      return new XssType[]{FORM, JACKSON};
   }
}
