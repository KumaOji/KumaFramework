package com.kuma.boot.data.datasource.autoconfigure;

import com.kuma.boot.data.datasource.tx.TxWrapper;
import com.kuma.boot.data.datasource.tx.TransactionalUtils;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/** Register transaction helpers after a transaction manager is available. */
@AutoConfiguration(after = TransactionExecutorAutoConfiguration.class,
        afterName = {"org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration",
                "org.springframework.boot.transaction.autoconfigure.TransactionAutoConfiguration"})
public class TransactionSupportAutoConfiguration {
    @Bean
    @ConditionalOnBean(PlatformTransactionManager.class)
    @ConditionalOnMissingBean
    public TransactionAutoConfiguration transactionAutoConfiguration(
            PlatformTransactionManager manager, ObjectProvider<TransactionDefinition> transactionDefinition,
            @Qualifier("transactionThreadPoolExecutor") ThreadPoolExecutor executor) {
        return new TransactionAutoConfiguration(manager,
                transactionDefinition.getIfAvailable(DefaultTransactionDefinition::new), executor);
    }

    @Bean
    @ConditionalOnBean(PlatformTransactionManager.class)
    @ConditionalOnMissingBean
    public TxWrapper txWrapper() {
        return new TxWrapper();
    }

    @Bean
    @ConditionalOnBean(TransactionTemplate.class)
    @ConditionalOnMissingBean
    public TransactionalUtils transactionalUtils(TransactionTemplate template) {
        return new TransactionalUtils(template);
    }
}
