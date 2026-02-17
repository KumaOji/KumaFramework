/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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
 * <p>
 * Description: 枚举值定义
 * </p>
 *
 * @author : gengwei.zheng
 * @since : 2022/3/26 16:49
 */
public interface ValueEnum<T> {

    /**
     * 获取枚举自定义值
     * @return 自定义枚举值
     */
    T getValue();
}
