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

package com.kuma.boot.ratelimit.ratelimitbs.core.support.proxy.cglib;

import com.kuma.boot.ratelimit.ratelimitbs.core.bs.RateLimitBs;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.proxy.AbstractProxy;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * CGLIB 代理类
 */
public class CglibProxy extends AbstractProxy implements MethodInterceptor {

    /**
     * 被代理的对象
     */
    private final Object target;

    public CglibProxy(Object target) {
        this.target = target;
    }

    public CglibProxy(Object target, RateLimitBs bs) {
        super(bs);
        this.target = target;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        // 1. 添加判断
        super.rateLimitBs.tryAcquire(method, objects);

        // 2. 返回结果
        return method.invoke(target, objects);
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
