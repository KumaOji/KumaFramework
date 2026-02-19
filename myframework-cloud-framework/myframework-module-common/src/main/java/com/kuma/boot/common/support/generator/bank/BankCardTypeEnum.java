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

package com.kuma.boot.common.support.generator.bank;

/**
 *
 *
 * <pre>
 * 银行卡类型枚举类
 * </pre>
 */
public enum BankCardTypeEnum {

    /** 借记卡/储蓄卡 */
    DEBIT("借记卡/储蓄卡"),
    /** 信用卡/贷记卡 */
    CREDIT("信用卡/贷记卡");

    private final String name;

    BankCardTypeEnum(String name) {
        this.name = name;
    }
}
