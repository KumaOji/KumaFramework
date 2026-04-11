package com.kuma.boot.data.mongodb.converter;

import com.kuma.boot.common.utils.json.JacksonUtils;
import org.bson.BasicBSONObject;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import tools.jackson.databind.JsonNode;

@ReadingConverter
public enum DBObjectToJsonNodeConverter implements Converter<BasicBSONObject, JsonNode> {
   INSTANCE;

   private DBObjectToJsonNodeConverter() {
   }

   public @Nullable JsonNode convert(@Nullable BasicBSONObject source) {
      return source == null ? null : JacksonUtils.MAPPER.valueToTree(source);
   }

   // $FF: synthetic method
   private static DBObjectToJsonNodeConverter[] $values() {
      return new DBObjectToJsonNodeConverter[]{INSTANCE};
   }
}
