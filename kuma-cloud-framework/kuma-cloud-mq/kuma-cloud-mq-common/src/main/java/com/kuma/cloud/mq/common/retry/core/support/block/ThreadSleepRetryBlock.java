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

package com.kuma.cloud.mq.common.retry.core.support.block;

import com.kuma.boot.common.support.instance.InstanceFactory;
import com.kuma.cloud.mq.common.retry.api.exception.RetryException;
import com.kuma.cloud.mq.common.retry.api.model.WaitTime;
import com.kuma.cloud.mq.common.retry.api.support.block.RetryBlock;

/**
 * 线程沉睡的阻塞方式
 * @author kuma
 * @since 0.0.1
 */
public class ThreadSleepRetryBlock implements RetryBlock {

    /**
     * 获取单例
     * @return 获取单例
     */
    public static RetryBlock getInstance() {
        return InstanceFactory.getInstance().singleton(ThreadSleepRetryBlock.class);
    }

    @Override
    public void block(WaitTime waitTime) {
        try {
            waitTime.unit().sleep(waitTime.time());
        } catch (InterruptedException e) {
            // restore status
            Thread.currentThread().interrupt();

            throw new RetryException(e);
        }
    }
}
