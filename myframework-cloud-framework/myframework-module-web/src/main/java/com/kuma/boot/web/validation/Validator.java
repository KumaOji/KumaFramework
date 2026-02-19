/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.date.DateUtil
 *  cn.hutool.core.exceptions.ValidateException
 *  cn.hutool.core.lang.Console
 *  cn.hutool.core.lang.Validator
 *  cn.hutool.core.util.NumberUtil
 *  cn.hutool.core.util.ObjUtil
 *  cn.hutool.core.util.StrUtil
 *  com.alibaba.fastjson2.JSONArray
 *  com.alibaba.fastjson2.JSONObject
 *  com.alibaba.fastjson2.JSONWriter$Feature
 *  com.kuma.boot.common.exception.BusinessException
 *  com.kuma.boot.common.utils.context.ContextUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.validation.Validator
 */
package com.kuma.boot.web.validation;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

public class Validator {
    private Object param;
    private static final String NOT_NULL_HINT_MSG = "\u53c2\u6570 {} \u5fc5\u987b\u4e0d\u4e3a null";
    private static final String NOT_EMPTY_HINT_MSG = "\u53c2\u6570 {} \u5fc5\u987b\u4e0d\u4e3aempty(null \u6216 \"\")";
    private static final String ASSERT_TRUE_HINT_MSG = "\u53c2\u6570 {} \u5fc5\u987b\u4e3a true";
    private static final String ASSERT_FALSE_HINT_MSG = "\u53c2\u6570 {} \u5fc5\u987b\u4e3a false";
    private static final String DIGITS_HINT_MSG = "\u53c2\u6570 {} \u5fc5\u987b\u662f\u4e00\u4e2a\u6570\u5b57\uff0c\u5176\u503c\u5fc5\u987b\u5728 {} - {} \u4e4b\u95f4\uff08\u5305\u542b\uff09";
    private static final String MAX_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u80fd\u8d85\u8fc7\u6700\u5927\u503c\uff1a{}";
    private static final String MIN_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u80fd\u4f4e\u4e8e\u6700\u5c0f\u503c\uff1a{}";
    private static final String LENGTH_HINT_MSG = "\u53c2\u6570 {} \u957f\u5ea6\u5fc5\u987b\u5728 {} - {} \u4e4b\u95f4\uff08\u5305\u542b\uff09";
    private static final String CHINESE_HINT_MSG = "\u53c2\u6570 {} \u4e2d\u6587\u6821\u9a8c\u4e0d\u901a\u8fc7";
    private static final String ENGLISH_HINT_MSG = "\u53c2\u6570 {} \u82f1\u6587\u6821\u9a8c\u4e0d\u901a\u8fc7";
    private static final String BIRTHDAY_HINT_MSG = "\u53c2\u6570 {} \u751f\u65e5\u6821\u9a8c\u4e0d\u901a\u8fc7";
    private static final String CELLPHONE_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u662f\u4e00\u4e2a\u5408\u6cd5\u7684\u624b\u673a\u53f7\u7801";
    private static final String EMAIL_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u662f\u4e00\u4e2a\u5408\u6cd5\u7684\u90ae\u7bb1\u683c\u5f0f";
    private static final String ID_CARD_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u662f\u4e00\u4e2a\u5408\u6cd5\u7684\u8eab\u4efd\u8bc1\u53f7\u7801";
    private static final String PLATE_NUMBER_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u662f\u4e00\u4e2a\u5408\u6cd5\u7684\u4e2d\u56fd\u8f66\u724c\u53f7\u7801";
    private static final String UUID_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u662f\u4e00\u4e2a\u5408\u6cd5\u7684UUID";
    private static final String URL_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u662f\u4e00\u4e2a\u5408\u6cd5\u7684URL";
    private static final String IPV4_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u662f\u4e00\u4e2a\u5408\u6cd5\u7684IPV4\u5730\u5740";
    private static final String IPV6_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u662f\u4e00\u4e2a\u5408\u6cd5\u7684IPV6\u5730\u5740";
    private static final String MAC_ADDRESS_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u662f\u4e00\u4e2a\u5408\u6cd5\u7684MAC\u5730\u5740";
    private static final String CAR_DRIVING_LICENCE_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u662f\u4e00\u4e2a\u5408\u6cd5\u7684\u9a7e\u9a76\u8bc1\uff08\u4ec5\u9650\uff1a\u4e2d\u56fd\u9a7e\u9a76\u8bc1\u6863\u6848\u7f16\u53f7\uff09";
    private static final String CAR_VIN_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u662f\u4e00\u4e2a\u5408\u6cd5\u7684\u8f66\u67b6\u53f7";
    private static final String CREDIT_CODE_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u662f\u4e00\u4e2a\u5408\u6cd5\u7684\u7edf\u4e00\u793e\u4f1a\u4fe1\u7528\u4ee3\u7801";
    private static final String ZIP_CODE_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u662f\u4e00\u4e2a\u5408\u6cd5\u7684\u90ae\u653f\u7f16\u7801\uff08\u4e2d\u56fd\uff09";
    private static final String REGEX_HINT_MSG = "\u53c2\u6570 {} \u4e0d\u6ee1\u8db3\u6b63\u5219\u8868\u8fbe\u5f0f\uff1a{}";

    public Validator param(Object param) {
        this.param = param;
        return this;
    }

    public static Validator getValidatorAndSetParam(Object param) {
        return ((Validator)ContextUtils.getBean(Validator.class, (boolean)true)).param(param);
    }

    public Validator notNull(String paramName) {
        cn.hutool.core.lang.Validator.validateNotNull((Object)this.param, (String)StrUtil.format((CharSequence)NOT_NULL_HINT_MSG, (Object[])new Object[]{paramName}), (Object[])new Object[0]);
        return this;
    }

    public Validator notEmpty(String paramName) {
        cn.hutool.core.lang.Validator.validateNotEmpty((Object)this.param, (String)StrUtil.format((CharSequence)NOT_EMPTY_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator assertTrue(String paramName) {
        cn.hutool.core.lang.Validator.validateTrue((boolean)((Boolean)this.param), (String)StrUtil.format((CharSequence)ASSERT_TRUE_HINT_MSG, (Object[])new Object[]{paramName}), (Object[])new Object[0]);
        return this;
    }

    public Validator assertFalse(String paramName) {
        cn.hutool.core.lang.Validator.validateFalse((boolean)((Boolean)this.param), (String)StrUtil.format((CharSequence)ASSERT_FALSE_HINT_MSG, (Object[])new Object[]{paramName}), (Object[])new Object[0]);
        return this;
    }

    public Validator digits(Number min, Number max, String paramName) {
        cn.hutool.core.lang.Validator.validateBetween((Number)((Number)this.param), (Number)min, (Number)max, (String)StrUtil.format((CharSequence)DIGITS_HINT_MSG, (Object[])new Object[]{paramName, min, max}));
        return this;
    }

    public Validator max(Number max, String paramName) {
        BigDecimal bigNum2;
        BigDecimal bigNum1 = NumberUtil.toBigDecimal((Number)((Number)this.param));
        if (bigNum1.compareTo(bigNum2 = NumberUtil.toBigDecimal((Number)max)) > 0) {
            throw new ValidateException(StrUtil.format((CharSequence)MAX_HINT_MSG, (Object[])new Object[]{paramName, max}));
        }
        return this;
    }

    public Validator min(Number min, String paramName) {
        BigDecimal bigNum2;
        BigDecimal bigNum1 = NumberUtil.toBigDecimal((Number)((Number)this.param));
        if (bigNum1.compareTo(bigNum2 = NumberUtil.toBigDecimal((Number)min)) > 0) {
            throw new ValidateException(StrUtil.format((CharSequence)MIN_HINT_MSG, (Object[])new Object[]{paramName, min}));
        }
        return this;
    }

    public Validator length(int min, int max, String paramName) {
        int length = ObjUtil.length((Object)this.param);
        if (length < min || length > max) {
            throw new ValidateException(StrUtil.format((CharSequence)LENGTH_HINT_MSG, (Object[])new Object[]{paramName, min, max}));
        }
        return this;
    }

    public Validator chinese(String paramName) {
        cn.hutool.core.lang.Validator.validateChinese((CharSequence)((CharSequence)this.param), (String)StrUtil.format((CharSequence)CHINESE_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator english(String paramName) {
        cn.hutool.core.lang.Validator.validateWord((CharSequence)((CharSequence)this.param), (String)StrUtil.format((CharSequence)ENGLISH_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator birthday(String paramName) {
        String date = null;
        if (this.param instanceof String) {
            date = (String)this.param;
        } else if (this.param instanceof Date) {
            date = DateUtil.formatDate((Date)((Date)this.param));
        } else if (this.param instanceof LocalDate || this.param instanceof LocalDateTime) {
            date = DateUtil.formatLocalDateTime((LocalDateTime)((LocalDateTime)this.param));
        } else {
            throw new BusinessException(StrUtil.format((CharSequence)"\u53c2\u6570 {} \u672a\u77e5\u7c7b\u578b\uff0c\u4e0d\u652f\u6301\u751f\u65e5\u6821\u9a8c", (Object[])new Object[]{paramName}));
        }
        cn.hutool.core.lang.Validator.validateBirthday((CharSequence)date, (String)StrUtil.format((CharSequence)BIRTHDAY_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator cellphone(String paramName) {
        cn.hutool.core.lang.Validator.validateMobile((CharSequence)((CharSequence)this.param), (String)StrUtil.format((CharSequence)CELLPHONE_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator email(String paramName) {
        cn.hutool.core.lang.Validator.validateEmail((CharSequence)((CharSequence)this.param), (String)StrUtil.format((CharSequence)EMAIL_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator idCard(String paramName) {
        cn.hutool.core.lang.Validator.validateCitizenIdNumber((CharSequence)((CharSequence)this.param), (String)StrUtil.format((CharSequence)ID_CARD_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator plateNumber(String paramName) {
        cn.hutool.core.lang.Validator.validatePlateNumber((CharSequence)((CharSequence)this.param), (String)StrUtil.format((CharSequence)PLATE_NUMBER_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator uuid(String paramName) {
        cn.hutool.core.lang.Validator.validateUUID((CharSequence)((CharSequence)this.param), (String)StrUtil.format((CharSequence)UUID_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator url(String paramName) {
        cn.hutool.core.lang.Validator.validateUrl((CharSequence)((CharSequence)this.param), (String)StrUtil.format((CharSequence)URL_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator ipv4(String paramName) {
        cn.hutool.core.lang.Validator.validateIpv4((CharSequence)((CharSequence)this.param), (String)StrUtil.format((CharSequence)IPV4_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator ipv6(String paramName) {
        cn.hutool.core.lang.Validator.validateIpv6((CharSequence)((CharSequence)this.param), (String)StrUtil.format((CharSequence)IPV6_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator macAddress(String paramName) {
        cn.hutool.core.lang.Validator.validateMac((CharSequence)((CharSequence)this.param), (String)StrUtil.format((CharSequence)MAC_ADDRESS_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator carDrivingLicence(String paramName) {
        cn.hutool.core.lang.Validator.validateCarDrivingLicence((CharSequence)((CharSequence)this.param), (String)StrUtil.format((CharSequence)CAR_DRIVING_LICENCE_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator carVin(String paramName) {
        cn.hutool.core.lang.Validator.validateCarVin((CharSequence)((CharSequence)this.param), (String)StrUtil.format((CharSequence)CAR_VIN_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator creditCode(String paramName) {
        if (!cn.hutool.core.lang.Validator.isCreditCode((CharSequence)((CharSequence)this.param))) {
            throw new ValidateException(CREDIT_CODE_HINT_MSG, new Object[]{paramName});
        }
        return this;
    }

    public Validator zipCode(String paramName) {
        cn.hutool.core.lang.Validator.validateZipCode((CharSequence)((CharSequence)this.param), (String)StrUtil.format((CharSequence)ZIP_CODE_HINT_MSG, (Object[])new Object[]{paramName}));
        return this;
    }

    public Validator regex(String regex, String paramName) {
        cn.hutool.core.lang.Validator.validateMatchRegex((String)regex, (CharSequence)((CharSequence)this.param), (String)StrUtil.format((CharSequence)REGEX_HINT_MSG, (Object[])new Object[]{paramName, regex}));
        return this;
    }

    public Validator valid(Object param, Class<?> ... groups) {
        Set violations = ((jakarta.validation.Validator)ContextUtils.getBean(jakarta.validation.Validator.class, (boolean)true)).validate(param, (Class[])groups);
        if (!violations.isEmpty()) {
            LogUtils.warn((String)"{} violations.", (Object[])new Object[]{violations.size()});
            Console.log((String)"\u6821\u9a8c\u5bf9\u8c61\uff1a{}", (Object[])new Object[]{param});
            JSONArray errorHints = new JSONArray();
            violations.forEach(violation -> {
                String errorkey = violation.getPropertyPath().toString();
                Object errorValue = violation.getInvalidValue();
                String errorHintMsg = violation.getMessage();
                JSONObject errorHint = new JSONObject();
                errorHint.put((Object)"errorkey", (Object)errorkey);
                errorHint.put((Object)"errorValue", errorValue);
                errorHint.put((Object)"errorHintMsg", (Object)errorHintMsg);
                errorHints.add((Object)errorHint);
                LogUtils.error((String)errorHint.toString(new JSONWriter.Feature[]{JSONWriter.Feature.WriteMapNullValue}), (Object[])new Object[0]);
            });
            throw new ValidateException(errorHints.toString(new JSONWriter.Feature[]{JSONWriter.Feature.WriteMapNullValue}));
        }
        return this;
    }
}

