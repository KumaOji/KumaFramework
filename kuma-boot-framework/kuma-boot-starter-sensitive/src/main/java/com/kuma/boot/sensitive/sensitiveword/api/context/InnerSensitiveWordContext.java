package com.kuma.boot.sensitive.sensitiveword.api.context;

import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordValidModeEnum;
import java.util.Map;

public class InnerSensitiveWordContext {
   private String originalText;
   private Map<Character, Character> formatCharMapping;
   private WordValidModeEnum modeEnum;
   private IWordContext wordContext;

   public InnerSensitiveWordContext() {
   }

   public static InnerSensitiveWordContext newInstance() {
      return new InnerSensitiveWordContext();
   }

   public String originalText() {
      return this.originalText;
   }

   public InnerSensitiveWordContext originalText(String text) {
      this.originalText = text;
      return this;
   }

   public Map<Character, Character> formatCharMapping() {
      return this.formatCharMapping;
   }

   public InnerSensitiveWordContext formatCharMapping(Map<Character, Character> formatCharMapping) {
      this.formatCharMapping = formatCharMapping;
      return this;
   }

   public WordValidModeEnum modeEnum() {
      return this.modeEnum;
   }

   public InnerSensitiveWordContext modeEnum(WordValidModeEnum modeEnum) {
      this.modeEnum = modeEnum;
      return this;
   }

   public IWordContext wordContext() {
      return this.wordContext;
   }

   public InnerSensitiveWordContext wordContext(IWordContext context) {
      this.wordContext = context;
      return this;
   }
}
