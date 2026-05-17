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

package com.kuma.boot.common.support.dataframe.iframe.support;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

/**
 * Merge attributes based on the same field names
 *
 * @author caizhihao
 * @param <T>
 * @param <K>
 * @param <R>
 *
 */
public class DefaultJoin<T, K, R> implements Join<T, K, R> {

    @Override
    @SuppressWarnings("unchecked")
    public R join(T t, K k) {
        try {
            ParameterizedType parameterizedType =
                    (ParameterizedType) this.getClass().getGenericSuperclass();
            Class<R> rClass = (Class<R>) parameterizedType.getActualTypeArguments()[2];
            R r = rClass.getDeclaredConstructor().newInstance();
            for (Field field : r.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = null;
                if (t != null) {
                    Field tFeild = t.getClass().getDeclaredField(fieldName);
                    if (tFeild != null) {
                        tFeild.setAccessible(true);
                        fieldValue = tFeild.get(t);
                    }
                }
                if (k != null && fieldValue == null) {
                    Field kFeild = k.getClass().getDeclaredField(fieldName);
                    if (kFeild != null) {
                        kFeild.setAccessible(true);
                        fieldValue = kFeild.get(t);
                    }
                }
                field.set(r, fieldValue);
            }
            return r;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
