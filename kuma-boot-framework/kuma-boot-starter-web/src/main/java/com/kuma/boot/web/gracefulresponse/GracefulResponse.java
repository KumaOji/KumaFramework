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

package com.kuma.boot.web.gracefulresponse;

import com.kuma.boot.web.gracefulresponse.api.AssertFunction;

/**
 * GracefulResponse工具类
 */
public class GracefulResponse {

    /**
     * 需要抛自定义异常时，调用该方法
     *
     * @param code 异常码
     * @param msg  异常提示
     */
    public static void raiseException(String code, String msg) {
        throw new GracefulResponseException(code, msg);
    }

    /**
     * 需要抛自定义异常时，调用该方法
     *
     * @param code      异常码
     * @param msg       异常提示
     * @param throwable 捕获的异常
     */
    public static void raiseException(String code, String msg, Throwable throwable) {
        throw new GracefulResponseException(code, msg, throwable);
    }

    public static void wrapAssert( AssertFunction assertFunction) {
        try {
            assertFunction.doAssert();
        } catch (Exception e) {
            throw new GracefulResponseException(e.getMessage(), e);
        }
    }

    public static void wrapAssert(String code, AssertFunction assertFunction) {
        try {
            assertFunction.doAssert();
        } catch (Exception e) {
            throw new GracefulResponseException(code, e.getMessage(), e);
        }
    }
}
