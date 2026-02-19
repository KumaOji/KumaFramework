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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * TransactionSynchronizationManagerUtil
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class TransactionSynchronizationManagerUtil {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // 判断当前是否存在事务、添加钩子函数都是依赖线程变量的。因此，我们在使用过程中，一定要避免切换线程。否则会出现不生效的情况。
    public void sendLog() {
        // 判断当前是否存在事务
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            // 无事务，异步发送消息给kafka

            executor.submit(
                    () -> {
                        // 发送消息给kafka
                        try {
                            // 发送消息给kafka
                        } catch (Exception e) {
                            // 记录异常信息，发邮件或者进入待处理列表，让开发人员感知异常
                        }
                    });
            return;
        }

        // 有事务，则添加一个事务同步器，并重写afterCompletion方法（此方法在事务提交后会做回调）
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void beforeCommit( boolean readOnly ) {
                        System.out.println("Before commit: Check data integrity");
                    }

                    @Override
                    public void afterCommit() {
                        System.out.println("After commit: Send notification");
                        //// 事务提交后执行的逻辑
                    }

                    @Override
                    public void beforeCompletion() {
                        System.out.println("Before completion: Log transaction status");
                    }

                    @Override
                    public void afterCompletion( int status ) {
                        // // 事务提交后执行的逻辑
                        if (status == TransactionSynchronization.STATUS_COMMITTED) {
                            // 事务提交后，再异步发送消息给kafka
                            executor.submit(
                                    () -> {
                                        try {
                                            // 发送消息给kafka
                                        } catch (Exception e) {
                                            // 记录异常信息，发邮件或者进入待处理列表，让开发人员感知异常
                                        }
                                    });
                        } else {
                            // 事务回滚后执行的逻辑
                        }
                    }
                });
    }
}
