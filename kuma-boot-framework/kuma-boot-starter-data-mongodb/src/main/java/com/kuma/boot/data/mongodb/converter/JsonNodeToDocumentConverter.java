package com.kuma.boot.data.mongodb.converter;

import org.bson.Document;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import tools.jackson.databind.node.ObjectNode;

@WritingConverter
public enum JsonNodeToDocumentConverter implements Converter<ObjectNode, Document> {
   INSTANCE;

   private JsonNodeToDocumentConverter() {
   }

   public @Nullable Document convert(@Nullable ObjectNode source) {
      return source == null ? null : Document.parse(source.toString());
   }

   // $FF: synthetic method
   private static JsonNodeToDocumentConverter[] $values() {
      return new JsonNodeToDocumentConverter[]{INSTANCE};
   }
}
