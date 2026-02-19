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

import java.util.Deque;
import java.util.Map;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.MDC;

/**
 * MdcUtils
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class MdcUtils {

    public static void put( String key, String val ) {
        if (StrUtil.isNotBlank(val)) {
            MDC.put(key, val);
            LoggerContextUtils.put(key, val);
        }
    }

    public static String get( String key ) {
        String s = MDC.get(key);
        if (StrUtil.isBlank(s)) {
            s = LoggerContextUtils.get(key);
        }
        return s;
    }

    public static void remove( String key ) {
        MDC.remove(key);
        LoggerContextUtils.remove(key);
    }

    public static void clear() {
        MDC.clear();
        LoggerContextUtils.clear();
    }

    public static Map<String, String> getCopyOfContextMap() {
        Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
        if (MapUtil.isEmpty(copyOfContextMap)) {
            copyOfContextMap = LoggerContextUtils.getCopyOfContextMap();
        }
        return copyOfContextMap;
    }

    public static void setContextMap( Map<String, String> contextMap ) {
        if (MapUtil.isNotEmpty(contextMap)) {
            MDC.setContextMap(contextMap);
            LoggerContextUtils.setContextMap(contextMap);
        }
    }

    public static void pushByKey( String key, String value ) {
        if (StrUtil.isNotBlank(value)) {
            MDC.pushByKey(key, value);
            LoggerContextUtils.pushByKey(key, value);
        }
    }

    public static String popByKey( String key ) {
        String s = MDC.popByKey(key);
        if (StrUtil.isBlank(s)) {
            s = LoggerContextUtils.popByKey(key);
        }
        return s;
    }

    public static Deque<String> getCopyOfDequeByKey( String key ) {
        return LoggerContextUtils.getCopyOfDequeByKey(key);
    }

    public static void clearDequeByKey( String key ) {
        LoggerContextUtils.clearDequeByKey(key);
    }
}
