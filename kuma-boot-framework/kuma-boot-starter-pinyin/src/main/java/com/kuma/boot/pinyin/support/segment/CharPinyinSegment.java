package com.kuma.boot.pinyin.support.segment;

import com.kuma.boot.common.support.handler.Handler;
import com.kuma.boot.common.utils.collection.ArrayPrimitiveUtils;
import com.kuma.boot.pinyin.spi.IPinyinSegment;
import java.util.List;
import java.util.Objects;

public class CharPinyinSegment implements IPinyinSegment {
   public CharPinyinSegment() {
   }

   public List<String> segment(String string) {
      char[] chars = string.toCharArray();
      return ArrayPrimitiveUtils.toList(chars, new Handler<Character, String>() {
         {
            Objects.requireNonNull(CharPinyinSegment.this);
         }

         public String handle(Character character) {
            return String.valueOf(character);
         }
      });
   }
}
