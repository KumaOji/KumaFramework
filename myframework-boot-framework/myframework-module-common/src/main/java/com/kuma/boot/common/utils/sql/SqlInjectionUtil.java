/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.utils.sql;

import cn.hutool.crypto.SecureUtil;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * sql注入处理工具类
 *
 * @author kuma
 * @version 2022.09
 * @since 2023-01-03 11:29:24
 */
public class SqlInjectionUtil {

    /** sign 用于表字典加签的盐值【SQL漏洞】 （上线修改值 20200501，同步修改前端的盐值） */
    private static final String TABLE_DICT_SIGN_SALT = "20200501";

    private static final String XSS_STR =
            "and |exec |insert |select |delete |update |drop |count |chr |mid |master |truncate"
                    + " |char |declare |;|or |+|user()";

    /** 正则 user() 匹配更严谨 */
    private static final String REGULAR_EXPRE_USER = "user[\\s]*\\([\\s]*\\)";

    /** 正则 show tables */
    private static final String SHOW_TABLES = "show\\s+tables";

    /**
     * 针对表字典进行额外的sign签名校验（增加安全机制）
     * @param dictCode :
     * @param sign :
     * @param request :
     * @since 2023-01-03 11:30:10
     */
    public static void checkDictTableSign(
            String dictCode, String sign, HttpServletRequest request) {
        // 表字典SQL注入漏洞,签名校验
        String accessToken = request.getHeader("X-Access-Token");
        String signStr = dictCode + SqlInjectionUtil.TABLE_DICT_SIGN_SALT + accessToken;
        String javaSign = SecureUtil.md5(signStr);
        if (!javaSign.equals(sign)) {
            LogUtils.error(
                    "表字典，SQL注入漏洞签名校验失败 ：" + sign + "!=" + javaSign + ",dictCode=" + dictCode);
            throw new BaseException("无权限访问！");
        }
        LogUtils.info(" 表字典，SQL注入漏洞签名校验成功！sign=" + sign + ",dictCode=" + dictCode);
    }

    /**
     * sql注入过滤处理，遇到注入关键字抛异常
     * @param value
     */
    public static void filterContent(String value) {
        filterContent(value, null);
    }

    /**
     * sql注入过滤处理，遇到注入关键字抛异常
     * @param value
     * @param customXssString 定制xss字符串
     * @since 2023-01-03 11:30:05
     */
    public static void filterContent(String value, String customXssString) {
        if (value == null || "".equals(value)) {
            return;
        }
        // 统一转为小写
        value = value.toLowerCase();
        // SQL注入检测存在绕过风险 https://gitee.com/jeecg/jeecg-boot/issues/I4NZGE
        value = value.replaceAll("/\\*.*\\*/", "");

        String[] xssArr = XSS_STR.split("\\|");
        for (int i = 0; i < xssArr.length; i++) {
            if (value.indexOf(xssArr[i]) > -1) {
                LogUtils.error("请注意，存在SQL注入关键词---> {}", xssArr[i]);
                LogUtils.error("请注意，值可能存在SQL注入风险!---> {}", value);
                throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
            }
        }
        // update-begin-author:taoyan date:2022-7-13 for: 除了XSS_STR这些提前设置好的，还需要额外的校验比如 单引号
        if (customXssString != null) {
            String[] xssArr2 = customXssString.split("\\|");
            for (int i = 0; i < xssArr2.length; i++) {
                if (value.contains(xssArr2[i])) {
                    LogUtils.error("请注意，存在SQL注入关键词---> {}", xssArr2[i]);
                    LogUtils.error("请注意，值可能存在SQL注入风险!---> {}", value);
                    throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
                }
            }
        }
        // update-end-author:taoyan date:2022-7-13 for: 除了XSS_STR这些提前设置好的，还需要额外的校验比如 单引号
        if (Pattern.matches(SHOW_TABLES, value) || Pattern.matches(REGULAR_EXPRE_USER, value)) {
            throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
        }
        return;
    }

    /**
     * sql注入过滤处理，遇到注入关键字抛异常
     * @param values
     */
    public static void filterContent(String[] values) {
        filterContent(values, null);
    }

    /**
     * sql注入过滤处理，遇到注入关键字抛异常
     * @param values
     * @param customXssString 定制xss字符串
     * @since 2023-01-03 11:29:57
     */
    public static void filterContent(String[] values, String customXssString) {
        String[] xssArr = XSS_STR.split("\\|");
        for (String value : values) {
            if (value == null || "".equals(value)) {
                return;
            }
            // 统一转为小写
            value = value.toLowerCase();
            // SQL注入检测存在绕过风险 https://gitee.com/jeecg/jeecg-boot/issues/I4NZGE
            value = value.replaceAll("/\\*.*\\*/", "");

            for (int i = 0; i < xssArr.length; i++) {
                if (value.indexOf(xssArr[i]) > -1) {
                    LogUtils.error("请注意，存在SQL注入关键词---> {}", xssArr[i]);
                    LogUtils.error("请注意，值可能存在SQL注入风险!---> {}", value);
                    throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
                }
            }
            // update-begin-author:taoyan date:2022-7-13 for: 除了XSS_STR这些提前设置好的，还需要额外的校验比如
            // 单引号
            if (customXssString != null) {
                String[] xssArr2 = customXssString.split("\\|");
                for (int i = 0; i < xssArr2.length; i++) {
                    if (value.indexOf(xssArr2[i]) > -1) {
                        LogUtils.error("请注意，存在SQL注入关键词---> {}", xssArr2[i]);
                        LogUtils.error("请注意，值可能存在SQL注入风险!---> {}", value);
                        throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
                    }
                }
            }
            // update-end-author:taoyan date:2022-7-13 for: 除了XSS_STR这些提前设置好的，还需要额外的校验比如
            // 单引号
            if (Pattern.matches(SHOW_TABLES, value) || Pattern.matches(REGULAR_EXPRE_USER, value)) {
                throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
            }
        }
        return;
    }

    /**
     * 【提醒：不通用】 仅用于字典条件SQL参数，注入过滤
     * @param value
     * @since 2023-01-03 11:29:53
     */
    @Deprecated
    public static void specialFilterContentForDictSql(String value) {
        String specialXssStr =
                " exec | insert | select | delete | update | drop | count | chr | mid | master |"
                        + " truncate | char | declare |;|+|user()";
        String[] xssArr = specialXssStr.split("\\|");
        if (value == null || "".equals(value)) {
            return;
        }
        // 统一转为小写
        value = value.toLowerCase();
        // SQL注入检测存在绕过风险 https://gitee.com/jeecg/jeecg-boot/issues/I4NZGE
        value = value.replaceAll("/\\*.*\\*/", "");

        for (int i = 0; i < xssArr.length; i++) {
            if (value.indexOf(xssArr[i]) > -1 || value.startsWith(xssArr[i].trim())) {
                LogUtils.error("请注意，存在SQL注入关键词---> {}", xssArr[i]);
                LogUtils.error("请注意，值可能存在SQL注入风险!---> {}", value);
                throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
            }
        }
        if (Pattern.matches(SHOW_TABLES, value) || Pattern.matches(REGULAR_EXPRE_USER, value)) {
            throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
        }
        return;
    }

    /**
     * 【提醒：不通用】 仅用于Online报表SQL解析，注入过滤
     * @param value
     * @since 2023-01-03 11:29:43
     */
    @Deprecated
    public static void specialFilterContentForOnlineReport(String value) {
        String specialXssStr =
                " exec | insert | delete | update | drop | chr | mid | master | truncate | char |"
                        + " declare |user()";
        String[] xssArr = specialXssStr.split("\\|");
        if (value == null || "".equals(value)) {
            return;
        }
        // 统一转为小写
        value = value.toLowerCase();
        // SQL注入检测存在绕过风险 https://gitee.com/jeecg/jeecg-boot/issues/I4NZGE
        value = value.replaceAll("/\\*.*\\*/", "");

        for (int i = 0; i < xssArr.length; i++) {
            if (value.indexOf(xssArr[i]) > -1 || value.startsWith(xssArr[i].trim())) {
                LogUtils.error("请注意，存在SQL注入关键词---> {}", xssArr[i]);
                LogUtils.error("请注意，值可能存在SQL注入风险!---> {}", value);
                throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
            }
        }

        if (Pattern.matches(SHOW_TABLES, value) || Pattern.matches(REGULAR_EXPRE_USER, value)) {
            throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
        }
        return;
    }

    /**
     * 判断给定的字段是不是类中的属性
     * @param field 字段名
     * @param clazz 类对象
     * @return boolean
     * @since 2023-01-03 11:29:39
     */
    public static boolean isClassField(String field, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            String tableColumnName = StringUtils.camelToUnderline(fieldName);
            if (fieldName.equalsIgnoreCase(field) || tableColumnName.equalsIgnoreCase(field)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断给定的多个字段是不是类中的属性
     * @param fieldSet 字段名set
     * @param clazz 类对象
     * @return boolean
     * @since 2023-01-03 11:29:35
     */
    public static boolean isClassField(Set<String> fieldSet, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (String field : fieldSet) {
            boolean exist = false;
            for (int i = 0; i < fields.length; i++) {
                String fieldName = fields[i].getName();
                String tableColumnName = StringUtils.camelToUnderline(fieldName);
                if (fieldName.equalsIgnoreCase(field) || tableColumnName.equalsIgnoreCase(field)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                return false;
            }
        }
        return true;
    }
}
