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

package com.kuma.boot.common.enums;

import com.kuma.boot.common.enums.base.CommonEnum;

/**
 * 支付状态
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-22 10:48:29
 */
public enum AliPayStatusEnum implements CommonEnum {

    /** 交易成功 */
    FINISHED(1, "TRADE_FINISHED", "交易成功"),

    /** 支付成功 */
    SUCCESS(2, "TRADE_SUCCESS", "支付成功"),

    /** 交易创建 */
    BUYER_PAY(3, "WAIT_BUYER_PAY", "交易创建"),

    /** 交易关闭 */
    CLOSED(4, "TRADE_CLOSED", "交易关闭");

    private final int code;

    private final String value;

    private final String desc;

    AliPayStatusEnum(int code, String value, String desc) {
        this.code = code;
        this.desc = desc;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
