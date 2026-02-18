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

package com.kuma.boot.common.support.generator.bank;

import com.kuma.boot.common.support.generator.util.LuhnUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 *
 * <pre>
 * 银行卡号校验类
 * </pre>
 */
public class BankCardNumberValidator {

    /**
     * 校验银行卡号是否合法
     * @param cardNo 银行卡号
     * @return 是否合法
     */
    public static boolean validate(String cardNo) {
        if (StringUtils.isEmpty(cardNo)) {
            return false;
        }

        if (!NumberUtils.isDigits(cardNo)) {
            return false;
        }

        if (cardNo.length() > 19 || cardNo.length() < 16) {
            return false;
        }

        int luhnSum =
                LuhnUtils.getLuhnSum(cardNo.substring(0, cardNo.length() - 1).trim().toCharArray());
        char checkCode = (luhnSum % 10 == 0) ? '0' : (char) ((10 - luhnSum % 10) + '0');
        return cardNo.substring(cardNo.length() - 1).charAt(0) == checkCode;
    }
}
