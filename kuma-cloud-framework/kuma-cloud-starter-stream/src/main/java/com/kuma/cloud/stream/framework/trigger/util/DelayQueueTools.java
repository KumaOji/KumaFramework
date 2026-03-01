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

package com.kuma.cloud.stream.framework.trigger.util;

import com.kuma.cloud.stream.framework.trigger.enums.DelayTypeEnums;

/** 延时任务工具类 */
public class DelayQueueTools {

    /** 前缀 */
    private static final String PREFIX = "{rocketmq_trigger}_";

    /**
     * 组装延时任务唯一键
     *
     * @param type 延时任务类型
     * @param id id
     * @return 唯一键
     */
    public static String wrapperUniqueKey(DelayTypeEnums type, String id) {
        return "{TIME_TRIGGER_" + type.name() + "}_" + id;
    }

    /**
     * 生成延时任务标识key
     *
     * @param executorName 执行器beanId
     * @param triggerTime 执行时间
     * @param uniqueKey 自定义表示
     * @return 延时任务标识key
     */
    public static String generateKey(String executorName, Long triggerTime, String uniqueKey) {
        return PREFIX + (executorName + triggerTime + uniqueKey).hashCode();
    }
}
