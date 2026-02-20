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

package com.kuma.boot.common.support.instance;

import javax.annotation.concurrent.ThreadSafe;

/**
 * 实例化工具类 对于 {@link InstanceFactory} 的便于使用
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:10:31
 */
public final class Instances {

    private Instances() {}

    /**
     * 静态方法单例
     * @param tClass 类信息
     * @param <T> 泛型
     * @return 结果
     */
    public static <T> T singleton(Class<T> tClass) {
        return InstanceFactory.getInstance().singleton(tClass);
    }

    /**
     * 静态方法单例
     * @param tClass 类信息
     * @param groupName 分组名称
     * @param <T> 泛型
     * @return 结果
     */
    public static <T> T singleton(Class<T> tClass, final String groupName) {
        return InstanceFactory.getInstance().singleton(tClass, groupName);
    }

    /**
     * threadLocal 同一个线程对应的实例一致
     * @param tClass class
     * @param <T> 泛型
     * @return 结果
     */
    public static <T> T threadLocal(Class<T> tClass) {
        return InstanceFactory.getInstance().threadLocal(tClass);
    }

    /**
     * {@link ThreadSafe} 线程安全标示的使用单例，或者使用多例
     * @param tClass class
     * @param <T> 泛型
     * @return 结果
     */
    public static <T> T threadSafe(Class<T> tClass) {
        return InstanceFactory.getInstance().threadSafe(tClass);
    }

    /**
     * 多例
     * @param tClass class
     * @param <T> 泛型
     * @return 结果
     */
    public static <T> T multiple(Class<T> tClass) {
        return InstanceFactory.getInstance().multiple(tClass);
    }
}
