package com.kuma.boot.pinyin.support.segment;

import com.kuma.boot.pinyin.spi.IPinyinSegment;
import java.util.Collections;
import java.util.List;

public class SinglePinyinSegment implements IPinyinSegment {
   public SinglePinyinSegment() {
   }

   public List<String> segment(String string) {
      return Collections.singletonList(string);
   }
}
