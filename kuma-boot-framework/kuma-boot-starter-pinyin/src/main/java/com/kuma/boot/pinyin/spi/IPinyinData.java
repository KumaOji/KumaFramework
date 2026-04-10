package com.kuma.boot.pinyin.spi;

public interface IPinyinData {
   String shengMu(final String pinyinNormal);

   String yunMu(final String pinyinNormal);

   boolean isZeroShengMu(final String pinyinNormal);
}
