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
import com.kuma.boot.common.support.generator.util.ChineseCharUtils;
import cn.hutool.core.util.RandomUtil;

/*
 * ChineseAddressGenerator
 * @author kuma
 * @version 2023.12
 * @since 2023-12-18 14:59:10
 */
/**
 * ChineseAddressGenerator
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class ChineseAddressGenerator extends GenericGenerator {

    private static final GenericGenerator INSTANCE = new ChineseAddressGenerator();

    private ChineseAddressGenerator() {
    }

    public static GenericGenerator getInstance() {
        return INSTANCE;
    }

    @Override
    public String generate() {
        return genProvinceAndCity()
                + ChineseCharUtils.genRandomLengthChineseChars(2, 3)
                + "路"
                + RandomUtil.randomInt(1, 8000)
                + "号"
                + ChineseCharUtils.genRandomLengthChineseChars(2, 3)
                + "小区"
                + RandomUtil.randomInt(1, 20)
                + "单元"
                + RandomUtil.randomInt(101, 2500)
                + "室";
    }

    private static String genProvinceAndCity() {
        return ChineseAreaList.provinceCityList.get(
                RandomUtil.randomInt(0, ChineseAreaList.provinceCityList.size()));
    }
}
