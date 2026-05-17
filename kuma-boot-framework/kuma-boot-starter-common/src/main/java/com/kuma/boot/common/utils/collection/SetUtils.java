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

package com.kuma.boot.common.utils.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Set 工具类 */
public final class SetUtils {

    private SetUtils() {}

    /**
     * 获取第一个元素
     * @param set set 集合
     * @param <T> 泛型
     * @return 结果
     */
    public static <T> T getFirst(final Set<T> set) {
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }

        List<T> list = new ArrayList<>(set);
        return list.get(0);
    }
}
