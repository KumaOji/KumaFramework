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

package com.kuma.cloud.stream.framework.trigger.interfaces;

import com.kuma.cloud.stream.framework.trigger.model.TimeTriggerMsg;

/** 延时执行接口 */
public interface TimeTrigger {

    /**
     * 添加延时任务
     *
     * @param timeTriggerMsg 延时任务信息
     */
    void addDelay(TimeTriggerMsg timeTriggerMsg);

    /**
     * 执行延时任务
     *
     * @param timeTriggerMsg 延时任务信息
     */
    void execute(TimeTriggerMsg timeTriggerMsg);

    /**
     * 修改延时任务
     *
     * @param executorName 执行器beanId
     * @param param 执行参数
     * @param triggerTime 执行时间 时间戳 秒为单位
     * @param oldTriggerTime 旧的任务执行时间
     * @param uniqueKey 添加任务时的唯一凭证
     * @param delayTime 延时时间（秒）
     * @param topic rocketmq topic
     */
    void edit(
            String executorName,
            Object param,
            Long oldTriggerTime,
            Long triggerTime,
            String uniqueKey,
            int delayTime,
            String topic);

    /**
     * 删除延时任务
     *
     * @param executorName 执行器
     * @param triggerTime 执行时间
     * @param uniqueKey 添加任务时的唯一凭证
     * @param topic rocketmq topic
     */
    void delete(String executorName, Long triggerTime, String uniqueKey, String topic);
}
