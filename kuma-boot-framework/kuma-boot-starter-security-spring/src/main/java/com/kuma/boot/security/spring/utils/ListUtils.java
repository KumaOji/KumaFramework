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

package com.kuma.boot.security.spring.utils;

import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/**
 * <p>List 常用工具类
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:08:21
 */
public class ListUtils {

    /**
     * 将两个已排序的集合a和b合并到一个单独的已排序列表中，以便保留元素的自然顺序。
     *
     * @param appendResources  自定义配置
     * @param defaultResources 默认配置
     * @return {@link List }<{@link String }>
     * @since 2023-07-04 10:08:21
     */
    public static List<String> merge(List<String> appendResources, List<String> defaultResources) {
        if (CollectionUtils.isEmpty(appendResources)) {
            return defaultResources;
        } else {
            return CollectionUtils.collate(defaultResources, appendResources);
        }
    }

    /**
     * 将 List 转换为 String[]
     *
     * @param resources List
     * @return {@link String[] }
     * @since 2023-07-04 10:08:21
     */
    public static String[] toStringArray(List<String> resources) {
        if (CollectionUtils.isNotEmpty(resources)) {
            String[] result = new String[resources.size()];
            return resources.toArray(result);
        } else {
            return new String[] {};
        }
    }
}
