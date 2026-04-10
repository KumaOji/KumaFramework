package com.kuma.boot.pinyin.bs;

import com.kuma.boot.common.support.instance.Instances;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.pinyin.api.IPinyin;
import com.kuma.boot.pinyin.api.IPinyinContext;
import com.kuma.boot.pinyin.api.impl.Pinyin;
import com.kuma.boot.pinyin.api.impl.PinyinContext;
import com.kuma.boot.pinyin.spi.IPinyinChinese;
import com.kuma.boot.pinyin.spi.IPinyinData;
import com.kuma.boot.pinyin.spi.IPinyinSegment;
import com.kuma.boot.pinyin.spi.IPinyinTone;
import com.kuma.boot.pinyin.spi.IPinyinToneReverse;
import com.kuma.boot.pinyin.spi.IPinyinToneStyle;
import com.kuma.boot.pinyin.support.chinese.PinyinChineses;
import com.kuma.boot.pinyin.support.data.PinyinData;
import com.kuma.boot.pinyin.support.segment.PinyinSegments;
import com.kuma.boot.pinyin.support.style.PinyinToneStyles;
import com.kuma.boot.pinyin.support.tone.PinyinToneReverse;
import com.kuma.boot.pinyin.support.tone.PinyinTones;
import java.util.List;

public final class PinyinBs {
   private IPinyinSegment pinyinSegment = PinyinSegments.defaults();
   private final IPinyinChinese pinyinChinese = PinyinChineses.defaults();
   private final IPinyinTone pinyinTone = PinyinTones.defaults();
   private final IPinyinData data = (IPinyinData)Instances.singleton(PinyinData.class);
   private IPinyinToneStyle style = PinyinToneStyles.defaults();
   private final IPinyin pinyin = (IPinyin)Instances.singleton(Pinyin.class);
   private String connector = " ";
   private IPinyinToneReverse pinyinToneReverse = new PinyinToneReverse();
   private IPinyinContext pinyinContext;

   private PinyinBs() {
   }

   public static PinyinBs newInstance() {
      return new PinyinBs();
   }

   public PinyinBs style(IPinyinToneStyle style) {
      ArgUtils.notNull(style, "style");
      this.style = style;
      return this;
   }

   public PinyinBs connector(String connector) {
      this.connector = connector;
      return this;
   }

   public PinyinBs segment(IPinyinSegment pinyinSegment) {
      ArgUtils.notNull(pinyinSegment, "segment");
      this.pinyinSegment = pinyinSegment;
      return this;
   }

   public PinyinBs pinyinToneReverse(IPinyinToneReverse pinyinToneReverse) {
      ArgUtils.notNull(pinyinToneReverse, "pinyinToneReverse");
      this.pinyinToneReverse = pinyinToneReverse;
      return this;
   }

   public PinyinBs init() {
      this.pinyinContext = PinyinContext.newInstance().chinese(this.pinyinChinese).data(this.data).segment(this.pinyinSegment).style(this.style).tone(this.pinyinTone).connector(this.connector).pinyinToneReverse(this.pinyinToneReverse);
      return this;
   }

   private synchronized void statusCheck() {
      if (this.pinyinContext == null) {
         this.init();
      }

   }

   public String toPinyin(String string) {
      if (StringUtils.isEmpty(string)) {
         return string;
      } else {
         this.statusCheck();
         return this.pinyin.toPinyin(string, this.pinyinContext);
      }
   }

   public List<String> toPinyinList(char chinese) {
      this.statusCheck();
      return this.pinyin.toPinyinList(chinese, this.pinyinContext);
   }

   public boolean hasSamePinyin(char chineseOne, char chineseTwo) {
      this.statusCheck();
      return this.pinyin.hasSamePinyin(chineseOne, chineseTwo, this.pinyinContext);
   }

   public List<String> samePinyinList(String pinyin, final boolean sameTone) {
      this.statusCheck();
      return this.pinyin.samePinyinList(pinyin, sameTone, this.pinyinContext);
   }
}
