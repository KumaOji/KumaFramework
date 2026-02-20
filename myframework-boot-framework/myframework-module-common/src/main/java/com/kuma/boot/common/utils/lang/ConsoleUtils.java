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

package com.kuma.boot.common.utils.lang;

import com.google.common.collect.Lists;
import com.kuma.boot.common.constant.PunctuationConstants;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.List;

/**
 * 日志工具类
 */
public final class ConsoleUtils {

    private ConsoleUtils() {}

    /**
     * 单行日志信息
     */
    public static final String LINE = "--------------------------------------------------------";

    /**
     * 输出文档
     * @param className 类名
     * @param methodName 方法名
     * @param format 文本格式化
     * @param args 参数
     */
    public static void info(
            final String className,
            final String methodName,
            final String format,
            final Object... args) {
        String formatStr = buildString(format, args);
        log("INFO", className, methodName, formatStr, null);
    }

    /**
     * 输出文档
     * @param format 文本格式化
     * @param args 参数
     */
    public static void info(final String format, final Object... args) {
        StackTraceElement callMethodElem = Thread.currentThread().getStackTrace()[3];
        String className = callMethodElem.getClassName();
        final String methodNameName = callMethodElem.getMethodName();

        info(className, methodNameName, format, args);
    }

    /**
     * 格式化信息
     * @param format 格式化
     * @param params 参数
     * @return 结果
     */
    private static String buildString(String format, Object[] params) {
        String stringFormat = format;

        for (int i = 0; i < params.length; ++i) {
            stringFormat = stringFormat.replaceFirst("\\{}", "%s");
        }

        return String.format(stringFormat, params);
    }

    /**
     * 消息打印
     *
     * <p>
     * final String threadName = Thread.currentThread().getName();
     * @param level 消息等级
     * @param content 内容
     * @param throwable 异常
     */
    private static void log(
            final String level,
            final String className,
            final String methodName,
            String content,
            Throwable throwable) {
        final String prettyMethod = buildPrettyMethodName(className, methodName);

        String dateStr = DateUtils.getCurrentDateTimeStr();
        String log = String.format("[%s] [%s] [%s] - %s", level, dateStr, prettyMethod, content);
        if ("ERROR".equalsIgnoreCase(level)) {
            System.err.println(log);
        } else {
            LogUtils.info(log);
        }

        if (throwable != null) {
            throwable.printStackTrace(System.err);
        }
    }

    /**
     * 构建更加优雅的方法名称 （1）className 只取首字母
     * @param className 类名
     * @param methodName 方法名称
     * @return 结果
     */
    private static String buildPrettyMethodName(final String className, final String methodName) {
        String[] classNames = className.split("\\.");
        if (ArrayUtils.isEmpty(classNames)) {
            return methodName;
        }

        final int length = classNames.length;
        if (length == 1) {
            return className + PunctuationConstants.DOT + methodName;
        }

        // 类名超过一个
        List<String> classFirstChars = Lists.newArrayList();
        for (int i = 0; i < length - 1; i++) {
            String name = classNames[i];
            classFirstChars.add(String.valueOf(name.charAt(0)));
        }
        classFirstChars.add(classNames[length - 1]);
        String prettyClass = CollectionUtils.join(classFirstChars, PunctuationConstants.DOT);
        return prettyClass + PunctuationConstants.DOT + methodName;
    }
}
