package com.kuma.boot.sensitive.sensitiveword.support.result;

public class WordLengthResult {
   private int wordAllowLen;
   private int wordDenyLen;
   private String wordDeny;
   private String wordAllow;

   public WordLengthResult() {
   }

   public static WordLengthResult newInstance() {
      return new WordLengthResult();
   }

   public int wordAllowLen() {
      return this.wordAllowLen;
   }

   public WordLengthResult wordAllowLen(int wordAllowLen) {
      this.wordAllowLen = wordAllowLen;
      return this;
   }

   public int wordDenyLen() {
      return this.wordDenyLen;
   }

   public WordLengthResult wordDenyLen(int wordDenyLen) {
      this.wordDenyLen = wordDenyLen;
      return this;
   }

   public String wordDeny() {
      return this.wordDeny;
   }

   public WordLengthResult wordDeny(String wordDeny) {
      this.wordDeny = wordDeny;
      return this;
   }

   public String wordAllow() {
      return this.wordAllow;
   }

   public WordLengthResult wordAllow(String wordAllow) {
      this.wordAllow = wordAllow;
      return this;
   }

   public String toString() {
      return "WordLengthResult{wordAllowLen=" + this.wordAllowLen + ", wordDenyLen=" + this.wordDenyLen + ", wordDeny='" + this.wordDeny + "', wordAllow='" + this.wordAllow + "'}";
   }
}
