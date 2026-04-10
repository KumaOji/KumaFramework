package com.kuma.boot.pinyin.support.data;

import com.kuma.boot.pinyin.spi.IPinyinData;
import java.util.Arrays;
import java.util.List;

public class PinyinData implements IPinyinData {
   private static final List<String> ZERO_SHENG_MU_LIST = Arrays.asList("a", "ai", "an", "ang", "ao", "e", "\u00ea", "ei", "en", "eng", "er", "o", "ou");
   private static final List<String> DOUBLE_SHENG_MU_LIST = Arrays.asList("zh", "ch", "sh");

   public PinyinData() {
   }

   public String shengMu(String pinyinNormal) {
      if (this.isZeroShengMu(pinyinNormal)) {
         return "";
      } else {
         String prefixDouble = pinyinNormal.substring(0, 2);
         return DOUBLE_SHENG_MU_LIST.contains(prefixDouble) ? prefixDouble : pinyinNormal.substring(0, 1);
      }
   }

   public String yunMu(String pinyinNormal) {
      String shengMu = this.shengMu(pinyinNormal);
      return pinyinNormal.substring(shengMu.length());
   }

   public boolean isZeroShengMu(String pinyinNormal) {
      return ZERO_SHENG_MU_LIST.contains(pinyinNormal);
   }
}
