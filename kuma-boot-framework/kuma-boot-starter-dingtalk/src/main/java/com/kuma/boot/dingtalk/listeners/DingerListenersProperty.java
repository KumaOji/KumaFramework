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

package com.kuma.boot.dingtalk.listeners;

import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.model.DingerConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ApplicationEventTimeTable
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:21:40
 */
public class DingerListenersProperty {

    /** dingerClasses */
    protected static List<Class<?>> dingerClasses = new ArrayList<>();
    /** Dinger默认的DingerConfig */
    protected static Map<DingerType, DingerConfig> defaultDingerConfigs = new HashMap<>();

    protected static List<Class<?>> dingerClasses() {
        return dingerClasses;
    }

    protected static void emptyDingerClasses() {
        if (dingerClasses != null && !dingerClasses.isEmpty()) {
            dingerClasses.clear();
        }
    }

    protected static final List<DingerType> enabledDingerTypes;

    static {
        enabledDingerTypes =
                Arrays.stream(DingerType.values()).filter(e -> e.isEnabled()).toList();
    }

    protected static void clear() {
        dingerClasses.clear();
        defaultDingerConfigs.clear();
    }
}
