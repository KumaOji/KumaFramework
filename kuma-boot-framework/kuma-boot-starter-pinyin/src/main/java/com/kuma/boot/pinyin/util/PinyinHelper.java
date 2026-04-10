package com.kuma.boot.pinyin.util;

import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.pinyin.bs.PinyinBs;
import com.kuma.boot.pinyin.constant.enums.PinyinStyleEnum;
import com.kuma.boot.pinyin.spi.IPinyinToneStyle;
import com.kuma.boot.pinyin.support.style.PinyinToneStyles;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PinyinHelper {
   private static final PinyinBs PINYIN_BS_DEFAULT = PinyinBs.newInstance().init();

   private PinyinHelper() {
   }

   public static String toPinyin(final String string) {
      return PINYIN_BS_DEFAULT.toPinyin(string);
   }

   public static String toPinyin(final String string, final PinyinStyleEnum styleEnum) {
      return toPinyin(string, styleEnum, " ");
   }

   public static String toPinyin(final String string, final PinyinStyleEnum styleEnum, final String connector) {
      ArgUtils.notNull(connector, "connector");
      if (StringUtils.isEmptyTrim(string)) {
         return string;
      } else {
         IPinyinToneStyle style = PinyinToneStyles.getTone(styleEnum);
         return PinyinBs.newInstance().style(style).connector(connector).init().toPinyin(string);
      }
   }

   public static List<String> toPinyinList(final char chinese) {
      return PINYIN_BS_DEFAULT.toPinyinList(chinese);
   }

   public static List<String> toPinyinList(final char chinese, final PinyinStyleEnum styleEnum) {
      IPinyinToneStyle pinyinTone = PinyinToneStyles.getTone(styleEnum);
      return PinyinBs.newInstance().style(pinyinTone).init().toPinyinList(chinese);
   }

   public static boolean hasSamePinyin(final char chineseOne, final char chineseTwo) {
      return PINYIN_BS_DEFAULT.hasSamePinyin(chineseOne, chineseTwo);
   }

   public static Map<String, List<String>> samePinyinMap(final char hanzi) {
      List<String> pinyinList = toPinyinList(hanzi, PinyinStyleEnum.NUM_LAST);
      if (CollectionUtils.isEmpty(pinyinList)) {
         return Collections.emptyMap();
      } else {
         Map<String, List<String>> map = new HashMap(pinyinList.size());

         for(String pinyin : pinyinList) {
            List<String> characterList = samePinyinList(pinyin);
            map.put(pinyin, characterList);
         }

         return map;
      }
   }

   public static List<String> samePinyinList(String pinyinNumLast) {
      boolean sameTone = true;
      return PINYIN_BS_DEFAULT.samePinyinList(pinyinNumLast, true);
   }
}
