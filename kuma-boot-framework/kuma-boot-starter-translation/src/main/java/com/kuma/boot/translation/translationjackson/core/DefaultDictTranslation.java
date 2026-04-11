package com.kuma.boot.translation.translationjackson.core;

public class DefaultDictTranslation implements Translation<String> {
   public DefaultDictTranslation() {
   }

   public String translation(String key, String readConverterExp, Object value) {
      String[] expArray = readConverterExp.split(",");
      if (expArray.length == 0) {
         return "";
      } else {
         for(String exp : expArray) {
            String[] contentArray = exp.split("=");
            if (contentArray[0].trim().equals(value.toString())) {
               return contentArray[1].trim();
            }
         }

         return "";
      }
   }
}
