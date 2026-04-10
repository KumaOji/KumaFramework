package com.kuma.boot.pinyin.roses.api;

public interface PinYinApi {
   String getLastnameFirstLetterUpper(String lastnameChines);

   String getChineseStringFirstLetterUpper(String chineseString);

   String parsePinyinString(String chineseString);

   String parseEveryPinyinFirstLetter(String chinesString);

   String getChineseAscii(String chineseString);
}
