package com.kuma.boot.data.datasource.autoconfigure;

import com.kuma.boot.data.datasource.tx.TxWrapper;
import com.kuma.boot.data.datasource.tx.TransactionalUtils;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionAutoConfigurationTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(TransactionExecutorAutoConfiguration.class,
                    TransactionSupportAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
                    org.springframework.boot.transaction.autoconfigure.TransactionAutoConfiguration.class));

    @Configuration(proxyBeanMethods = false)
    @ComponentScan(basePackageClasses = TransactionSupportAutoConfiguration.class, useDefaultFilters = false,
            includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                    classes = {TransactionSupportAutoConfiguration.class, TransactionExecutorAutoConfiguration.class}))
    static class ScannedTransactions {}

    @Test
    void scanningWithoutDatabaseDoesNotRequireATransactionManager() {
        runner.withUserConfiguration(ScannedTransactions.class, KmcDataSourceAutoConfiguration.class).run(context ->
                assertThat(context).hasNotFailed().doesNotHaveBean(TransactionAutoConfiguration.class)
                        .doesNotHaveBean(TxWrapper.class).doesNotHaveBean(TransactionalUtils.class));
    }

    @Test
    void jdbcManagerCreatedAfterComponentScanEnablesTransactionHelpers() {
        runner.withUserConfiguration(ScannedTransactions.class)
                .withBean(DataSource.class, () -> mock(DataSource.class))
                .run(context -> assertThat(context).hasNotFailed().hasSingleBean(JdbcTransactionManager.class)
                        .hasSingleBean(TransactionAutoConfiguration.class).hasSingleBean(TxWrapper.class)
                        .hasSingleBean(TransactionalUtils.class));
    }

    @Test
    void managerWithoutCustomDefinitionUsesDefaultAndCommits() {
        var manager = mock(PlatformTransactionManager.class);
        var status = mock(TransactionStatus.class);
        when(manager.getTransaction(any())).thenReturn(status);
        runner.withBean(PlatformTransactionManager.class, () -> manager).run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBean(TransactionAutoConfiguration.class).execute(() -> "done")).contains("done");
            verify(manager).getTransaction(argThat(def ->
                    def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED));
            verify(manager).commit(status);
        });
    }

    @Test
    void preservesCustomTransactionDefinition() {
        var manager = mock(PlatformTransactionManager.class);
        var definition = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        runner.withBean(PlatformTransactionManager.class, () -> manager)
                .withBean(TransactionDefinition.class, () -> definition).run(context -> {
                    assertThat(context).hasNotFailed();
                    context.getBean(TransactionAutoConfiguration.class)
                            .execute((TransactionAutoConfiguration.Task) () -> {});
                    verify(manager).getTransaction(definition);
                });
    }
}
