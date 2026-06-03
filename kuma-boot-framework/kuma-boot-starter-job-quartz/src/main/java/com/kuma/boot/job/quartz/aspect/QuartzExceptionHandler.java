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

package com.kuma.boot.job.quartz.aspect;

/** Quartz 任务异常处理器接口，实现此接口并注册为 Spring Bean 可接收任务执行异常通知 */
public interface QuartzExceptionHandler {

    /**
     * 任务执行异常回调。
     *
     * @param jobName   任务名称
     * @param jobGroup  任务分组
     * @param timestamp 发生时间戳（毫秒）
     * @param e         异常信息
     */
    void handleException(String jobName, String jobGroup, long timestamp, Exception e);
}
