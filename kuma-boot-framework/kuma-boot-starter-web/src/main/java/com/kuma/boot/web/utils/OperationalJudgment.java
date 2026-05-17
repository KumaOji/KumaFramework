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

package com.kuma.boot.web.utils;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.UserEnum;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.core.utils.reflect.ReflectionUtils;
import com.kuma.boot.security.spring.utils.SecurityUtils;

import java.util.Objects;

/**
 * 全局统一判定是否可操作某属性
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-07 20:39:01
 */
public final class OperationalJudgment {

    /**
     * 需要判定的对象必须包含属性 memberId，storeId 代表判定的角色
     *
     * @param object 判定的对象
     * @param <T> 判定处理对象
     * @return 处理结果
     */
    public static <T> T judgment(T object) {
        return judgment(object, "memberId", "storeId");
    }

    /**
     * 需要判定的对象必须包含属性 memberId，storeId 代表判定的角色
     *
     * @param object 判定对象
     * @param buyerIdField 买家id
     * @param storeIdField 店铺id
     * @param <T> 范型
     * @return 返回判定本身，防止多次查询对象
     */
    public static <T> T judgment(T object, String buyerIdField, String storeIdField) {
        Integer type = SecurityUtils.getCurrentUser().getType();
        UserEnum userEnum = UserEnum.getEnumByCode(type);
        switch (Objects.requireNonNull(userEnum)) {
            case MANAGER -> {
                return object;
            }
            case MEMBER -> {
                if (SecurityUtils.getCurrentUser()
                        .getUserId()
                        .equals(ReflectionUtils.getFieldValue(object, buyerIdField))) {
                    return object;
                } else {
                    //todo
                    throw new BusinessException(ResultEnum.USER_AUTHORITY_ERROR);
                }
            }
            case STORE -> {
                if (SecurityUtils.getCurrentUser()
                        .getStoreId()
                        .equals(ReflectionUtils.getFieldValue(object, storeIdField))) {
                    return object;
                } else {
                    throw new BusinessException(ResultEnum.USER_AUTHORITY_ERROR);
                }
            }
            default -> throw new BusinessException(ResultEnum.USER_AUTHORITY_ERROR);
        }
    }
}
