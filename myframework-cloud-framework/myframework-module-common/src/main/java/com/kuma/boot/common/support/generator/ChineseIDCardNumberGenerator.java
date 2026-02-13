/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.date.DateTime
 *  cn.hutool.core.util.RandomUtil
 *  com.google.common.collect.Maps
 */
package com.kuma.boot.common.support.generator;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Maps;
import com.kuma.boot.common.support.generator.ChineseAreaList;
import com.kuma.boot.common.support.generator.base.GenericGenerator;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChineseIDCardNumberGenerator
extends GenericGenerator {
    private static final GenericGenerator INSTANCE = new ChineseIDCardNumberGenerator();

    private ChineseIDCardNumberGenerator() {
    }

    public static GenericGenerator getInstance() {
        return INSTANCE;
    }

    public static String generateIssueOrg() {
        return ChineseAreaList.cityNameList.get(RandomUtil.randomInt((int)0, (int)ChineseAreaList.cityNameList.size())) + "\u516c\u5b89\u5c40\u67d0\u67d0\u5206\u5c40";
    }

    public static String generateValidPeriod() {
        DateTime beginDate = new DateTime(ChineseIDCardNumberGenerator.randomDate());
        String formater = "yyyyMMdd";
        beginDate.toCalendar().set(1, beginDate.year() + 20);
        return beginDate.toString(formater) + "-" + beginDate.toString(formater);
    }

    @Override
    public String generate() {
        Map<String, String> code = ChineseIDCardNumberGenerator.getAreaCode();
        String areaCode = code.keySet().toArray(new String[0])[RandomUtil.randomInt((int)0, (int)code.size())] + StringUtils.leftPad("" + (RandomUtil.randomInt((int)0, (int)9998) + 1), 4, "0");
        String birthday = new SimpleDateFormat("yyyyMMdd").format(ChineseIDCardNumberGenerator.randomDate());
        String randomCode = String.valueOf(1000 + RandomUtil.randomInt((int)0, (int)999)).substring(1);
        String pre = areaCode + birthday + randomCode;
        String verifyCode = ChineseIDCardNumberGenerator.getVerifyCode(pre);
        return pre + verifyCode;
    }

    public static Date randomDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, 1, 1);
        long earlierDate = calendar.getTime().getTime();
        calendar.set(2000, 1, 1);
        long laterDate = calendar.getTime().getTime();
        long chosenDate = RandomUtil.randomLong((long)earlierDate, (long)laterDate);
        return new Date(chosenDate);
    }

    private static String getVerifyCode(String cardId) {
        String[] valCodeArr = new String[]{"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        String[] wi = new String[]{"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
        int tmp = 0;
        for (int i = 0; i < wi.length; ++i) {
            tmp += Integer.parseInt(String.valueOf(cardId.charAt(i))) * Integer.parseInt(wi[i]);
        }
        int modValue = tmp % 11;
        return valCodeArr[modValue];
    }

    private static Map<String, String> getAreaCode() {
        HashMap map = Maps.newHashMap();
        map.put("11", "\u5317\u4eac");
        map.put("12", "\u5929\u6d25");
        map.put("13", "\u6cb3\u5317");
        map.put("14", "\u5c71\u897f");
        map.put("15", "\u5185\u8499\u53e4");
        map.put("21", "\u8fbd\u5b81");
        map.put("22", "\u5409\u6797");
        map.put("23", "\u9ed1\u9f99\u6c5f");
        map.put("31", "\u4e0a\u6d77");
        map.put("32", "\u6c5f\u82cf");
        map.put("33", "\u6d59\u6c5f");
        map.put("34", "\u5b89\u5fbd");
        map.put("35", "\u798f\u5efa");
        map.put("36", "\u6c5f\u897f");
        map.put("37", "\u5c71\u4e1c");
        map.put("41", "\u6cb3\u5357");
        map.put("42", "\u6e56\u5317");
        map.put("43", "\u6e56\u5357");
        map.put("44", "\u5e7f\u4e1c");
        map.put("45", "\u5e7f\u897f");
        map.put("46", "\u6d77\u5357");
        map.put("50", "\u91cd\u5e86");
        map.put("51", "\u56db\u5ddd");
        map.put("52", "\u8d35\u5dde");
        map.put("53", "\u4e91\u5357");
        map.put("54", "\u897f\u85cf");
        map.put("61", "\u9655\u897f");
        map.put("62", "\u7518\u8083");
        map.put("63", "\u9752\u6d77");
        map.put("64", "\u5b81\u590f");
        map.put("65", "\u65b0\u7586");
        map.put("71", "\u53f0\u6e7e");
        map.put("81", "\u9999\u6e2f");
        map.put("82", "\u6fb3\u95e8");
        map.put("91", "\u56fd\u5916");
        return map;
    }
}

