package com.kuma.boot.pinyin.support.style;

import com.kuma.boot.pinyin.model.CharToneInfo;

public class NormalPinyinToneStyle extends AbstractPinyinToneStyle {
   public NormalPinyinToneStyle() {
   }

   protected String getCharFormat(String tone, CharToneInfo toneInfo) {
      int index = toneInfo.getIndex();
      if (index < 0) {
         return tone;
      } else {
         char letter = toneInfo.getToneItem().getLetter();
         return super.connector(tone, index, String.valueOf(letter));
      }
   }
}
