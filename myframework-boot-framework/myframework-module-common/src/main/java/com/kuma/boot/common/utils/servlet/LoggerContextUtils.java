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

import ch.qos.logback.classic.LoggerContext;

import java.util.Deque;
import java.util.Map;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

/**
 * LoggerContextUtils
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class LoggerContextUtils {

    public static LoggerContext loggerContext = null;

    static {
        ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
        if (iLoggerFactory instanceof LoggerContext loggerContextInfo) {
            LoggerContextUtils.loggerContext = loggerContextInfo;
        }
    }

    public static void put( String key, String val ) {
        if (null != loggerContext) {
            loggerContext.getMDCAdapter().put(key, val);
        }
    }

    public static String get( String key ) {
        if (null != loggerContext) {
            return loggerContext.getMDCAdapter().get(key);
        }
        return null;
    }

    public static void remove( String key ) {
        if (null != loggerContext) {

            loggerContext.getMDCAdapter().remove(key);
        }
    }

    public static void clear() {
        if (null != loggerContext) {

            loggerContext.getMDCAdapter().clear();
        }
    }

    public static Map<String, String> getCopyOfContextMap() {
        if (null != loggerContext) {
            return loggerContext.getMDCAdapter().getCopyOfContextMap();
        }
        return null;
    }

    public static void setContextMap( Map<String, String> contextMap ) {
        if (null != loggerContext) {

            loggerContext.getMDCAdapter().setContextMap(contextMap);
        }
    }

    public static void pushByKey( String key, String value ) {
        if (null != loggerContext) {

            loggerContext.getMDCAdapter().pushByKey(key, value);
        }
    }

    public static String popByKey( String key ) {
        if (null != loggerContext) {
            return loggerContext.getMDCAdapter().popByKey(key);
        }
        return null;
    }

    public static Deque<String> getCopyOfDequeByKey( String key ) {
        if (null != loggerContext) {
            return loggerContext.getMDCAdapter().getCopyOfDequeByKey(key);
        }
        return null;
    }

    public static void clearDequeByKey( String key ) {
        if (null != loggerContext) {
            loggerContext.getMDCAdapter().clearDequeByKey(key);
        }
    }
}
