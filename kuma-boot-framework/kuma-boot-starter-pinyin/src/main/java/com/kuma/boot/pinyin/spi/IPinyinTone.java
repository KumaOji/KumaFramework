package com.kuma.boot.pinyin.spi;

import com.kuma.boot.pinyin.api.IPinyinContext;
import java.util.List;
import java.util.Set;

public interface IPinyinTone {
   String tone(final String segment, final IPinyinContext context);

   List<String> toneList(final String chinese, final IPinyinContext context);

   Set<String> phraseSet();

   int toneNum(final String defaultPinyin);
}
