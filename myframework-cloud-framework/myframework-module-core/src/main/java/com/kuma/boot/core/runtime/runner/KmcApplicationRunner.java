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

package com.kuma.boot.core.runtime.runner;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.common.PropertyUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * CoreApplicationRunner
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:45:34
 */
public class KmcApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments var1) throws Exception {
        LogUtils.info("----------- application [{}] 已正常启动 -----------", PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY));
    }
}
