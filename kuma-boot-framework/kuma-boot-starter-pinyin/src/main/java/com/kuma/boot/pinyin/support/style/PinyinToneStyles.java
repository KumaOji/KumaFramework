package com.kuma.boot.pinyin.support.style;

import com.kuma.boot.common.support.instance.Instances;
import com.kuma.boot.pinyin.constant.enums.PinyinStyleEnum;
import com.kuma.boot.pinyin.spi.IPinyinToneStyle;

public final class PinyinToneStyles {
   private PinyinToneStyles() {
   }

   public static IPinyinToneStyle getTone(final PinyinStyleEnum styleEnum) {
      if (PinyinStyleEnum.DEFAULT.equals(styleEnum)) {
         return defaults();
      } else if (PinyinStyleEnum.NORMAL.equals(styleEnum)) {
         return normal();
      } else if (PinyinStyleEnum.NUM_LAST.equals(styleEnum)) {
         return numLast();
      } else if (PinyinStyleEnum.FIRST_LETTER.equals(styleEnum)) {
         return firstLetter();
      } else {
         return PinyinStyleEnum.INPUT.equals(styleEnum) ? input() : defaults();
      }
   }

   public static IPinyinToneStyle defaults() {
      return (IPinyinToneStyle)Instances.singleton(DefaultPinyinToneStyle.class);
   }

   public static IPinyinToneStyle firstLetter() {
      return (IPinyinToneStyle)Instances.singleton(FirstLetterPinyinToneStyle.class);
   }

   public static IPinyinToneStyle numLast() {
      return (IPinyinToneStyle)Instances.singleton(NumLastPinyinToneStyle.class);
   }

   public static IPinyinToneStyle normal() {
      return (IPinyinToneStyle)Instances.singleton(NormalPinyinToneStyle.class);
   }

   public static IPinyinToneStyle input() {
      return (IPinyinToneStyle)Instances.singleton(InputToneStyle.class);
   }
}
