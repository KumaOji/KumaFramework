package com.kuma.boot.pinyin.support.style;

import com.kuma.boot.pinyin.model.CharToneInfo;

public class InputToneStyle extends AbstractPinyinToneStyle {
   public InputToneStyle() {
   }

   protected String getCharFormat(String tone, CharToneInfo toneInfo) {
      int index = toneInfo.getIndex();
      String result = tone;
      if (index >= 0) {
         char letter = toneInfo.getToneItem().getLetter();
         result = super.connector(tone, index, String.valueOf(letter));
      }

      return result.replace('\u00fc', 'v');
   }
}
