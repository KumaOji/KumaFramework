/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.dingtalk.support;

/**
 * dkid 生成接口
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:26:00
 */
public interface DingerIdGenerator {

    /**
     * dkid生成规则, 须保证全局唯一
     *
     * @return dkid
     */
    String dingerId();
}
