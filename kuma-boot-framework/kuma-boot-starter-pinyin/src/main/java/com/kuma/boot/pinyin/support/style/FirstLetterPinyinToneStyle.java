package com.kuma.boot.pinyin.support.style;

import com.kuma.boot.pinyin.model.CharToneInfo;

public class FirstLetterPinyinToneStyle extends AbstractPinyinToneStyle {
   public FirstLetterPinyinToneStyle() {
   }

   protected String getCharFormat(String tone, CharToneInfo toneInfo) {
      int index = toneInfo.getIndex();
      return index != 0 ? String.valueOf(tone.charAt(0)) : String.valueOf(toneInfo.getToneItem().getLetter());
   }
}
