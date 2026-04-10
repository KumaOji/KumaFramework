package com.kuma.boot.pinyin.api;

import java.util.List;

public interface IPinyin {
   String toPinyin(final String string, final IPinyinContext context);

   List<String> toPinyinList(final char chinese, final IPinyinContext context);

   boolean hasSamePinyin(final char chineseOne, final char chineseTwo, final IPinyinContext context);

   /** @deprecated */
   @Deprecated
   List<Integer> toneNumList(final String chinese, final IPinyinContext context);

   /** @deprecated */
   @Deprecated
   List<Integer> toneNumList(final char chinese, final IPinyinContext context);

   /** @deprecated */
   @Deprecated
   List<String> shengMuList(final String chinese, final IPinyinContext context);

   /** @deprecated */
   @Deprecated
   List<String> yunMuList(final String chinese, final IPinyinContext context);

   List<String> samePinyinList(String pinyin, final boolean sameToneNum, final IPinyinContext context);
}
