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

package com.kuma.boot.web.utils;

import cn.hutool.core.util.ObjUtil;
import com.kuma.boot.common.utils.exception.FastStringPrintWriter;
import com.kuma.boot.web.exception.event.ErrorEvent;

import java.time.LocalDateTime;

/**
 * 异常工具类
 */
public class ErrorUtils {

    /**
     * 初始化异常信息
     *
     * @param error 异常
     * @param event 异常事件封装
     */
    public static void initErrorInfo(Throwable error, ErrorEvent event) {
        // 堆栈信息
        event.setStackTrace(getStackTraceAsString(error));
        event.setExceptionName(error.getClass().getName());
        event.setMessage(error.getMessage());
        event.setCreatedAt(LocalDateTime.now());
        StackTraceElement[] elements = error.getStackTrace();
        if (ObjUtil.isNotEmpty(elements)) {
            // 报错的类信息
            StackTraceElement element = elements[0];
            event.setClassName(element.getClassName());
            event.setFileName(element.getFileName());
            event.setMethodName(element.getMethodName());
            event.setLineNumber(element.getLineNumber());
        }
    }

    public static String getStackTraceAsString(Throwable ex) {
        FastStringPrintWriter printWriter = new FastStringPrintWriter(512);
        ex.printStackTrace(printWriter);
        return printWriter.toString();
    }
}
