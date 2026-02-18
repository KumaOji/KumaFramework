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

package com.kuma.boot.common.support.deepcopy;

/**
 * 抽象深度拷贝实现
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:08:19
 */
public abstract class AbstractDeepCopy implements DeepCopy {

    /**
     * 实现深度拷贝
     * @param object 入参对象
     * @param <T> 泛型
     * @return 结果
     */
    protected abstract <T> T doDeepCopy(T object);

    @Override
    public <T> T deepCopy(T object) {
        if (null == object) {
            return null;
        }

        return doDeepCopy(object);
    }
}
