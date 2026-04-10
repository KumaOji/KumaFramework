package com.kuma.boot.pinyin.spi;

import java.util.List;

public interface IPinyinToneReverse {
   List<String> getHanziList(String pinyinLast);
}
