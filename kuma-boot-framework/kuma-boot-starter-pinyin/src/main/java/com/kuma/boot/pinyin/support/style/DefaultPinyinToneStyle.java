package com.kuma.boot.pinyin.support.style;

import com.kuma.boot.pinyin.spi.IPinyinToneStyle;

public class DefaultPinyinToneStyle implements IPinyinToneStyle {
   public DefaultPinyinToneStyle() {
   }

   public String style(String charTone) {
      return charTone;
   }
}
