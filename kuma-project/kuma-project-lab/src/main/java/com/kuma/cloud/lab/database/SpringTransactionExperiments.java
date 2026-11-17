package com.kuma.cloud.lab.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.TransactionTemplate;

import static com.kuma.cloud.lab.database.DatabaseSupport.*;

final class SpringTransactionExperiments {
    static void run() throws Exception {
        System.out.println("--- Spring事务：REQUIRES_NEW / NESTED / REQUIRED rollback-only ---");
        try (var db = new DatabaseSupport()) {
            var manager = new DataSourceTransactionManager(db.source);
            manager.setNestedTransactionAllowed(true);
            var jdbc = new JdbcTemplate(db.source); jdbc.setQueryTimeout(5);
            var required = template(manager, TransactionDefinition.PROPAGATION_REQUIRED);
            var independent = template(manager, TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            var nested = template(manager, TransactionDefinition.PROPAGATION_NESTED);

            required.executeWithoutResult(outer -> {
                jdbc.update("UPDATE accounts SET balance=balance-10 WHERE id=1");
                independent.executeWithoutResult(inner -> jdbc.update("INSERT INTO audit_log(note) VALUES(?)", "independent commit"));
                outer.setRollbackOnly();
            });
            try (var verify = db.connection()) {
                check("REQUIRES_NEW独立提交、外层回滚", number(verify, "SELECT balance FROM accounts WHERE id=1") == 100
                        && number(verify, "SELECT COUNT(*) FROM audit_log") == 1, balances(verify));
            }

            required.executeWithoutResult(outer -> {
                jdbc.update("UPDATE accounts SET balance=balance-10 WHERE id=1");
                try {
                    nested.executeWithoutResult(inner -> {
                        jdbc.update("UPDATE accounts SET balance=balance+999 WHERE id=2");
                        throw new IllegalArgumentException("nested failure");
                    });
                } catch (IllegalArgumentException expected) { System.out.println("nested failure caught after savepoint rollback"); }
                jdbc.update("UPDATE accounts SET balance=balance+10 WHERE id=2");
            });
            try (var verify = db.connection()) {
                check("NESTED局部回滚，外层仍提交", number(verify, "SELECT balance FROM accounts WHERE id=1") == 90
                        && number(verify, "SELECT balance FROM accounts WHERE id=2") == 60, balances(verify));
            }

            try {
                required.executeWithoutResult(outer -> {
                    jdbc.update("UPDATE accounts SET balance=balance-5 WHERE id=1");
                    try {
                        required.executeWithoutResult(inner -> {
                            jdbc.update("UPDATE accounts SET balance=balance+5 WHERE id=2");
                            throw new IllegalArgumentException("joined failure");
                        });
                    } catch (IllegalArgumentException expected) { System.out.println("inner REQUIRED failure caught; transaction remains rollback-only"); }
                });
                throw new AssertionError("Expected UnexpectedRollbackException");
            } catch (UnexpectedRollbackException expected) {
                try (var verify = db.connection()) {
                    check("捕获内层异常不能清除rollback-only", number(verify, "SELECT balance FROM accounts WHERE id=1") == 90
                            && number(verify, "SELECT balance FROM accounts WHERE id=2") == 60, balances(verify));
                }
            }
        }
        System.out.println("使用TransactionTemplate与真实JDBC事务，无需Spring容器；这不验证@Transactional代理或自调用失效，相关内容见既有MySQL进阶实验。");
    }

    private static TransactionTemplate template(DataSourceTransactionManager manager, int propagation) {
        var template = new TransactionTemplate(manager);
        template.setPropagationBehavior(propagation); template.setTimeout(5);
        return template;
    }
}
