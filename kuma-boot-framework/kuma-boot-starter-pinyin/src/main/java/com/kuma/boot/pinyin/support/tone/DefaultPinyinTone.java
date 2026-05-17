package com.kuma.boot.pinyin.support.tone;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kuma.boot.common.support.handler.Handler;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.core.utils.io.FileStreamUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.pinyin.constant.enums.PinyinToneNumEnum;
import com.kuma.boot.pinyin.model.CharToneInfo;
import com.kuma.boot.pinyin.model.ToneItem;
import com.kuma.boot.pinyin.spi.IPinyinToneStyle;
import com.kuma.boot.pinyin.util.InnerToneHelper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DefaultPinyinTone extends AbstractPinyinTone {
   private static volatile Map<String, List<String>> charMap;
   private static volatile Map<String, String> phraseMap;

   public DefaultPinyinTone() {
   }

   protected List<String> getCharTones(String chinese, final IPinyinToneStyle toneStyle) {
      List<String> defaultList = (List)this.getCharMap().get(chinese);
      return CollectionUtils.toList(defaultList, new Handler<String, String>() {
         {
            Objects.requireNonNull(DefaultPinyinTone.this);
         }

         public String handle(String s) {
            return toneStyle.style(s);
         }
      });
   }

   protected String getCharTone(String segment, final IPinyinToneStyle toneStyle) {
      List<String> pinyinList = (List)this.getCharMap().get(segment);
      if (CollectionUtils.isNotEmpty(pinyinList)) {
         String firstPinyin = (String)pinyinList.get(0);
         return toneStyle.style(firstPinyin);
      } else {
         return null;
      }
   }

   protected String getPhraseTone(String segment, final IPinyinToneStyle toneStyle, final String connector) {
      String phrasePinyin = (String)this.getPhraseMap().get(segment);
      if (StringUtils.isEmptyTrim(phrasePinyin)) {
         return "";
      } else {
         String[] strings = phrasePinyin.split(" ");
         List<String> resultList = Lists.newArrayList();

         for(String string : strings) {
            String style = toneStyle.style(string);
            resultList.add(style);
         }

         return StringUtils.join(resultList, connector);
      }
   }

   private Map<String, List<String>> getCharMap() {
      if (ObjectUtils.isNotNull(charMap)) {
         return charMap;
      } else {
         synchronized(DefaultPinyinTone.class) {
            if (ObjectUtils.isNull(charMap)) {
               List<String> lines = FileStreamUtils.readAllLines("/pinyin/pinyin_dict_char.txt");
               List<String> defineLines = FileStreamUtils.readAllLines("/pinyin/pinyin_dict_char_define.txt");
               lines.addAll(defineLines);
               charMap = Maps.newHashMap();

               for(String line : lines) {
                  String[] strings = line.split(":");
                  List<String> pinyinList = StringUtils.splitToList(strings[1]);
                  String word = strings[0];
                  charMap.put(word, pinyinList);
               }
            }
         }

         return charMap;
      }
   }

   private Map<String, String> getPhraseMap() {
      if (ObjectUtils.isNotNull(phraseMap)) {
         return phraseMap;
      } else {
         synchronized(DefaultPinyinTone.class) {
            if (ObjectUtils.isNull(phraseMap)) {
               long startTime = System.currentTimeMillis();
               List<String> lines = FileStreamUtils.readAllLines("/pinyin/pinyin_dict_phrase.txt");
               List<String> defineLines = FileStreamUtils.readAllLines("/pinyin/pinyin_dict_phrase_define.txt");
               lines.addAll(defineLines);
               phraseMap = Maps.newHashMap();

               for(String line : lines) {
                  String[] strings = line.split(":");
                  String word = strings[0];
                  phraseMap.put(word, strings[1]);
               }

               long endTime = System.currentTimeMillis();
               LogUtils.info("[Pinyin] phrase dict loaded, cost time " + (endTime - startTime) + " ms!", new Object[0]);
            }
         }

         return phraseMap;
      }
   }

   public Set<String> phraseSet() {
      Map<String, String> map = this.getPhraseMap();
      return map.keySet();
   }

   public int toneNum(String defaultPinyin) {
      if (StringUtils.isNotEmpty(defaultPinyin)) {
         CharToneInfo toneInfo = this.getCharToneInfo(defaultPinyin);
         int index = toneInfo.getIndex();
         return index < 0 ? PinyinToneNumEnum.FIVE.num() : toneInfo.getToneItem().getTone();
      } else {
         return PinyinToneNumEnum.UN_KNOWN.num();
      }
   }

   protected CharToneInfo getCharToneInfo(final String tone) {
      CharToneInfo charToneInfo = new CharToneInfo();
      charToneInfo.setIndex(-1);
      int length = tone.length();

      for(int i = 0; i < length; ++i) {
         char currentChar = tone.charAt(i);
         ToneItem toneItem = InnerToneHelper.getToneItem(currentChar);
         if (ObjectUtils.isNotNull(toneItem)) {
            charToneInfo.setToneItem(toneItem);
            charToneInfo.setIndex(i);
            break;
         }
      }

      return charToneInfo;
   }
}
