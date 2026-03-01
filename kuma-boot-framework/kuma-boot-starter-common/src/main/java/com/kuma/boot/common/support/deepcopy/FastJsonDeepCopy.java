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

package com.kuma.boot.common.support.deepcopy;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

/**
 * FastJson 深度拷贝实现
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:08:11
 */
public class FastJsonDeepCopy extends AbstractDeepCopy {

    /** 对象单例 */
    private static final FastJsonDeepCopy INSTANCE = new FastJsonDeepCopy();

    /** 获取 */
    public static FastJsonDeepCopy getInstance() {
        return INSTANCE;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T doDeepCopy(T object) {
        final Class<?> clazz = object.getClass();
        String jsonString = JSON.toJSONString(object, JSONWriter.Feature.ReferenceDetection);
        return (T) JSON.parseObject(jsonString, clazz);
    }
}
