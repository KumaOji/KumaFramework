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

package com.kuma.boot.cache.redis.redisson;

/**
 * 延迟队列业务枚举
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:45:55
 */
public enum RedisDelayQueueEnum {
    ORDER_PAYMENT_TIMEOUT("ORDER_PAYMENT_TIMEOUT", "订单支付超时，自动取消订单", "orderPaymentTimeout"),
    ORDER_TIMEOUT_NOT_EVALUATED("ORDER_TIMEOUT_NOT_EVALUATED", "订单超时未评价，系统默认好评", "orderTimeoutNotEvaluated");

    /** 延迟队列 Redis Key */
    private String code;

    /** 中文描述 */
    private String name;

    /** 延迟队列具体业务实现的 Bean 可通过 Spring 的上下文获取 */
    private String beanId;

    RedisDelayQueueEnum(String code, String name, String beanId) {
        this.code = code;
        this.name = name;
        this.beanId = beanId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getBeanId() {
        return beanId;
    }
}
