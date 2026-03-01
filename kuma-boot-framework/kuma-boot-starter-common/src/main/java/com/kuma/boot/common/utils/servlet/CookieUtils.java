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

package com.kuma.boot.common.utils.servlet;

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CookieUtil
 *
 * @author kuma
 * @version 2023.01
 * @since 2023-01-03 11:33:23
 */
public class CookieUtils {

    /**
     * 新增cookie
     * @param key key值
     * @param value 对应值
     * @param maxAge cookie 有效时间
     * @param response 响应
     * @since 2023-01-03 11:33:23
     */
    public static void addCookie(
            String key, String value, Integer maxAge, HttpServletResponse response) {
        try {
            Cookie c = new Cookie(key, value);
            c.setMaxAge(maxAge);
            c.setPath("/");
            response.addCookie(c);
        } catch (Exception e) {
            LogUtils.error("新增cookie错误", e);
        }
    }

    /**
     * 删除cookie
     * @param key key值
     * @param response 响应
     * @since 2023-01-03 11:33:23
     */
    public static void delCookie(String key, HttpServletResponse response) {
        try {
            Cookie c = new Cookie(key, "");
            c.setMaxAge(0);
            response.addCookie(c);
        } catch (Exception e) {
            LogUtils.error("删除cookie错误", e);
        }
    }

    /**
     * 获取cookie
     * @param key key值
     * @param request 请求
     * @return {@link String }
     * @since 2023-01-03 11:33:23
     */
    public static String getCookie(String key, HttpServletRequest request) {
        try {
            if (request.getCookies() == null) {
                return null;
            }
            for (int i = 0; i < request.getCookies().length; i++) {
                if (request.getCookies()[i].getName().equals(key)) {
                    return request.getCookies()[i].getValue();
                }
            }
        } catch (Exception e) {
            LogUtils.error("获取cookie错误", e);
        }
        return null;
    }
}
