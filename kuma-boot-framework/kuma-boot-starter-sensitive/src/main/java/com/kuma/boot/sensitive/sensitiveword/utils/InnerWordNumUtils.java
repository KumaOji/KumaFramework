package com.kuma.boot.sensitive.sensitiveword.utils;

import cn.hutool.core.map.MapUtil;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.sensitive.sensitiveword.api.IWordContext;
import com.kuma.boot.sensitive.sensitiveword.constant.enums.WordValidModeEnum;
import java.util.Map;

public final class InnerWordNumUtils {
   private static final String NUM_ONE = "⓪０零º₀⓿○１２３４５６７８９一二三四五六七八九壹贰叁肆伍陆柒捌玖¹²³⁴⁵⁶⁷⁸⁹₁₂₃₄₅₆₇₈₉①②③④⑤⑥⑦⑧⑨⑴⑵⑶⑷⑸⑹⑺⑻⑼⒈⒉⒊⒋⒌⒍⒎⒏⒐❶❷❸❹❺❻❼❽❾➀➁➂➃➄➅➆➇➈➊➋➌➍➎➏➐➑➒㈠㈡㈢㈣㈤㈥㈦㈧㈨⓵⓶⓷⓸⓹⓺⓻⓼⓽㊀㊁㊂㊃㊄㊅㊆㊇㊈ⅰⅱⅲⅳⅴⅵⅶⅷⅸⅠⅡⅢⅣⅤⅥⅦⅧⅨ";
   private static final String NUM_TWO = "0000000123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789";
   private static final Map<Character, Character> NUMBER_MAP = MapUtil.newHashMap("⓪０零º₀⓿○１２３４５６７８９一二三四五六七八九壹贰叁肆伍陆柒捌玖¹²³⁴⁵⁶⁷⁸⁹₁₂₃₄₅₆₇₈₉①②③④⑤⑥⑦⑧⑨⑴⑵⑶⑷⑸⑹⑺⑻⑼⒈⒉⒊⒋⒌⒍⒎⒏⒐❶❷❸❹❺❻❼❽❾➀➁➂➃➄➅➆➇➈➊➋➌➍➎➏➐➑➒㈠㈡㈢㈣㈤㈥㈦㈧㈨⓵⓶⓷⓸⓹⓺⓻⓼⓽㊀㊁㊂㊃㊄㊅㊆㊇㊈ⅰⅱⅲⅳⅴⅵⅶⅷⅸⅠⅡⅢⅣⅤⅥⅦⅧⅨ".length());

   private InnerWordNumUtils() {
   }

   public static Character getMappingChar(final Character character) {
      Character mapChar = (Character)NUMBER_MAP.get(character);
      return ObjectUtils.isNotNull(mapChar) ? mapChar : character;
   }

   public static String getMappingString(final String string) {
      if (StringUtils.isEmpty(string)) {
         return string;
      } else {
         char[] chars = string.toCharArray();
         StringBuilder stringBuilder = new StringBuilder(chars.length);

         for(char c : chars) {
            char mapChar = getMappingChar(c);
            stringBuilder.append(mapChar);
         }

         return stringBuilder.toString();
      }
   }

   private int getSensitiveNumber(final String txt, final int beginIndex, final WordValidModeEnum wordValidModeEnum, final IWordContext context) {
      return 0;
   }

   static {
      int size = "⓪０零º₀⓿○１２３４５６７８９一二三四五六七八九壹贰叁肆伍陆柒捌玖¹²³⁴⁵⁶⁷⁸⁹₁₂₃₄₅₆₇₈₉①②③④⑤⑥⑦⑧⑨⑴⑵⑶⑷⑸⑹⑺⑻⑼⒈⒉⒊⒋⒌⒍⒎⒏⒐❶❷❸❹❺❻❼❽❾➀➁➂➃➄➅➆➇➈➊➋➌➍➎➏➐➑➒㈠㈡㈢㈣㈤㈥㈦㈧㈨⓵⓶⓷⓸⓹⓺⓻⓼⓽㊀㊁㊂㊃㊄㊅㊆㊇㊈ⅰⅱⅲⅳⅴⅵⅶⅷⅸⅠⅡⅢⅣⅤⅥⅦⅧⅨ".length();

      for(int i = 0; i < size; ++i) {
         NUMBER_MAP.put("⓪０零º₀⓿○１２３４５６７８９一二三四五六七八九壹贰叁肆伍陆柒捌玖¹²³⁴⁵⁶⁷⁸⁹₁₂₃₄₅₆₇₈₉①②③④⑤⑥⑦⑧⑨⑴⑵⑶⑷⑸⑹⑺⑻⑼⒈⒉⒊⒋⒌⒍⒎⒏⒐❶❷❸❹❺❻❼❽❾➀➁➂➃➄➅➆➇➈➊➋➌➍➎➏➐➑➒㈠㈡㈢㈣㈤㈥㈦㈧㈨⓵⓶⓷⓸⓹⓺⓻⓼⓽㊀㊁㊂㊃㊄㊅㊆㊇㊈ⅰⅱⅲⅳⅴⅵⅶⅷⅸⅠⅡⅢⅣⅤⅥⅦⅧⅨ".charAt(i), "0000000123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789123456789".charAt(i));
      }

   }
}
