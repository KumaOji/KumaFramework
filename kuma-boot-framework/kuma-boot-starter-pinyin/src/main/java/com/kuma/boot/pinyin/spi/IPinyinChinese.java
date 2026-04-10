package com.kuma.boot.pinyin.spi;

public interface IPinyinChinese {
   boolean isChinese(final String original);

   boolean isChinese(final char original);

   String toSimple(final String segment);

   String toSimple(final char original);
}
