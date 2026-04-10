package com.kuma.boot.pinyin.roses;

import com.kuma.boot.pinyin.roses.api.PinYinApi;
import com.kuma.boot.pinyin.roses.api.exception.PinyinException;
import java.util.Properties;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinServiceImpl implements PinYinApi {
   private final Properties properties = new Properties();

   public PinyinServiceImpl() {
      this.init();
   }

   public void init() {
      this.properties.put("\u533a", "O");
      this.properties.put("\u665f", "C");
      this.properties.put("\u4e50", "Y");
      this.properties.put("\u5458", "Y");
      this.properties.put("\u8d20", "Y");
      this.properties.put("\u9ed1", "H");
      this.properties.put("\u91cd", "C");
      this.properties.put("\u4ec7", "Q");
      this.properties.put("\u79d8", "B");
      this.properties.put("\u51bc", "X");
      this.properties.put("\u89e3", "X");
      this.properties.put("\u6298", "S");
      this.properties.put("\u5355", "S");
      this.properties.put("\u6734", "P");
      this.properties.put("\u7fdf", "Z");
      this.properties.put("\u67e5", "Z");
      this.properties.put("\u76d6", "G");
      this.properties.put("\u4e07\u4fdf", "M I");
      this.properties.put("\u5c09\u8fdf", "Y C");
   }

   public String getLastnameFirstLetterUpper(String lastnameChines) {
      for(Object lastNameObject : this.properties.keySet()) {
         String lastname = (String)lastNameObject;
         if (lastnameChines.length() >= lastname.length() && lastnameChines.startsWith(lastname)) {
            return this.properties.getProperty(lastname);
         }
      }

      return getFirstLetters(lastnameChines, HanyuPinyinCaseType.UPPERCASE);
   }

   public String getChineseStringFirstLetterUpper(String chineseString) {
      return getFirstLetters(chineseString, HanyuPinyinCaseType.UPPERCASE);
   }

   public String parsePinyinString(String chineseString) {
      char[] chineseWordsArray = chineseString.toCharArray();
      HanyuPinyinOutputFormat hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
      hanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
      hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
      hanyuPinyinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
      StringBuilder finalPinyinString = new StringBuilder();

      try {
         for(char chineseWord : chineseWordsArray) {
            if (Character.toString(chineseWord).matches("[\u4e00-\u9fa5]+")) {
               String[] strings = PinyinHelper.toHanyuPinyinStringArray(chineseWord, hanyuPinyinOutputFormat);
               finalPinyinString.append(strings[0]);
            } else {
               finalPinyinString.append(chineseWord);
            }
         }

         return finalPinyinString.toString();
      } catch (BadHanyuPinyinOutputFormatCombination e1) {
         throw new PinyinException(e1.getMessage());
      }
   }

   public String parseEveryPinyinFirstLetter(String chinesString) {
      StringBuilder convert = new StringBuilder();

      for(int i = 0; i < chinesString.length(); ++i) {
         char word = chinesString.charAt(i);
         String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
         if (pinyinArray != null) {
            convert.append(pinyinArray[0].charAt(0));
         } else {
            convert.append(word);
         }
      }

      return convert.toString();
   }

   public String getChineseAscii(String chineseString) {
      StringBuilder strBuf = new StringBuilder();
      byte[] bGBK = chineseString.getBytes();

      for(byte b : bGBK) {
         strBuf.append(Integer.toHexString(b & 255));
      }

      return strBuf.toString();
   }

   private static String getFirstLetters(String chineseString, HanyuPinyinCaseType caseType) {
      char[] chinesWords = chineseString.trim().toCharArray();
      StringBuilder hanyupinyin = new StringBuilder();
      HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
      defaultFormat.setCaseType(caseType);
      defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

      try {
         for(char word : chinesWords) {
            String str = String.valueOf(word);
            if (str.matches("[\u4e00-\u9fa5]+")) {
               hanyupinyin.append(PinyinHelper.toHanyuPinyinStringArray(word, defaultFormat)[0].charAt(0));
            } else if (str.matches("[0-9]+")) {
               hanyupinyin.append(word);
            } else if (str.matches("[a-zA-Z]+")) {
               hanyupinyin.append(word);
            } else {
               hanyupinyin.append(word);
            }
         }
      } catch (BadHanyuPinyinOutputFormatCombination e) {
         throw new PinyinException(e.getMessage());
      }

      return hanyupinyin.toString();
   }
}
