/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.io.IoUtil
 *  cn.hutool.core.util.EscapeUtil
 *  cn.hutool.core.util.ReUtil
 *  cn.hutool.core.util.StrUtil
 */
package com.kuma.boot.common.support.html;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.support.html.HtmlFilter;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class HtmlUtil {
    public static final Pattern RE_HTML_MARK = Pattern.compile("(<[^<]*?>)|(<\\s*?/[^<]*?>)|(<[^<]*?/\\s*?>)", 2);
    public static final String RE_HTML_EMPTY_MARK = "<(\\w+)([^>]*)>\\s*</\\1>";
    public static final Pattern RE_SCRIPT = Pattern.compile("<\\s*?script[^>]*?>.*?<\\s*?/\\s*?script\\s*?>", 2);
    public static final Pattern META_CHARSET_PATTERN = Pattern.compile("<meta[^>]*?charset\\s*=\\s*['\"]?([a-z0-9-]*)", 2);
    private static final char[][] TEXT = new char[256][];

    public static String escape(String text) {
        return HtmlUtil.encode(text);
    }

    public static String unescape(String htmlStr) {
        if (StrUtil.isBlank((CharSequence)htmlStr)) {
            return htmlStr;
        }
        return EscapeUtil.unescapeHtml4((CharSequence)htmlStr);
    }

    public static String cleanHtmlTag(String content) {
        return ReUtil.replaceAll((CharSequence)content, (Pattern)RE_HTML_MARK, (String)"");
    }

    public static String cleanEmptyTag(String content) {
        return content.replaceAll(RE_HTML_EMPTY_MARK, "");
    }

    public static String removeScriptTag(String content) {
        return ReUtil.replaceAll((CharSequence)content, (Pattern)RE_SCRIPT, (String)"");
    }

    public static String removeHtmlTag(String content, String ... tagNames) {
        return HtmlUtil.removeHtmlTag(content, true, tagNames);
    }

    public static String unwrapHtmlTag(String content, String ... tagNames) {
        return HtmlUtil.removeHtmlTag(content, false, tagNames);
    }

    public static String removeHtmlTag(String content, boolean withTagContent, String ... tagNames) {
        for (String tagName : tagNames) {
            if (StrUtil.isBlank((CharSequence)tagName)) continue;
            tagName = tagName.trim();
            String regex = withTagContent ? StrUtil.format((CharSequence)"(?i)<{}(\\s+[^>]*?)?/?>(.*?</{}>)?", (Object[])new Object[]{tagName, tagName}) : StrUtil.format((CharSequence)"(?i)<{}(\\s+[^>]*?)?/?>|</?{}>", (Object[])new Object[]{tagName, tagName});
            content = ReUtil.delAll((String)regex, (CharSequence)content);
        }
        return content;
    }

    public static String removeHtmlAttr(String content, String ... attrs) {
        for (String attr : attrs) {
            String regex = StrUtil.format((CharSequence)"(?i)(\\s*{}\\s*=\\s*)(([\"][^\"]+?[\"])|([^>]+?\\s*(?=\\s|>)))", (Object[])new Object[]{attr});
            content = content.replaceAll(regex, "");
        }
        content = ReUtil.replaceAll((CharSequence)content, (String)"\\s+(>|/>)", (String)"$1");
        return content;
    }

    public static String removeAllHtmlAttr(String content, String ... tagNames) {
        for (String tagName : tagNames) {
            String regex = StrUtil.format((CharSequence)"(?i)<{}[^>]*?>", (Object[])new Object[]{tagName});
            content = content.replaceAll(regex, StrUtil.format((CharSequence)"<{}>", (Object[])new Object[]{tagName}));
        }
        return content;
    }

    private static String encode(String text) {
        int len;
        if (text == null || (len = text.length()) == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(len + (len >> 2));
        for (int i = 0; i < len; ++i) {
            char c = text.charAt(i);
            if (c < '\u0100') {
                buffer.append(TEXT[c]);
                continue;
            }
            buffer.append(c);
        }
        return buffer.toString();
    }

    public static String filter(String htmlContent) {
        return new HtmlFilter().filter(htmlContent);
    }

    public static String getString(InputStream in, Charset charset, boolean isGetCharsetFromContent) {
        byte[] contentBytes = IoUtil.readBytes((InputStream)in);
        return HtmlUtil.getString(contentBytes, charset, isGetCharsetFromContent);
    }

    public static String getString(byte[] contentBytes, Charset charset, boolean isGetCharsetFromContent) {
        String charsetInContentStr;
        if (null == contentBytes) {
            return null;
        }
        if (null == charset) {
            charset = StandardCharsets.UTF_8;
        }
        String content = new String(contentBytes, charset);
        if (isGetCharsetFromContent && StrUtil.isNotBlank((CharSequence)(charsetInContentStr = ReUtil.get((Pattern)META_CHARSET_PATTERN, (CharSequence)content, (int)1)))) {
            Charset charsetInContent;
            block7: {
                charsetInContent = null;
                try {
                    charsetInContent = Charset.forName(charsetInContentStr);
                }
                catch (Exception e) {
                    if (StrUtil.containsIgnoreCase((CharSequence)charsetInContentStr, (CharSequence)"utf-8") || StrUtil.containsIgnoreCase((CharSequence)charsetInContentStr, (CharSequence)"utf8")) {
                        charsetInContent = StandardCharsets.UTF_8;
                    }
                    if (!StrUtil.containsIgnoreCase((CharSequence)charsetInContentStr, (CharSequence)"gbk")) break block7;
                    charsetInContent = StandardCharsets.ISO_8859_1;
                }
            }
            if (null != charsetInContent && !charset.equals(charsetInContent)) {
                content = new String(contentBytes, charsetInContent);
            }
        }
        return content;
    }

    static {
        for (int i = 0; i < 256; ++i) {
            HtmlUtil.TEXT[i] = new char[]{(char)i};
        }
        HtmlUtil.TEXT[39] = "&#039;".toCharArray();
        HtmlUtil.TEXT[34] = "&quot;".toCharArray();
        HtmlUtil.TEXT[38] = "&amp;".toCharArray();
        HtmlUtil.TEXT[60] = "&lt;".toCharArray();
        HtmlUtil.TEXT[62] = "&gt;".toCharArray();
        HtmlUtil.TEXT[160] = "&nbsp;".toCharArray();
    }
}

