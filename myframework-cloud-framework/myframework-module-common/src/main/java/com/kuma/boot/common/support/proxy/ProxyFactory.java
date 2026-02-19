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

package com.kuma.boot.common.support.proxy;

import com.kuma.boot.common.enums.ProxyTypeEnum;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import java.lang.reflect.Proxy;

/**
 * 代理工厂
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:11:11
 */
public class ProxyFactory {

    private ProxyFactory() {}

    /**
     * 获取代理类型
     * @param object 对象
     * @return 代理枚举
     */
    public static ProxyTypeEnum getProxyType(final Object object) {
        if (ObjectUtils.isNull(object)) {
            return ProxyTypeEnum.NONE;
        }

        final Class<?> clazz = object.getClass();

        // 如果targetClass本身是个接口或者targetClass是JDK Proxy生成的,则使用JDK动态代理。
        // 参考 spring 的 AOP 判断
        if (clazz.isInterface() || Proxy.isProxyClass(clazz)) {
            return ProxyTypeEnum.JDK;
        }

        return ProxyTypeEnum.CGLIB;
    }
}
