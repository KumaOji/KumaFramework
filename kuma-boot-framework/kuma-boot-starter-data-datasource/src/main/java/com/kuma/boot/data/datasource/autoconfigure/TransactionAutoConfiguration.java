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

import com.kuma.boot.data.datasource.tx.TxWrapper;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

/**
 * 1、少用@Transactional注解 2、将查询(select)方法放到事务外 3、事务中避免远程调用 4、事务中避免一次性处理太多数据 5、非事务执行 6、异步处理
 *
 * @author kuma
 * @version 2022.03
 * @since 2022/03/09 12:16
 */
@AutoConfiguration(after = TransactionExecutorAutoConfiguration.class)
@Import({TxWrapper.class})
// @EnableTransactionManagement
public class TransactionAutoConfiguration {

    // implements PlatformTransactionManagerCustomizer<AbstractPlatformTransactionManager> {
    //
    // @Override
    // public void customize(AbstractPlatformTransactionManager transactionManager) {
    //
    // }
    private final PlatformTransactionManager platformTransactionManager;
    private final TransactionDefinition transactionDefinition;
    private final ThreadPoolExecutor threadPoolExecutor;

    public TransactionAutoConfiguration(
            PlatformTransactionManager platformTransactionManager,
            TransactionDefinition transactionDefinition,
            @Autowired @Qualifier("transactionThreadPoolExecutor")
            ThreadPoolExecutor threadPoolExecutor) {
        this.platformTransactionManager = platformTransactionManager;
        this.transactionDefinition = transactionDefinition;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public void execute(Task task) {
        // DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //// 定义该事务的名称、事务级别
        // def.setName("txTest");
        // def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus transactionStatus =
                platformTransactionManager.getTransaction(transactionDefinition);
        try {
            task.run();
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            platformTransactionManager.rollback(transactionStatus);
            throw new RuntimeException("");
        }
    }

    public <T> Optional<T> execute(Supplier<T> supplier) {
        TransactionStatus transactionStatus =
                platformTransactionManager.getTransaction(transactionDefinition);
        T t = null;
        try {
            t = supplier.get();
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            platformTransactionManager.rollback(transactionStatus);
            throw new RuntimeException("");
        }
        return Optional.ofNullable(t);
    }

    public void asyncExecute(Task task) {
        threadPoolExecutor.execute(
                () -> {
                    TransactionStatus transactionStatus =
                            platformTransactionManager.getTransaction(transactionDefinition);
                    try {
                        task.run();
                        platformTransactionManager.commit(transactionStatus);
                    } catch (Exception e) {
                        platformTransactionManager.rollback(transactionStatus);
                    }
                });
    }

    public <T> Optional<T> asyncExecute(Supplier<T> supplier) {
        Future<T> future =
                threadPoolExecutor.submit(
                        () -> {
                            TransactionStatus transactionStatus =
                                    platformTransactionManager.getTransaction(
                                            transactionDefinition);
                            T t = null;
                            try {
                                t = supplier.get();
                                platformTransactionManager.commit(transactionStatus);
                            } catch (Exception e) {
                                platformTransactionManager.rollback(transactionStatus);
                            }
                            return t;
                        });

        T t = null;
        try {
            t = future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(t);
    }

    @FunctionalInterface
    public interface Task {

        public void run();
    }

    // @Bean
    // public PlatformTransactionManager txManager(final DataSource dataSource) {
    //	return new DataSourceTransactionManager(dataSource);
    // }
    //
    // @Bean
    // public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
    //	return new JdbcTemplate(dataSource);
    // }

}
