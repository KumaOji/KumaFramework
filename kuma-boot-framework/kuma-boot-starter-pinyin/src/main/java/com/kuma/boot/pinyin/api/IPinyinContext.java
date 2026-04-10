package com.kuma.boot.pinyin.api;

import com.kuma.boot.pinyin.spi.IPinyinChinese;
import com.kuma.boot.pinyin.spi.IPinyinData;
import com.kuma.boot.pinyin.spi.IPinyinSegment;
import com.kuma.boot.pinyin.spi.IPinyinTone;
import com.kuma.boot.pinyin.spi.IPinyinToneReverse;
import com.kuma.boot.pinyin.spi.IPinyinToneStyle;

public interface IPinyinContext {
   IPinyinToneStyle style();

   IPinyinSegment segment();

   IPinyinData data();

   IPinyinChinese chinese();

   IPinyinTone tone();

   String connector();

   IPinyinToneReverse pinyinToneReverse();
}
