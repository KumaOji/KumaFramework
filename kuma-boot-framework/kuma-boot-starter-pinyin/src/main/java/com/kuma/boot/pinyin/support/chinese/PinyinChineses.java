package com.kuma.boot.pinyin.support.chinese;

import com.kuma.boot.common.support.instance.Instances;
import com.kuma.boot.pinyin.spi.IPinyinChinese;

public final class PinyinChineses {
   private PinyinChineses() {
   }

   public static IPinyinChinese defaults() {
      return (IPinyinChinese)Instances.singleton(DefaultsPinyinChinese.class);
   }
}
