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

package com.kuma.boot.common.enums.base;

/**
 * 自我描述枚举
 *
 * @author kuma
 * @version 2023.01
 * @since 2023-02-21 13:34:11
 */
public interface SelfDescribedEnum {

    /**
     * 得到名字
     * @return {@link String }
     * @since 2023-02-21 13:34:13
     */
    default String getName() {
        return name();
    }

    /**
     * 名字
     * @return {@link String }
     * @since 2023-02-21 13:34:17
     */
    String name();

    /**
     * 得到desc
     * @return {@link String }
     * @since 2023-02-21 13:34:19
     */
    String getDesc();
}
