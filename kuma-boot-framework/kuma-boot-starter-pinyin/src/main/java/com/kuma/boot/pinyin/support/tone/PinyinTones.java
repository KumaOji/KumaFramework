package com.kuma.boot.pinyin.support.tone;

import com.kuma.boot.common.support.instance.Instances;
import com.kuma.boot.pinyin.spi.IPinyinTone;

public final class PinyinTones {
   private PinyinTones() {
   }

   public static IPinyinTone defaults() {
      return (IPinyinTone)Instances.singleton(DefaultPinyinTone.class);
   }
}
