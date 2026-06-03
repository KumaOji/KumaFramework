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

package com.kuma.boot.dingtalk.session;

import com.kuma.boot.dingtalk.model.DingerRobot;
import com.kuma.boot.dingtalk.autoconfigure.properties.DingtalkProperties;

/**
 * Configuration
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:24:53
 */
public class SessionConfiguration {

    protected DingtalkProperties dingtalkProperties;
    protected DingerRobot dingerRobot;

    private SessionConfiguration(DingtalkProperties dingtalkProperties, DingerRobot dingerRobot) {
        this.dingtalkProperties = dingtalkProperties;
        this.dingerRobot = dingerRobot;
    }

    public static SessionConfiguration of(DingtalkProperties dingtalkProperties, DingerRobot dingerRobot) {
        return new SessionConfiguration(dingtalkProperties, dingerRobot);
    }

    public DingtalkProperties getDingerProperties() {
        return dingtalkProperties;
    }

    public DingerRobot getDingerRobot() {
        return dingerRobot;
    }
}
