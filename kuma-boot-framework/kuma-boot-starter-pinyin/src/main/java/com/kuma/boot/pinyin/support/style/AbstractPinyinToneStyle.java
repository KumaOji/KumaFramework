package com.kuma.boot.pinyin.support.style;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.pinyin.model.CharToneInfo;
import com.kuma.boot.pinyin.spi.IPinyinToneStyle;
import com.kuma.boot.pinyin.util.InnerToneHelper;

public abstract class AbstractPinyinToneStyle implements IPinyinToneStyle {
   public AbstractPinyinToneStyle() {
   }

   protected abstract String getCharFormat(final String tone, final CharToneInfo toneInfo);

   public String style(String charTone) {
      if (StringUtils.isEmpty(charTone)) {
         return charTone;
      } else {
         CharToneInfo toneInfo = InnerToneHelper.getCharToneInfo(charTone);
         return this.getCharFormat(charTone, toneInfo);
      }
   }

   String connector(final String tone, final int index, final String letter) {
      int maxIndex = index + 1;
      if (index + 1 == tone.length()) {
         String var10000 = tone.substring(0, index);
         return var10000 + letter;
      } else {
         return tone.substring(0, index) + letter + tone.substring(maxIndex);
      }
   }
}
