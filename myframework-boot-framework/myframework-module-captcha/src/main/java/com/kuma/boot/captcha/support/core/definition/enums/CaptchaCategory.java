/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonFormat
 *  com.fasterxml.jackson.annotation.JsonFormat$Shape
 *  com.google.common.collect.ImmutableMap
 *  io.swagger.v3.oas.annotations.media.Schema
 */
package com.kuma.boot.captcha.support.core.definition.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Schema(title="\u9a8c\u8bc1\u7801\u7c7b\u522b")
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum CaptchaCategory {
    JIGSAW("JIGSAW", "\u6ed1\u5757\u62fc\u56fe\u9a8c\u8bc1\u7801"),
    WORD_CLICK("WORD_CLICK", "\u6587\u5b57\u70b9\u9009\u9a8c\u8bc1\u7801"),
    ARITHMETIC("ARITHMETIC", "\u7b97\u6570\u7c7b\u578b\u9a8c\u8bc1\u7801"),
    CHINESE("CHINESE", "\u4e2d\u6587\u7c7b\u578b\u9a8c\u8bc1\u7801"),
    CHINESE_GIF("CHINESE_GIF", "\u4e2d\u6587GIF\u7c7b\u578b\u9a8c\u8bc1\u7801"),
    SPEC_GIF("SPEC_GIF", "GIF\u7c7b\u578b\u9a8c\u8bc1\u7801"),
    SPEC("SPEC", "PNG\u7c7b\u578b\u9a8c\u8bc1\u7801"),
    HUTOOL_LINE("HUTOOL_LINE", "Hutool\u7ebf\u6bb5\u5e72\u6270\u9a8c\u8bc1\u7801"),
    HUTOOL_CIRCLE("HUTOOL_CIRCLE", "Hutool\u5706\u5708\u5e72\u6270\u9a8c\u8bc1\u7801"),
    HUTOOL_SHEAR("HUTOOL_SHEAR", "Hutool\u626d\u66f2\u5e72\u6270\u9a8c\u8bc1\u7801"),
    HUTOOL_GIF("HUTOOL_GIF", "Hutool GIF\u9a8c\u8bc1\u7801");

    public static final String JIGSAW_CAPTCHA = "JIGSAW";
    public static final String WORD_CLICK_CAPTCHA = "WORD_CLICK";
    public static final String ARITHMETIC_CAPTCHA = "ARITHMETIC";
    public static final String CHINESE_CAPTCHA = "CHINESE";
    public static final String CHINESE_GIF_CAPTCHA = "CHINESE_GIF";
    public static final String SPEC_CAPTCHA = "SPEC";
    public static final String SPEC_GIF_CAPTCHA = "SPEC_GIF";
    public static final String HUTOOL_LINE_CAPTCHA = "HUTOOL_LINE";
    public static final String HUTOOL_CIRCLE_CAPTCHA = "HUTOOL_CIRCLE";
    public static final String HUTOOL_SHEAR_CAPTCHA = "HUTOOL_SHEAR";
    public static final String HUTOOL_GIF_CAPTCHA = "HUTOOL_GIF";
    private static final Map<String, CaptchaCategory> INDEX_MAP;
    private static final List<Map<String, Object>> JSON_STRUCT;
    @Schema(title="\u5e38\u91cf\u503c")
    private final String constant;
    @Schema(title="\u6587\u5b57")
    private final String description;

    private CaptchaCategory(String constant, String description) {
        this.constant = constant;
        this.description = description;
    }

    public String getConstant() {
        return this.constant;
    }

    public String getDescription() {
        return this.description;
    }

    public static CaptchaCategory getCaptchaCategory(String name) {
        return INDEX_MAP.get(name);
    }

    public static List<Map<String, Object>> getJsonStruct() {
        return JSON_STRUCT;
    }

    static {
        INDEX_MAP = new HashMap<String, CaptchaCategory>();
        JSON_STRUCT = new ArrayList<Map<String, Object>>();
        for (CaptchaCategory captchaCategory : CaptchaCategory.values()) {
            INDEX_MAP.put(captchaCategory.getConstant(), captchaCategory);
            JSON_STRUCT.add(captchaCategory.ordinal(), (Map<String, Object>)ImmutableMap.builder().put((Object)"value", (Object)captchaCategory.ordinal()).put((Object)"key", (Object)captchaCategory.name()).put((Object)"text", (Object)captchaCategory.getDescription()).build());
        }
    }
}

