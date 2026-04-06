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

package com.kuma.cloud.cache.support.proxy.cglib;

import com.kuma.cloud.cache.api.Cache;
import com.kuma.cloud.cache.support.proxy.CacheProxy;
import com.kuma.cloud.cache.support.proxy.bs.CacheProxyBs;
import com.kuma.cloud.cache.support.proxy.bs.DefaultCacheProxyBsContext;
import com.kuma.cloud.cache.support.proxy.bs.CacheProxyBsContext;
import java.lang.reflect.Method;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

/**
 * CGLIB 代理类
 * @author kuma
 * date 2019/3/7
 * @since 2024.06
 */
public class CglibProxy implements MethodInterceptor, CacheProxy {

    /**
     * 被代理的对象
     */
    private final Cache target;

    public CglibProxy( Cache target) {
        this.target = target;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] params, MethodProxy methodProxy)
            throws Throwable {
        CacheProxyBsContext context =
                DefaultCacheProxyBsContext.newInstance().method(method).params(params).target(target);

        return CacheProxyBs.newInstance().context(context).execute();
    }

    @Override
    public Object proxy() {
        Enhancer enhancer = new Enhancer();
        // 目标对象类
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        // 通过字节码技术创建目标对象类的子类实例作为代理
        return enhancer.create();
    }
}
