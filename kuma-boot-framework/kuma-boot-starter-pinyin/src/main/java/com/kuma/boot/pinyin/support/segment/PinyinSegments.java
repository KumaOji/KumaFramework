package com.kuma.boot.pinyin.support.segment;

import com.kuma.boot.common.support.instance.Instances;
import com.kuma.boot.pinyin.spi.IPinyinSegment;

public final class PinyinSegments {
   private PinyinSegments() {
   }

   public static IPinyinSegment chars() {
      return (IPinyinSegment)Instances.singleton(CharPinyinSegment.class);
   }

   public static IPinyinSegment defaults() {
      return (IPinyinSegment)Instances.singleton(DefaultPinyinSegment.class);
   }

   public static IPinyinSegment single() {
      return (IPinyinSegment)Instances.singleton(SinglePinyinSegment.class);
   }
}
