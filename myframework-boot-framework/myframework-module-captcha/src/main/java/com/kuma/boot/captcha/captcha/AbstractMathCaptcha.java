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

package com.kuma.boot.captcha.captcha;

import java.util.Random;

public abstract class AbstractMathCaptcha extends BaseCaptcha {

    /**
     * 生成随机加减验证码
     *
     * @return 验证码字符数组
     */
    @Override
    protected char[] alphas() {
        // 生成随机类
        Random random = new Random();
        char[] cs = new char[4];
        int rand0 = random.nextInt(10);
        if (rand0 == 0) {
            rand0 = 1;
        }
        int rand1 = random.nextInt(10);
        boolean rand2 = random.nextBoolean();
        int rand3 = random.nextInt(10);
        cs[0] = (char) ('0' + rand0);
        cs[1] = (char) ('0' + rand1);
        cs[2] = rand2 ? '+' : '-';
        cs[3] = (char) ('0' + rand3);

        int num1 = rand0 * 10 + rand1;
        int num2 = rand3;
        int result = rand2 ? num1 + num2 : num1 - num2;
        chars = String.valueOf(result);
        return cs;
    }
}
