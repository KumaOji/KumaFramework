package com.kuma.boot.pinyin.support.tone;

import com.google.common.collect.Maps;
import com.kuma.boot.core.utils.io.FileStreamUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.pinyin.spi.IPinyinToneReverse;
import com.kuma.boot.pinyin.spi.IPinyinToneStyle;
import com.kuma.boot.pinyin.support.style.PinyinToneStyles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PinyinToneReverse implements IPinyinToneReverse {
   private static final Map<String, List<String>> CHAR_MAP;

   public PinyinToneReverse() {
   }

   public List<String> getHanziList(String pinyinLast) {
      return (List)CHAR_MAP.get(pinyinLast);
   }

   static {
      List<String> lines = FileStreamUtils.readAllLines("/pinyin/pinyin_dict_char.txt");
      List<String> defineLines = FileStreamUtils.readAllLines("/pinyin/pinyin_dict_char_define.txt");
      lines.addAll(defineLines);
      CHAR_MAP = Maps.newHashMap();
      IPinyinToneStyle pinyinToneStyle = PinyinToneStyles.numLast();

      for(String line : lines) {
         String[] strings = line.split(":");
         List<String> pinyinList = StringUtils.splitToList(strings[1]);
         String hanzi = strings[0];

         for(String pinyin : pinyinList) {
            String pinyinNumLast = pinyinToneStyle.style(pinyin);
            List<String> hanziList = (List)CHAR_MAP.get(pinyinNumLast);
            if (hanziList == null) {
               hanziList = new ArrayList();
            }

            hanziList.add(hanzi);
            CHAR_MAP.put(pinyinNumLast, hanziList);
         }
      }

   }
}
