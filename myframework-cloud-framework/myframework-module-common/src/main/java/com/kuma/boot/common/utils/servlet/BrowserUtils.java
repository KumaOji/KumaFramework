/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 */
package com.kuma.boot.common.utils.servlet;

import com.kuma.boot.common.utils.servlet.BrowserType;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BrowserUtils {
    private static final String IE11 = "rv:11.0";
    private static final String IE10 = "MSIE 10.0";
    private static final String IE9 = "MSIE 9.0";
    private static final String IE8 = "MSIE 8.0";
    private static final String IE7 = "MSIE 7.0";
    private static final String IE6 = "MSIE 6.0";
    private static final String MAXTHON = "Maxthon";
    private static final String QQ = "QQBrowser";
    private static final String GREEN = "GreenBrowser";
    private static final String SE360 = "360SE";
    private static final String FIREFOX = "Firefox";
    private static final String OPERA = "Opera";
    private static final String CHROME = "Chrome";
    private static final String SAFARI = "Safari";
    private static final String OTHER = "\u5176\u5b83";
    private static final String CAMINO = "Camino";
    private static Map<String, String> langMap = new HashMap<String, String>();
    private static final String ZH = "zh";
    private static final String ZH_CN = "zh-cn";
    private static final String EN = "en";
    private static final String EN_US = "en";

    public static boolean isIe(HttpServletRequest request) {
        return request.getHeader("USER-AGENT").toLowerCase().indexOf("msie") > 0 || request.getHeader("USER-AGENT").toLowerCase().indexOf(IE11) > 0;
    }

    public static Double getIeVersion(HttpServletRequest request) {
        double version = 0.0;
        if (BrowserUtils.getBrowserType(request, IE11)) {
            version = 11.0;
        } else if (BrowserUtils.getBrowserType(request, IE10)) {
            version = 10.0;
        } else if (BrowserUtils.getBrowserType(request, IE9)) {
            version = 9.0;
        } else if (BrowserUtils.getBrowserType(request, IE8)) {
            version = 8.0;
        } else if (BrowserUtils.getBrowserType(request, IE7)) {
            version = 7.0;
        } else if (BrowserUtils.getBrowserType(request, IE6)) {
            version = 6.0;
        }
        return version;
    }

    public static BrowserType getBrowserType(HttpServletRequest request) {
        BrowserType browserType = null;
        if (BrowserUtils.getBrowserType(request, IE11)) {
            browserType = BrowserType.IE11;
        }
        if (BrowserUtils.getBrowserType(request, IE10)) {
            browserType = BrowserType.IE10;
        }
        if (BrowserUtils.getBrowserType(request, IE9)) {
            browserType = BrowserType.IE9;
        }
        if (BrowserUtils.getBrowserType(request, IE8)) {
            browserType = BrowserType.IE8;
        }
        if (BrowserUtils.getBrowserType(request, IE7)) {
            browserType = BrowserType.IE7;
        }
        if (BrowserUtils.getBrowserType(request, IE6)) {
            browserType = BrowserType.IE6;
        }
        if (BrowserUtils.getBrowserType(request, FIREFOX)) {
            browserType = BrowserType.Firefox;
        }
        if (BrowserUtils.getBrowserType(request, SAFARI)) {
            browserType = BrowserType.Safari;
        }
        if (BrowserUtils.getBrowserType(request, CHROME)) {
            browserType = BrowserType.Chrome;
        }
        if (BrowserUtils.getBrowserType(request, OPERA)) {
            browserType = BrowserType.Opera;
        }
        if (BrowserUtils.getBrowserType(request, CAMINO)) {
            browserType = BrowserType.Camino;
        }
        return browserType;
    }

    private static boolean getBrowserType(HttpServletRequest request, String brosertype) {
        return request.getHeader("USER-AGENT").toLowerCase().indexOf(brosertype) > 0;
    }

    public static String checkBrowse(HttpServletRequest request) {
        String userAgent = request.getHeader("USER-AGENT");
        if (BrowserUtils.regex(OPERA, userAgent)) {
            return OPERA;
        }
        if (BrowserUtils.regex(CHROME, userAgent)) {
            return CHROME;
        }
        if (BrowserUtils.regex(FIREFOX, userAgent)) {
            return FIREFOX;
        }
        if (BrowserUtils.regex(SAFARI, userAgent)) {
            return SAFARI;
        }
        if (BrowserUtils.regex(SE360, userAgent)) {
            return SE360;
        }
        if (BrowserUtils.regex(GREEN, userAgent)) {
            return GREEN;
        }
        if (BrowserUtils.regex(QQ, userAgent)) {
            return QQ;
        }
        if (BrowserUtils.regex(MAXTHON, userAgent)) {
            return MAXTHON;
        }
        if (BrowserUtils.regex(IE11, userAgent)) {
            return IE11;
        }
        if (BrowserUtils.regex(IE10, userAgent)) {
            return IE10;
        }
        if (BrowserUtils.regex(IE9, userAgent)) {
            return IE9;
        }
        if (BrowserUtils.regex(IE8, userAgent)) {
            return IE8;
        }
        if (BrowserUtils.regex(IE7, userAgent)) {
            return IE7;
        }
        if (BrowserUtils.regex(IE6, userAgent)) {
            return IE6;
        }
        return OTHER;
    }

    public static boolean regex(String regex, String str) {
        Pattern p = Pattern.compile(regex, 8);
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static String getBrowserLanguage(HttpServletRequest request) {
        String browserLang = request.getLocale().getLanguage();
        String browserLangCode = langMap.get(browserLang);
        if (browserLangCode == null) {
            browserLangCode = "en";
        }
        return browserLangCode;
    }

    public static boolean isDesktop(HttpServletRequest request) {
        return !BrowserUtils.isMobile(request);
    }

    public static boolean isMobile(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent").toLowerCase();
        String type = "(phone|pad|pod|iphone|ipod|ios|ipad|android|mobile|blackberry|iemobile|mqqbrowser|juc|fennec|wosbrowser|browserng|webos|symbian|windows phone)";
        Pattern pattern = Pattern.compile(type);
        return pattern.matcher(ua).find();
    }

    static {
        langMap.put(ZH, ZH_CN);
        langMap.put("en", "en");
    }
}

