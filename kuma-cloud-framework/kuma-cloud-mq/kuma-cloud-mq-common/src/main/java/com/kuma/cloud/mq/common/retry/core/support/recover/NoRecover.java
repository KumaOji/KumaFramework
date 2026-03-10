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

package com.kuma.cloud.mq.common.retry.core.support.recover;

import com.kuma.boot.common.support.instance.InstanceFactory;
import com.kuma.cloud.mq.common.retry.api.model.RetryAttempt;
import com.kuma.cloud.mq.common.retry.api.support.recover.Recover;

/**
 * 不指定任何动作
 *
 * @author kuma
 * @since 0.0.1
 */
public class NoRecover implements Recover {

    /**
     * 获取一个单例示例
     *
     * @return 单例示例
     */
    public static Recover getInstance() {
        return InstanceFactory.getInstance().singleton(NoRecover.class);
    }

    @Override
    public <R> void recover(RetryAttempt<R> retryAttempt) {}
}
