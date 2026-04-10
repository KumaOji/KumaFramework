package com.kuma.boot.pinyin.model;

public class ToneItem {
   private char letter;
   private int tone;

   public ToneItem() {
   }

   public static ToneItem of(final char letter, final int tone) {
      ToneItem item = new ToneItem();
      item.letter = letter;
      item.tone = tone;
      return item;
   }

   public char getLetter() {
      return this.letter;
   }

   public int getTone() {
      return this.tone;
   }

   public String toString() {
      return "ToneItem{letter=" + this.letter + ", tone=" + this.tone + "}";
   }
}
