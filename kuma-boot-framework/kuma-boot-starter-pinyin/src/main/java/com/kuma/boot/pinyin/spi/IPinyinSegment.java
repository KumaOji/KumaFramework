package com.kuma.boot.pinyin.spi;

import java.util.List;

public interface IPinyinSegment {
   List<String> segment(final String string);
}
