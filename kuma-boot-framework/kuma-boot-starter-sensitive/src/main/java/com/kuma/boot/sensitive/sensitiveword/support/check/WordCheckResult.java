package com.kuma.boot.sensitive.sensitiveword.support.check;

import com.kuma.boot.sensitive.sensitiveword.api.IWordCheck;
import com.kuma.boot.sensitive.sensitiveword.support.result.WordLengthResult;

public class WordCheckResult {
   private WordLengthResult wordLengthResult;
   private Class<? extends IWordCheck> checkClass;
   private String type;

   private WordCheckResult() {
   }

   public static WordCheckResult newInstance() {
      return new WordCheckResult();
   }

   public WordLengthResult wordLengthResult() {
      return this.wordLengthResult;
   }

   public WordCheckResult wordLengthResult(WordLengthResult wordLengthResult) {
      this.wordLengthResult = wordLengthResult;
      return this;
   }

   public Class<? extends IWordCheck> checkClass() {
      return this.checkClass;
   }

   public WordCheckResult checkClass(Class<? extends IWordCheck> checkClass) {
      this.checkClass = checkClass;
      return this;
   }

   public String type() {
      return this.type;
   }

   public WordCheckResult type(String type) {
      this.type = type;
      return this;
   }

   public String toString() {
      String var10000 = String.valueOf(this.wordLengthResult);
      return "WordCheckResult{wordLengthResult=" + var10000 + ", checkClass=" + String.valueOf(this.checkClass) + ", type='" + this.type + "'}";
   }
}
