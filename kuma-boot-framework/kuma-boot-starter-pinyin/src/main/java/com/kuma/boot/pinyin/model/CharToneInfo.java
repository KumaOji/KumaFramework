package com.kuma.boot.pinyin.model;

public class CharToneInfo {
   private int index;
   private ToneItem toneItem;

   public CharToneInfo() {
   }

   public static CharToneInfo of(final ToneItem toneItem, final int index) {
      CharToneInfo item = new CharToneInfo();
      item.toneItem = toneItem;
      item.index = index;
      return item;
   }

   public int getIndex() {
      return this.index;
   }

   public ToneItem getToneItem() {
      return this.toneItem;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public void setToneItem(ToneItem toneItem) {
      this.toneItem = toneItem;
   }

   public String toString() {
      int var10000 = this.index;
      return "CharToneInfo{index=" + var10000 + ", toneItem=" + String.valueOf(this.toneItem) + "}";
   }
}
