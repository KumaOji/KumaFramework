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

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.model.Code;

/**
 * 基于代码枚举
 *
 * @author kuma
 * @version 2023.01
 * @since 2023-02-21 13:34:01
 */
public interface CodeEnum {

    /**
     * 获取代码
     *
     * @return int
     * @since 2023-02-21 13:34:03
     */
    int getCode();

    default Code code() {
        return ResultEnum.FAILED.code();
    }

    default String codeDesc() {
        return "";
    }
}
