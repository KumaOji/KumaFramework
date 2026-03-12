//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.support.core.definition.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Schema(
        title = "验证码类别"
)
@JsonFormat(
        shape = Shape.OBJECT
)
public enum CaptchaCategory {
    JIGSAW("JIGSAW", "滑块拼图验证码"),
    WORD_CLICK("WORD_CLICK", "文字点选验证码"),
    ARITHMETIC("ARITHMETIC", "算数类型验证码"),
    CHINESE("CHINESE", "中文类型验证码"),
    CHINESE_GIF("CHINESE_GIF", "中文GIF类型验证码"),
    SPEC_GIF("SPEC_GIF", "GIF类型验证码"),
    SPEC("SPEC", "PNG类型验证码"),
    HUTOOL_LINE("HUTOOL_LINE", "Hutool线段干扰验证码"),
    HUTOOL_CIRCLE("HUTOOL_CIRCLE", "Hutool圆圈干扰验证码"),
    HUTOOL_SHEAR("HUTOOL_SHEAR", "Hutool扭曲干扰验证码"),
    HUTOOL_GIF("HUTOOL_GIF", "Hutool GIF验证码");

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
    private static final Map<String, CaptchaCategory> INDEX_MAP = new HashMap();
    private static final List<Map<String, Object>> JSON_STRUCT = new ArrayList();
    @Schema(
            title = "常量值"
    )
    private final String constant;
    @Schema(
            title = "文字"
    )
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
        return (CaptchaCategory)INDEX_MAP.get(name);
    }

    public static List<Map<String, Object>> getJsonStruct() {
        return JSON_STRUCT;
    }

    static {
        for(CaptchaCategory captchaCategory : values()) {
            INDEX_MAP.put(captchaCategory.getConstant(), captchaCategory);
            JSON_STRUCT.add(captchaCategory.ordinal(), ImmutableMap.builder().put("value", captchaCategory.ordinal()).put("key", captchaCategory.name()).put("text", captchaCategory.getDescription()).build());
        }

    }
}
