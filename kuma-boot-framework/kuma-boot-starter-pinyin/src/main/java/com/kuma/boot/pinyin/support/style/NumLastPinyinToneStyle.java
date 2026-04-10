package com.kuma.boot.pinyin.support.style;

import com.kuma.boot.pinyin.model.CharToneInfo;

public class NumLastPinyinToneStyle extends AbstractPinyinToneStyle {
   public NumLastPinyinToneStyle() {
   }

   protected String getCharFormat(String tone, CharToneInfo toneInfo) {
      int index = toneInfo.getIndex();
      if (index < 0) {
         return tone + "5";
      } else {
         char letter = toneInfo.getToneItem().getLetter();
         int num = toneInfo.getToneItem().getTone();
         String var10000 = super.connector(tone, index, String.valueOf(letter));
         return var10000 + num;
      }
   }
}
