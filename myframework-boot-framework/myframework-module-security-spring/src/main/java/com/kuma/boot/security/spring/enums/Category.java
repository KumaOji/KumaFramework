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

package com.kuma.boot.security.spring.enums;

import com.kuma.boot.common.utils.lang.StringUtils;

/**
 * <p>URL 路径类别
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 09:59:56
 */
public enum Category {

    /**
     * 含有通配符，含有 "*" 或 "?"
     */
    WILDCARD,
    /**
     * 含有占位符，含有 "{" 和 " } "
     */
    PLACEHOLDER,
    /**
     * 不含有任何特殊字符的完整路径
     */
    FULL_PATH;

    /**
     * 得到类别
     *
     * @param url url
     * @return {@link Category }
     * @since 2023-07-04 09:59:56
     */
    public static Category getCategory(String url) {

        if (StringUtils.containsAny(url, new String[] {"*", "?"})) {
            return Category.WILDCARD;
        }

        if (StringUtils.contains(url, "{")) {
            return Category.PLACEHOLDER;
        }

        return Category.FULL_PATH;
    }
}
