package com.kuma.boot.sensitive.sensitiveword.support.result;

import java.io.Serializable;
import java.util.Set;

public class WordTagsDto implements Serializable {
   private String word;
   private Set<String> tags;

   public WordTagsDto() {
   }

   public String getWord() {
      return this.word;
   }

   public void setWord(String word) {
      this.word = word;
   }

   public Set<String> getTags() {
      return this.tags;
   }

   public void setTags(Set<String> tags) {
      this.tags = tags;
   }

   public String toString() {
      String var10000 = this.word;
      return "WordTagsDto{word='" + var10000 + "', tags=" + String.valueOf(this.tags) + "}";
   }
}
