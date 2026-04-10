package com.kuma.boot.pinyin.api.impl;

import com.kuma.boot.pinyin.api.IPinyinContext;
import com.kuma.boot.pinyin.spi.IPinyinChinese;
import com.kuma.boot.pinyin.spi.IPinyinData;
import com.kuma.boot.pinyin.spi.IPinyinSegment;
import com.kuma.boot.pinyin.spi.IPinyinTone;
import com.kuma.boot.pinyin.spi.IPinyinToneReverse;
import com.kuma.boot.pinyin.spi.IPinyinToneStyle;

public class PinyinContext implements IPinyinContext {
   private IPinyinToneStyle style;
   private IPinyinSegment segment;
   private IPinyinData data;
   private IPinyinChinese chinese;
   private IPinyinTone tone;
   private String connector;
   private IPinyinToneReverse pinyinToneReverse;

   public PinyinContext() {
   }

   public static PinyinContext newInstance() {
      return new PinyinContext();
   }

   public IPinyinToneStyle style() {
      return this.style;
   }

   public PinyinContext style(IPinyinToneStyle style) {
      this.style = style;
      return this;
   }

   public IPinyinSegment segment() {
      return this.segment;
   }

   public PinyinContext segment(IPinyinSegment segment) {
      this.segment = segment;
      return this;
   }

   public IPinyinData data() {
      return this.data;
   }

   public PinyinContext data(IPinyinData data) {
      this.data = data;
      return this;
   }

   public IPinyinChinese chinese() {
      return this.chinese;
   }

   public PinyinContext chinese(IPinyinChinese chinese) {
      this.chinese = chinese;
      return this;
   }

   public IPinyinTone tone() {
      return this.tone;
   }

   public PinyinContext tone(IPinyinTone tone) {
      this.tone = tone;
      return this;
   }

   public String connector() {
      return this.connector;
   }

   public PinyinContext connector(String connector) {
      this.connector = connector;
      return this;
   }

   public IPinyinToneReverse pinyinToneReverse() {
      return this.pinyinToneReverse;
   }

   public PinyinContext pinyinToneReverse(IPinyinToneReverse pinyinToneReverse) {
      this.pinyinToneReverse = pinyinToneReverse;
      return this;
   }
}
