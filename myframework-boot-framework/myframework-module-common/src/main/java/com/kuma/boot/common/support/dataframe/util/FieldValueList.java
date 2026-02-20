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

package com.kuma.boot.common.support.dataframe.util;

import java.util.List;
import java.util.function.Function;

/**
 * @author caizhihao
 *
 */
public class FieldValueList<T, F> {

    private final List<T> data;

    private final Function<T, F> field;

    public FieldValueList(List<T> data, Function<T, F> field) {
        this.data = data;
        this.field = field;
    }

    public F get(Integer index) {
        if (index == null) {
            return null;
        }
        return field.apply(data.get(index));
    }
}
