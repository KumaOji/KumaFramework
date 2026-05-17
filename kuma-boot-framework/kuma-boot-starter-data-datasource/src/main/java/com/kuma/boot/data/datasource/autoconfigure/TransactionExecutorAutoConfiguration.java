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

package com.kuma.boot.data.datasource.autoconfigure;

import com.kuma.boot.core.support.thread.MDCThreadPoolExecutor;
import com.kuma.boot.core.support.thread.ThreadPoolFactory;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 1、少用@Transactional注解 2、将查询(select)方法放到事务外 3、事务中避免远程调用 4、事务中避免一次性处理太多数据 5、非事务执行 6、异步处理
 *
 * @author kuma
 * @version 2022.03
 * @since 2022/03/09 12:16
 */
@AutoConfiguration
public class TransactionExecutorAutoConfiguration {

    @Bean("transactionThreadPoolExecutor")
    public ThreadPoolExecutor transactionThreadPoolExecutor() {
        MDCThreadPoolExecutor transactionThreadPoolExecutor =
                new MDCThreadPoolExecutor(
                        20,
                        100,
                        60,
                        TimeUnit.SECONDS,
                        new SynchronousQueue<>(),
                        new ThreadPoolFactory("kmc-transaction-executor", true));

        transactionThreadPoolExecutor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy());
        return transactionThreadPoolExecutor;
    }
}
