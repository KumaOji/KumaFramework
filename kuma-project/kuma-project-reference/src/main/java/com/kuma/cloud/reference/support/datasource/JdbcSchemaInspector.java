package com.kuma.cloud.reference.support.datasource;

import com.kuma.cloud.reference.domain.vo.SchemaVO;
import com.kuma.cloud.reference.domain.enums.DataScope;
import com.kuma.cloud.reference.mapper.OrderMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class JdbcSchemaInspector {
    private final DynamicSchemaExecutor schemas;
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transactions;
    private final OrderMapper orders;

    public JdbcSchemaInspector(DynamicSchemaExecutor schemas, JdbcTemplate jdbc, TransactionTemplate transactions, OrderMapper orders) {
        this.schemas = schemas;
        this.jdbc = jdbc;
        this.transactions = transactions;
        this.orders = orders;
    }

    public SchemaVO inspect(DataScope scope) {
        return schemas.execute(scope, () -> transactions.execute(status -> jdbc.execute(
                (org.springframework.jdbc.core.ConnectionCallback<SchemaVO>) connection -> {
                    long count = orders.count(null, null);
                    return new SchemaVO(scope.name(), scope.dataSource(), connection.getCatalog(),
                            connection.getSchema(), connection.getMetaData().getUserName(), count);
                })));
    }
}
