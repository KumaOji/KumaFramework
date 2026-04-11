package com.kuma.boot.xss.xsssupport;

import java.io.IOException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.exc.MismatchedInputException;

public abstract class XssCleanDeserializerBase extends ValueDeserializer<String> {
   public XssCleanDeserializerBase() {
   }

   public String deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
      String name = p.currentName();
      if (p.hasToken(JsonToken.VALUE_STRING)) {
         String text = p.getText();
         if (text == null) {
            return null;
         } else {
            try {
               return this.clean(name, text);
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
         }
      } else {
         JsonToken jsonToken = p.getLastClearedToken();
         if (jsonToken.isScalarValue()) {
            String text = p.getValueAsString();
            if (text != null) {
               return text;
            }
         }

         throw MismatchedInputException.from(p, String.class, "xss: can't deserialize json name:" + name + " value of type java.lang.String from " + String.valueOf(jsonToken));
      }
   }

   public abstract String clean(String name, String value) throws IOException;
}
