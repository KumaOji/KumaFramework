/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.utils.common;

import cn.hutool.core.lang.Assert;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.utils.log.LogUtils;

/**
 * RESTful断言
 *
 * @author kuma
 * @version 2022.06
 * @since 2022-07-30 10:02:51
 */
public class AssertUtils extends Assert {

    /**
     * 断言-执行给定代码块不会抛出异常
     *
     * <p>
     * 若不是期望的结果，执行代码块时出现错误，将抛出ResultException
     * @param throwMsg 执行代码块错误时抛出的ResultException message
     * @param lambdaRun lambda给定代码块，示例：{@code () -&gt; {代码块}}
     */
    public static void notThrow(String throwMsg, Runnable lambdaRun) {
        try {
            lambdaRun.run();
        } catch (Exception e) {
            throw new BaseException(throwMsg);
        }
    }

    /**
     * 断言-执行给定代码块不会抛出异常
     *
     * <p>
     * 若不是期望的结果，执行代码块时出现错误，将打印printMsg
     * @param printMsg 执行代码块错误时，打印的信息
     * @param lambdaRun lambda给定代码块，示例：{@code () -&gt; {代码块}}
     */
    public static void notThrowIfErrorPrintMsg(String printMsg, Runnable lambdaRun) {
        try {
            lambdaRun.run();
        } catch (Exception e) {
            LogUtils.warn(printMsg);
        }
    }

    /**
     * 断言-执行给定代码块不会抛出异常
     *
     * <p>
     * 若不是期望的结果，执行代码块时出现错误，将打印printMsg与堆栈跟踪信息
     * @param printMsg 执行代码块错误时，打印的信息
     * @param lambdaRun lambda给定代码块，示例：{@code () -&gt; {代码块}}
     */
    public static void notThrowIfErrorPrintStackTrace(String printMsg, Runnable lambdaRun) {
        try {
            lambdaRun.run();
        } catch (Exception e) {
            LogUtils.warn(printMsg);
            LogUtils.error(e);
        }
    }
}
