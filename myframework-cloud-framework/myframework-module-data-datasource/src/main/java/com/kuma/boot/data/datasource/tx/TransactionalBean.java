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

package com.kuma.boot.data.datasource.tx;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * TransactionalBean
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@Component
public class TransactionalBean {

    @Transactional
    public void someTransactionalMethod() {
        // 业务逻辑...
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit() {
        // 在事务提交前执行的逻辑...
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit() {
        // 在事务提交后执行的逻辑...
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void afterCompletion( TransactionPhase phase ) {
        // 在事务完成后执行的逻辑，可以获取事务结束的阶段
        switch (phase) {
            case AFTER_COMMIT:
                // 事务已提交后执行的逻辑...
                break;
            case AFTER_ROLLBACK:
                // 事务回滚后执行的逻辑...
                break;
            case AFTER_COMPLETION:
                // 事务完成后执行的逻辑...
                break;
        }
    }
}
