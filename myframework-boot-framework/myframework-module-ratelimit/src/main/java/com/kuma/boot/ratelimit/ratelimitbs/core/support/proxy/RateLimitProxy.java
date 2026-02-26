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

package com.kuma.boot.ratelimit.ratelimitbs.core.support.proxy;

import com.kuma.boot.common.support.proxy.NoneProxy;
import com.kuma.boot.ratelimit.ratelimitbs.core.bs.RateLimitBs;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.proxy.cglib.CglibProxy;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.proxy.dynamic.DynamicProxy;
import cn.hutool.core.util.ObjUtil;

import java.lang.reflect.Proxy;

public final class RateLimitProxy {

    private RateLimitProxy() {}

    /**
     * 获取对象代理
     * @param <T> 泛型
     * @param object 对象代理
     * @return 代理信息
     */
    @SuppressWarnings("all")
    public static <T> T getProxy(final T object) {
        final RateLimitBs rateLimitBs = RateLimitBs.newInstance();

        return getProxy(object, rateLimitBs);
    }

    /**
     * 获取对象代理
     * @param <T> 泛型
     * @param object 对象代理
     * @param rateLimitBs 引导类
     * @return 代理信息
     */
    @SuppressWarnings("all")
    public static <T> T getProxy(final T object, final RateLimitBs rateLimitBs) {
        if (ObjUtil.isNull(object)) {
            return (T) new NoneProxy(object).proxy();
        }

        final Class clazz = object.getClass();

        // 如果targetClass本身是个接口或者targetClass是JDK Proxy生成的,则使用JDK动态代理。
        // 参考 spring 的 AOP 判断
        if (clazz.isInterface() || Proxy.isProxyClass(clazz)) {
            return (T) new DynamicProxy(object, rateLimitBs).proxy();
        }

        return (T) new CglibProxy(object, rateLimitBs).proxy();
    }
}
