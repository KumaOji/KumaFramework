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

package com.kuma.cloud.stream.framework.trigger.model;

/** 延时任务执行器常量 */
public interface TimeExecuteConstant {

    /** 促销延迟加载执行器 */
    public static final String PROMOTION_EXECUTOR = "promotionTimeTriggerExecutor";

    /** 直播间延迟加载执行器 */
    public static final String BROADCAST_EXECUTOR = "broadcastTimeTriggerExecutor";
}
