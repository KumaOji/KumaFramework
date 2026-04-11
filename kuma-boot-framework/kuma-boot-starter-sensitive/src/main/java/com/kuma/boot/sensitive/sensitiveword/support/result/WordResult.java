package com.kuma.boot.sensitive.sensitiveword.support.result;

import com.kuma.boot.sensitive.sensitiveword.api.IWordResult;

public class WordResult implements IWordResult {
   private int startIndex;
   private int endIndex;
   private String type;
   private String word;

   private WordResult() {
   }

   public static WordResult newInstance() {
      return new WordResult();
   }

   public int startIndex() {
      return this.startIndex;
   }

   public WordResult startIndex(int startIndex) {
      this.startIndex = startIndex;
      return this;
   }

   public int endIndex() {
      return this.endIndex;
   }

   public WordResult endIndex(int endIndex) {
      this.endIndex = endIndex;
      return this;
   }

   public String type() {
      return this.type;
   }

   public WordResult type(String type) {
      this.type = type;
      return this;
   }

   public String word() {
      return this.word;
   }

   public WordResult word(String word) {
      this.word = word;
      return this;
   }

   public String toString() {
      return "WordResult{startIndex=" + this.startIndex + ", endIndex=" + this.endIndex + ", type='" + this.type + "', word='" + this.word + "'}";
   }
}
