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

package com.kuma.boot.common.support.generator;

import com.kuma.boot.common.support.generator.base.GenericGenerator;
import com.kuma.boot.common.utils.lang.StringUtils;
import cn.hutool.core.util.RandomUtil;

/*
 * ChineseMobileNumberGenerator
 * @author kuma
 * @version 2023.12
 * @since 2023-12-18 15:08:27
 */
/**
 * ChineseMobileNumberGenerator
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class ChineseMobileNumberGenerator extends GenericGenerator {

    private static final int[] MOBILE_PREFIX =
            new int[]{
                    133, 153, 177, 180, 181, 189, 134, 135, 136, 137, 138, 139, 150, 151, 152, 157, 158,
                    159, 178, 182, 183, 184, 187, 188, 130, 131, 132, 155, 156, 176, 185, 186, 145, 147,
                    170
            };

    private static final ChineseMobileNumberGenerator INSTANCE = new ChineseMobileNumberGenerator();

    private ChineseMobileNumberGenerator() {
    }

    public static ChineseMobileNumberGenerator getInstance() {
        return INSTANCE;
    }

    @Override
    public String generate() {
        return genMobilePre()
                + StringUtils.leftPad("" + RandomUtil.randomInt(0, 99999999 + 1), 8, "0");
    }

    /**
     * 生成假的手机号，以19开头
     */
    public String generateFake() {
        return "19" + StringUtils.leftPad("" + RandomUtil.randomInt(0, 999999999 + 1), 9, "0");
    }

    private static String genMobilePre() {
        return "" + MOBILE_PREFIX[RandomUtil.randomInt(0, MOBILE_PREFIX.length)];
    }
}
