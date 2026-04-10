package com.kuma.boot.logger.serialization;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class ObjectSerializer implements Serializer<Object> {
   private String encoding;

   public ObjectSerializer() {
      this.encoding = StandardCharsets.UTF_8.name();
   }

   public void configure(Map<String, ?> configs, boolean isKey) {
      String propertyName = isKey ? "key.serializer.encoding" : "value.serializer.encoding";
      Object encodingValue = configs.get(propertyName);
      if (encodingValue == null) {
         encodingValue = configs.get("serializer.encoding");
      }

      if (encodingValue instanceof String) {
         this.encoding = (String)encodingValue;
      }

   }

   public byte[] serialize(String topic, Object data) {
      try {
         if (data == null) {
            return null;
         } else if (data instanceof byte[]) {
            String s = new String((byte[])data);
            return s.getBytes(StandardCharsets.UTF_8);
         } else {
            return data.toString().getBytes(this.encoding);
         }
      } catch (UnsupportedEncodingException var4) {
         throw new SerializationException("Error when serializing string to byte[] due to unsupported encoding " + this.encoding);
      }
   }
}
