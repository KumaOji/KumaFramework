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

package com.kuma.boot.data.datasource.utils;

import com.google.common.collect.Lists;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 */
public class BatchUtils<T> {

    private final PlatformTransactionManager platformTransactionManager;
    private final ThreadPoolTaskExecutor taskExecutor;

    public BatchUtils(
            PlatformTransactionManager platformTransactionManager,
            @Qualifier("asyncThreadPoolTaskExecutor") ThreadPoolTaskExecutor taskExecutor) {
        this.platformTransactionManager = platformTransactionManager;
        this.taskExecutor = taskExecutor;
    }

    public TransactionTemplate transactionTemplate() {
        TransactionTemplate transactionTemplate =
                new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setReadOnly(false);
        // 设置事务传播行为
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        // 事务隔离级别设置未读已提交
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        // 事务超时时间 单位s
        transactionTemplate.setTimeout(120);
        transactionTemplate.setName("kuma-transaction-template");
        return transactionTemplate;
    }

    /**
     * 批量新增
     *
     * @param dataList 集合
     * @param batchNum 每组多少条数据
     * @param service  基础service
     */
    public void insertBatch(List<T> dataList, int batchNum, Consumer<List<T>> service) {
        // 数据分组
        List<List<T>> partition = Lists.partition(dataList, batchNum);
        AtomicBoolean rollback = new AtomicBoolean(false);
        List<CompletableFuture<Void>> futures = new ArrayList<>(partition.size());

        partition.forEach(
                item -> {
                    futures.add(
                            CompletableFuture.runAsync(
                                    () -> {
                                        transactionTemplate()
                                                .executeWithoutResult(
                                                        callback -> {
                                                            try {
                                                                // service.insertBatch(item);
                                                                service.accept(item);
                                                            } catch (Exception e) {
                                                                // 回滚标识
                                                                rollback.compareAndSet(false, true);
                                                                LogUtils.error(
                                                                        "错误信息：批量插入数据异常，设置回滚标识");
                                                            } finally {
                                                                if (rollback.get()) {
                                                                    callback.setRollbackOnly();
                                                                }
                                                            }
                                                        });
                                    },
                                    taskExecutor));
                });
        // 阻塞主线程
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        if (rollback.get()) {
            throw new RuntimeException("批量插入数据异常，数据已回滚");
        }
    }
}
