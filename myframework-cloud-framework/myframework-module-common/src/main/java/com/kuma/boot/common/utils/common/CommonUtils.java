/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CommonUtils {
    public static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,20}$";
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,20}$";
    public static final String REGEX_MOBILE = "^1\\d{10}$";
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],*$";
    public static final String REGEX_ID_CARD = "(^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}$)";
    public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
    public static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
    public static final String REGEX_LOGIN_PASSWORD = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$";
    public static final String REGEX_FORMAT_DATE = "^((\\d{2}(([02468][048])|([13579][26]))[\\-]((((0?[13578])|(1[02]))[\\-]{1}((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-]{1}((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-]{1}((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-]((((0?[13578])|(1[02]))[\\-]{1}((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-]((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-]((0?[1-9])|(1[0-9])|(2[0-8]))))))";
    public static final String REGEX_FORMAT_MONEY = "^(0|([1-9][0-9]*)|(([0]\\.\\d{1,2}|[1-9][0-9]*\\.\\d{1,2})))$";
    public static final String REGEX_FORMAT_TIME = "0[0-9]:[0-5][0-9]|1[0-9]:[0-5][0-9]|2[0-3]:[0-5][0-9]";
    private static final Pattern REGEX = Pattern.compile("[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~\uff01@#\uffe5%\u2026\u2026&*\uff08\uff09\u2014\u2014+|{}\u3010\u3011\u2018\uff1b\uff1a\u201d\u201c\u2019\u3002\uff0c\u3001\uff1f]|\n|\r|\t|[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]");

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static String toLikeStr(String str) {
        return "%" + str + "%";
    }

    public static String toLikeStrLt(String str) {
        return "%" + str;
    }

    public static String toLikeStrRt(String str) {
        return str + "%";
    }

    public static String replaceTag(String input) {
        if (!CommonUtils.hasSpecialChars(input)) {
            return input;
        }
        StringBuilder filtered = new StringBuilder(input.length());
        block6: for (int i = 0; i <= input.length() - 1; ++i) {
            char c = input.charAt(i);
            switch (c) {
                case '<': {
                    filtered.append("&lt;");
                    continue block6;
                }
                case '>': {
                    filtered.append("&gt;");
                    continue block6;
                }
                case '\"': {
                    filtered.append("&quot;");
                    continue block6;
                }
                case '&': {
                    filtered.append("&amp;");
                    continue block6;
                }
                default: {
                    filtered.append(c);
                }
            }
        }
        return filtered.toString();
    }

    public static boolean hasSpecialChars(String input) {
        boolean flag = false;
        if (input != null && input.length() > 0) {
            for (int i = 0; i < input.length(); ++i) {
                char c = input.charAt(i);
                switch (c) {
                    case '>': {
                        flag = true;
                        break;
                    }
                    case '<': {
                        flag = true;
                        break;
                    }
                    case '\"': {
                        flag = true;
                        break;
                    }
                    case '&': {
                        flag = true;
                    }
                }
                if (flag) break;
            }
        }
        return flag;
    }

    public static String createUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }

    public static boolean isBlank(String obj) {
        if (obj == null) {
            return true;
        }
        return obj.isEmpty();
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isEmpty(Object ... objs) {
        if (objs == null) {
            return true;
        }
        int var2 = objs.length;
        for (Object obj : objs) {
            if (obj == null || "".equals(obj)) continue;
            return false;
        }
        return true;
    }

    public static boolean isNotEmpty(Object ... objs) {
        if (objs == null) {
            return false;
        }
        int var2 = objs.length;
        for (Object obj : objs) {
            if (obj != null && !"".equals(obj)) continue;
            return false;
        }
        return true;
    }

    public static boolean isLengthRight(Integer[] lens, String str) {
        if (str == null || lens.length != 2) {
            return true;
        }
        return str.length() > lens[1] || str.length() < lens[0];
    }

    public static boolean isUsername(String username) {
        return Pattern.matches(REGEX_USERNAME, username);
    }

    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    public static boolean isChinese(String chinese) {
        return Pattern.matches(REGEX_CHINESE, chinese);
    }

    public static boolean isIDCard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }

    public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url);
    }

    public static boolean isIPAddr(String ipAddr) {
        return Pattern.matches(REGEX_IP_ADDR, ipAddr);
    }

    public static boolean isTime(String time) {
        return Pattern.matches(REGEX_FORMAT_TIME, time);
    }

    public static boolean isPasswordOk(String password) {
        return Pattern.matches(REGEX_LOGIN_PASSWORD, password);
    }

    public static String getRoundNum() {
        Random rad = new Random();
        String result = "" + rad.nextInt(1000000);
        if (result.length() != 6) {
            return CommonUtils.getRoundNum();
        }
        return result;
    }

    public static int dayForWeek(Date pTime) throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(pTime);
        int dayForWeek = 0;
        dayForWeek = c.get(7) == 1 ? 7 : c.get(7) - 1;
        return dayForWeek;
    }

    public static boolean validatorIdCardNo(String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }
        String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)";
        boolean matches = id.matches(regularExpression);
        if (matches && id.length() == 18) {
            try {
                char[] charArray = id.toCharArray();
                int[] idCardWi = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                String[] idCardY = new String[]{"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                int sum = 0;
                for (int i = 0; i < idCardWi.length; ++i) {
                    int current = Integer.parseInt(String.valueOf(charArray[i]));
                    int count = current * idCardWi[i];
                    sum += count;
                }
                char idCardLast = charArray[17];
                int idCardMod = sum % 11;
                return idCardY[idCardMod].equalsIgnoreCase(String.valueOf(idCardLast));
            }
            catch (Exception e) {
                LogUtils.error(e);
                return false;
            }
        }
        return matches;
    }

    public static boolean isFormatDate(String date) {
        Pattern pat = Pattern.compile(REGEX_FORMAT_DATE);
        Matcher mat = pat.matcher(date);
        return mat.matches();
    }

    public static int dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        int[] weekDays = new int[]{7, 1, 2, 3, 4, 5, 6};
        Calendar cal = Calendar.getInstance();
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        }
        catch (ParseException e) {
            LogUtils.error(e);
        }
        int w = cal.get(7) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    public static boolean isMoney(String str) {
        Pattern pattern = Pattern.compile(REGEX_FORMAT_MONEY);
        Matcher isMoney = pattern.matcher(str);
        return isMoney.matches();
    }

    public static String getEncryptIdCard(String idcard) {
        if (idcard.length() == 18) {
            idcard = idcard.replaceAll("(\\d{10})\\d{4}(\\S{4})", "$1****$2");
        }
        return idcard;
    }

    public static String getEncryptPhone(String phone) {
        if (phone.length() == 11) {
            phone = phone.replaceAll("(\\d{3})\\d{4}(\\S{4})", "$1****$2");
        }
        return phone;
    }

    public static String formatToNumber(BigDecimal obj) {
        DecimalFormat df = new DecimalFormat("#.00");
        if (obj.compareTo(BigDecimal.ZERO) == 0) {
            return "0.00";
        }
        if (obj.compareTo(BigDecimal.ZERO) > 0 && obj.compareTo(new BigDecimal(1)) < 0) {
            return "0" + df.format(obj);
        }
        return df.format(obj);
    }

    public static String formatToString(BigDecimal bd) {
        bd = bd.setScale(2, RoundingMode.DOWN);
        return new DecimalFormat("0.00#").format(bd);
    }

    public static String roundByScale(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");
        }
        if (scale == 0) {
            return new DecimalFormat("0").format(v);
        }
        Object formatStr = "0.";
        for (int i = 0; i < scale; ++i) {
            formatStr = (String)formatStr + "0";
        }
        return new DecimalFormat((String)formatStr).format(v);
    }

    public static String getSqlWhereIn(String str, String regex) {
        String[] arr = str.split(regex);
        StringBuilder stringBuffer = new StringBuilder();
        int length = arr.length;
        for (int i = 0; i < length; ++i) {
            if (i == length - 1) {
                stringBuffer.append("'").append(arr[i]).append("'");
                continue;
            }
            stringBuffer.append("'").append(arr[i]).append("',");
        }
        return stringBuffer.toString();
    }

    public static boolean canshu(String name) {
        Matcher m = REGEX.matcher(name);
        return m.find();
    }
}

