package com.kuma.boot.pinyin.api.impl;

import com.google.common.collect.Lists;
import com.kuma.boot.common.support.handler.Handler;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.pinyin.api.IPinyin;
import com.kuma.boot.pinyin.api.IPinyinContext;
import com.kuma.boot.pinyin.spi.IPinyinChinese;
import com.kuma.boot.pinyin.spi.IPinyinData;
import com.kuma.boot.pinyin.spi.IPinyinSegment;
import com.kuma.boot.pinyin.spi.IPinyinTone;
import com.kuma.boot.pinyin.spi.IPinyinToneReverse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Pinyin implements IPinyin {
   public Pinyin() {
   }

   public String toPinyin(String string, IPinyinContext context) {
      List<String> stringList = this.toPinyinList(string, context);
      String connector = context.connector();
      return StringUtils.join(stringList, connector);
   }

   public List<String> toPinyinList(char chinese, IPinyinContext context) {
      String original = String.valueOf(chinese);
      IPinyinChinese pinyinChinese = context.chinese();
      IPinyinTone pinyinTone = context.tone();
      if (pinyinChinese.isChinese(original)) {
         String simple = pinyinChinese.toSimple(original);
         return pinyinTone.toneList(simple, context);
      } else {
         return Collections.singletonList(original);
      }
   }

   public boolean hasSamePinyin(char chineseOne, char chineseTwo, IPinyinContext context) {
      IPinyinChinese pinyinChinese = context.chinese();
      IPinyinTone pinyinTone = context.tone();
      if (pinyinChinese.isChinese(chineseOne) && pinyinChinese.isChinese(chineseTwo)) {
         if (chineseOne == chineseTwo) {
            return true;
         } else {
            String simpleOne = pinyinChinese.toSimple(chineseOne);
            String simpleTwo = pinyinChinese.toSimple(chineseTwo);
            List<String> tonesOne = pinyinTone.toneList(simpleOne, context);
            List<String> tonesTwo = pinyinTone.toneList(simpleTwo, context);
            return CollectionUtils.containAny(tonesOne, tonesTwo);
         }
      } else {
         return false;
      }
   }

   /** @deprecated */
   @Deprecated
   public List<Integer> toneNumList(String chinese, IPinyinContext context) {
      List<String> pinyinList = this.toPinyinList(chinese, context);
      return this.buildToneNumList(pinyinList, context.tone());
   }

   /** @deprecated */
   @Deprecated
   public List<Integer> toneNumList(char chinese, IPinyinContext context) {
      List<String> pinyinList = this.toPinyinList(chinese, context);
      return this.buildToneNumList(pinyinList, context.tone());
   }

   /** @deprecated */
   @Deprecated
   public List<String> shengMuList(String chinese, IPinyinContext context) {
      IPinyinData pinyinData = context.data();
      Objects.requireNonNull(pinyinData);
      return this.normalPinyinHandler(chinese, context, pinyinData::shengMu);
   }

   /** @deprecated */
   @Deprecated
   public List<String> yunMuList(String chinese, IPinyinContext context) {
      IPinyinData pinyinData = context.data();
      Objects.requireNonNull(pinyinData);
      return this.normalPinyinHandler(chinese, context, pinyinData::yunMu);
   }

   public List<String> samePinyinList(String pinyin, boolean sameToneNum, IPinyinContext context) {
      IPinyinToneReverse pinyinToneReverse = context.pinyinToneReverse();
      if (sameToneNum) {
         return pinyinToneReverse.getHanziList(pinyin);
      } else {
         List<String> resultList = new ArrayList();
         String pinyinRaw = pinyin.substring(0, pinyin.length() - 1);

         for(int i = 1; i <= 5; ++i) {
            String pinyinLast = pinyinRaw + i;
            List<String> characterList = pinyinToneReverse.getHanziList(pinyinLast);
            if (CollectionUtils.isNotEmpty(characterList)) {
               resultList.addAll(characterList);
            }
         }

         return resultList;
      }
   }

   private List<String> toPinyinList(final String string, final IPinyinContext context) {
      if (StringUtils.isEmptyTrim(string)) {
         return Collections.emptyList();
      } else {
         IPinyinSegment pinyinSegment = context.segment();
         IPinyinChinese pinyinChinese = context.chinese();
         IPinyinTone pinyinTone = context.tone();
         List<String> entryList = pinyinSegment.segment(string);
         List<String> resultList = Lists.newArrayList();

         for(String entry : entryList) {
            if (!StringUtils.isEmptyTrim(entry)) {
               if (pinyinChinese.isChinese(entry)) {
                  String simple = pinyinChinese.toSimple(entry);
                  String tone = pinyinTone.tone(simple, context);
                  resultList.add(tone);
               } else {
                  resultList.add(entry);
               }
            }
         }

         return resultList;
      }
   }

   private List<Integer> buildToneNumList(List<String> pinyinList, final IPinyinTone pinyinTone) {
      List<Integer> resultList = Lists.newArrayList();

      for(String pinyin : pinyinList) {
         Integer toneNum = pinyinTone.toneNum(pinyin);
         resultList.add(toneNum);
      }

      return resultList;
   }

   private List<String> normalPinyinHandler(final String chinese, final IPinyinContext context, final Handler<String, String> handler) {
      List<String> pinyinList = this.toPinyinList(chinese, context);
      List<String> resultList = Lists.newArrayList();

      for(String pinyin : pinyinList) {
         String result = (String)handler.handle(pinyin);
         resultList.add(result);
      }

      return resultList;
   }
}
