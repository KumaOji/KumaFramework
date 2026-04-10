package com.kuma.boot.pinyin.support.chinese;

import com.kuma.boot.common.utils.lang.CharUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.pinyin.spi.IPinyinChinese;

public class DefaultsPinyinChinese implements IPinyinChinese {
   public DefaultsPinyinChinese() {
   }

   public boolean isChinese(String original) {
      if (StringUtils.isEmpty(original)) {
         return false;
      } else {
         char[] chars = original.toCharArray();

         for(char c : chars) {
            if (this.isChinese(c)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isChinese(char original) {
      return CharUtils.isNotAscii(original);
   }

   public String toSimple(String segment) {
      return segment;
   }

   public String toSimple(char original) {
      return "" + original;
   }
}
