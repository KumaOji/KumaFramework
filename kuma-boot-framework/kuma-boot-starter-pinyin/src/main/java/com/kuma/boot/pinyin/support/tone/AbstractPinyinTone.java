package com.kuma.boot.pinyin.support.tone;

import com.kuma.boot.common.support.handler.Handler;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.pinyin.api.IPinyinContext;
import com.kuma.boot.pinyin.spi.IPinyinTone;
import com.kuma.boot.pinyin.spi.IPinyinToneStyle;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AbstractPinyinTone implements IPinyinTone {
   public AbstractPinyinTone() {
   }

   protected abstract String getCharTone(final String segment, final IPinyinToneStyle toneStyle);

   protected abstract List<String> getCharTones(final String chinese, final IPinyinToneStyle toneStyle);

   protected abstract String getPhraseTone(final String phraseTone, final IPinyinToneStyle toneStyle, final String connector);

   public String tone(String segment, final IPinyinContext context) {
      int length = segment.length();
      final IPinyinToneStyle toneStyle = context.style();
      String connector = context.connector();
      if (length == 1) {
         this.getCharToneDefault(segment, toneStyle);
      }

      String result = this.getPhraseTone(segment, toneStyle, connector);
      if (StringUtils.isNotEmpty(result)) {
         return result;
      } else {
         List<String> chars = StringUtils.toCharStringList(segment);
         List<String> tones = CollectionUtils.toList(chars, new Handler<String, String>() {
            {
               Objects.requireNonNull(AbstractPinyinTone.this);
            }

            public String handle(String string) {
               return AbstractPinyinTone.this.getCharToneDefault(string, toneStyle);
            }
         });
         return StringUtils.join(tones, " ");
      }
   }

   private String getCharToneDefault(final String string, final IPinyinToneStyle toneStyle) {
      String pinyin = this.getCharTone(string, toneStyle);
      return StringUtils.isNotEmpty(pinyin) ? pinyin : string;
   }

   public List<String> toneList(String chinese, final IPinyinContext context) {
      IPinyinToneStyle toneStyle = context.style();
      return this.getCharTonesDefault(chinese, toneStyle);
   }

   private List<String> getCharTonesDefault(final String chinese, final IPinyinToneStyle toneStyle) {
      List<String> toneList = this.getCharTones(chinese, toneStyle);
      return CollectionUtils.isNotEmpty(toneList) ? toneList : Collections.singletonList(chinese);
   }
}
