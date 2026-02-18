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

package com.kuma.boot.common.support.proxy;

import java.lang.reflect.Method;

/**
 * 方法的处理
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:11:08
 */
public interface MethodHandler {

    /**
     * 方法的处理
     * @param proxy 代理类
     * @param method 方法
     * @param args 参数
     * @return 结果
     * @throws Throwable if any
     */
    Object handle(final Object proxy, final Method method, final Object[] args) throws Throwable;
}
