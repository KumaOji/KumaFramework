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
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * SQL注入处理工具类
 *
 * @author kuma
 * @version 2022.09
 * @since 2023-01-03 11:30:16
 */
public class SqlInjectionUtils {

    /** sql正则表达式 */
    private static final String SQL_REGEX =
            "\\b(and|or)\\b.{1,6}?(=|>|<|\\bin\\b|\\blike\\b)|\\/\\*.+?\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT|UPDATE.+?SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE).+?FROM|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)";

    /** sql模式 */
    private static final Pattern SQL_PATTERN = Pattern.compile(SQL_REGEX, Pattern.CASE_INSENSITIVE);

    /**
     * 匹配
     * @param lowerValue 较低价值
     * @param param 参数
     * @return boolean
     * @since 2023-01-03 11:30:16
     */
    private static boolean matching(String lowerValue, String param) {
        if (SQL_PATTERN.matcher(param).find()) {
            LogUtils.error("The parameter contains keywords {} that do not allow SQL!", lowerValue);
            return true;
        }
        return false;
    }

    /**
     * 以小写
     * @param obj obj
     * @return {@link String }
     * @since 2023-01-03 11:30:16
     */
    private static String toLowerCase(Object obj) {
        // 这里需要将参数转换为小写来处理
        return Optional.ofNullable(obj).map(Object::toString).map(String::toLowerCase).orElse("");
    }

    /**
     * 检查
     * @param value 价值
     * @return boolean
     * @since 2023-01-03 11:30:16
     */
    private static boolean checking(Object value) {
        // 这里需要将参数转换为小写来处理
        String lowerValue = toLowerCase(value);
        return matching(lowerValue, lowerValue);
    }

    /**
     * get请求sql注入校验
     * @param value 具体的检验
     * @return boolean
     * @since 2023-01-03 11:30:16
     */
    public static boolean checkForGet(String value) {
        // 参数需要url编码
        // 这里需要将参数转换为小写来处理
        // 不改变原值
        // value示例 order=asc&pageNum=1&pageSize=100&parentId=0
        String lowerValue = URLDecoder.decode(value, StandardCharsets.UTF_8).toLowerCase();

        // 获取到请求中所有参数值-取每个key=value组合第一个等号后面的值
        return Stream.of(lowerValue.split("\\&"))
                .map(kp -> kp.substring(kp.indexOf("=") + 1))
                .parallel()
                .anyMatch(param -> matching(lowerValue, param));
    }

    /**
     * post请求sql注入校验
     * @param value 具体的检验
     * @return boolean
     * @since 2023-01-03 11:30:16
     */
    public static boolean checkForPost(String value) {

        Object jsonObj = JSON.parse(value);
        if (jsonObj instanceof JSONObject) {
            JSONObject json = (JSONObject) jsonObj;
            return json.entrySet().stream()
                    .parallel()
                    .anyMatch(entry -> checking(entry.getValue()));
        }

        if (jsonObj instanceof JSONArray) {
            JSONArray json = (JSONArray) jsonObj;
            return json.stream().parallel().anyMatch(SqlInjectionUtils::checking);
        }

        return false;
    }

    /** 表dict标志盐 sign 用于表字典加签的盐值【SQL漏洞】 （上线修改值 20200501，同步修改前端的盐值） */
    private static final String TABLE_DICT_SIGN_SALT = "20200501";

    /** xss str */
    private static final String XSS_STR =
            "and |exec |insert |select |delete |update |drop |count |chr |mid |master |truncate"
                    + " |char |declare |;|or |+|user()";

    /** 显示表 show tables */
    private static final String SHOW_TABLES = "show\\s+tables";

    /**
     * 针对表字典进行额外的sign签名校验（增加安全机制）
     * @param dictCode :
     * @param sign :
     * @param request :
     * @since 2023-01-03 11:30:16
     */
    public static void checkDictTableSign(
            String dictCode, String sign, HttpServletRequest request) {
        // 表字典SQL注入漏洞,签名校验
        String accessToken = request.getHeader("X-Access-Token");
        String signStr = dictCode + TABLE_DICT_SIGN_SALT + accessToken;
        String javaSign = SecureUtil.md5(signStr);
        if (!javaSign.equals(sign)) {
            LogUtils.error(
                    "表字典，SQL注入漏洞签名校验失败 ：" + sign + "!=" + javaSign + ",dictCode=" + dictCode);
            throw new RuntimeException("无权限访问！");
        }
        LogUtils.info(" 表字典，SQL注入漏洞签名校验成功！sign=" + sign + ",dictCode=" + dictCode);
    }

    /**
     * sql注入过滤处理，遇到注入关键字抛异常
     * @param value
     * @since 2023-01-03 11:30:17
     */
    public static void filterContent(String value) {
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
        if (Pattern.matches(SHOW_TABLES, value)) {
            throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
        }
        return;
    }

    /**
     * sql注入过滤处理，遇到注入关键字抛异常
     * @param values
     * @since 2023-01-03 11:30:17
     */
    public static void filterContent(String[] values) {
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
            if (Pattern.matches(SHOW_TABLES, value)) {
                throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
            }
        }
        return;
    }

    /**
     * 【提醒：不通用】 仅用于字典条件SQL参数，注入过滤
     * @param value
     * @since 2023-01-03 11:30:17
     */
    // @Deprecated
    public static void specialFilterContent(String value) {
        String specialXssStr =
                " exec | insert | select | delete | update | drop | count | chr | mid | master |"
                        + " truncate | char | declare |;|+|";
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
        if (Pattern.matches(SHOW_TABLES, value)) {
            throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
        }
        return;
    }

    /**
     * 【提醒：不通用】 仅用于Online报表SQL解析，注入过滤
     * @param value
     * @since 2023-01-03 11:30:17
     */
    // @Deprecated
    public static void specialFilterContentForOnlineReport(String value) {
        String specialXssStr =
                " exec | insert | delete | update | drop | chr | mid | master | truncate | char |"
                        + " declare |";
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

        if (Pattern.matches(SHOW_TABLES, value)) {
            throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
        }
        return;
    }
}
