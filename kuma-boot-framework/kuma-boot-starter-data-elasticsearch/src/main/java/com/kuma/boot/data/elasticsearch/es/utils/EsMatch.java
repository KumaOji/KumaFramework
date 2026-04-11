package com.kuma.boot.data.elasticsearch.es.utils;

import java.util.List;

public class EsMatch {
   private String field;
   private String word;
   private List words;
   private String mode;
   private String lte;
   private String gte;

   public EsMatch() {
   }

   public String getField() {
      return this.field;
   }

   public void setField(String field) {
      this.field = field;
   }

   public String getWord() {
      return this.word;
   }

   public void setWord(String word) {
      this.word = word;
   }

   public List getWords() {
      return this.words;
   }

   public void setWords(List words) {
      this.words = words;
   }

   public String getMode() {
      return this.mode;
   }

   public void setMode(String mode) {
      this.mode = mode;
   }

   public String getLte() {
      return this.lte;
   }

   public void setLte(String lte) {
      this.lte = lte;
   }

   public String getGte() {
      return this.gte;
   }

   public void setGte(String gte) {
      this.gte = gte;
   }
}
