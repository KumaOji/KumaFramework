package com.kuma.boot.pinyin.util;

import com.kuma.boot.core.utils.io.FileStreamUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.pinyin.model.CharToneInfo;
import com.kuma.boot.pinyin.model.ToneItem;
import java.util.HashMap;
import java.util.Map;

public final class InnerToneHelper {
   private static final Map<Character, ToneItem> TONE_ITEM_MAP = new HashMap(34);

   private InnerToneHelper() {
   }

   public static ToneItem getToneItem(final char c) {
      return (ToneItem)TONE_ITEM_MAP.get(c);
   }

   public static CharToneInfo getCharToneInfo(final String tone) {
      CharToneInfo charToneInfo = new CharToneInfo();
      charToneInfo.setIndex(-1);
      int length = tone.length();

      for(int i = 0; i < length; ++i) {
         char currentChar = tone.charAt(i);
         ToneItem toneItem = getToneItem(currentChar);
         if (ObjectUtils.isNotNull(toneItem)) {
            charToneInfo.setToneItem(toneItem);
            charToneInfo.setIndex(i);
            break;
         }
      }

      return charToneInfo;
   }

   static {
      for(String line : FileStreamUtils.readAllLines("/pinyin/pinyin_dict_tone.txt")) {
         String[] strings = line.split(" ");
         ToneItem item = ToneItem.of(strings[0].charAt(0), Integer.parseInt(strings[1]));
         TONE_ITEM_MAP.put(strings[2].charAt(0), item);
      }

   }
}
