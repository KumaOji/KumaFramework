package com.kuma.cloud.reference.service.impl;

import com.kuma.cloud.reference.domain.dto.CreateOrderDTO;
import com.kuma.cloud.reference.domain.entity.OrderDO;
import com.kuma.cloud.reference.domain.enums.DataScope;
import com.kuma.cloud.reference.domain.model.Order;
import com.kuma.cloud.reference.domain.qo.OrderPageQO;
import com.kuma.cloud.reference.domain.vo.OrderVO;
import com.kuma.cloud.reference.domain.vo.PageVO;
import com.kuma.cloud.reference.exception.OrderConflictException;
import com.kuma.cloud.reference.exception.OrderNotFoundException;
import com.kuma.cloud.reference.integration.catalog.WebClientProductCatalog;
import com.kuma.cloud.reference.mapper.OrderMapper;
import com.kuma.cloud.reference.service.OrderService;
import com.kuma.cloud.reference.support.datasource.DynamicSchemaExecutor;
import java.time.Clock;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orders;
    private final WebClientProductCatalog catalog;
    private final Clock clock;
    private final DynamicSchemaExecutor schemas;
    private final TransactionTemplate writes;
    private final TransactionTemplate reads;

    public OrderServiceImpl(OrderMapper orders, WebClientProductCatalog catalog, Clock clock,
                            DynamicSchemaExecutor schemas, TransactionTemplate writes, PlatformTransactionManager manager) {
        this.schemas = schemas;
        this.writes = writes;
        this.reads = new TransactionTemplate(manager);
        this.reads.setReadOnly(true);
        this.reads.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        this.reads.setTimeout(3);
        this.orders = orders;
        this.catalog = catalog;
        this.clock = clock;
    }

    @Override
    public OrderVO create(CreateOrderDTO request, DataScope scope) {
        // 外部 HTTP 调用先完成，等待网络响应时不占用数据库事务连接。
        var product = catalog.findBySku(request.sku());
        var order = Order.create(product, request.quantity(), clock.instant());
        return inScope(scope, writes, () -> {
            if (orders.insert(OrderDO.from(order)) != 1) {
                throw new DataAccessResourceFailureException("订单插入未生效");
            }
            return toVO(order);
        });
    }

    @Override
    public OrderVO get(UUID id, DataScope scope) {
        return inScope(scope, reads, () -> toVO(requireOrder(id)));
    }

    @Override
    public PageVO<OrderVO> page(OrderPageQO query) {
        return inScope(query.scope(), reads, () -> {
            var items = orders.selectPage(query.sku(), query.status(), (query.page() - 1) * query.size(), query.size());
            return new PageVO<>(items.stream().map(OrderDO::toDomain).map(OrderServiceImpl::toVO).toList(),
                    orders.count(query.sku(), query.status()), query.page(), query.size());
        });
    }

    @Override
    public OrderVO confirm(UUID id, DataScope scope) {
        return inScope(scope, writes, () -> {
            var previous = requireOrder(id);
            var confirmed = previous.confirm();
            if (orders.updateStatus(id.toString(), confirmed.status().name(), confirmed.version(), previous.version()) != 1) {
                throw new OrderConflictException("订单已被其他请求修改，请重新读取后重试");
            }
            return toVO(confirmed);
        });
    }

    private Order requireOrder(UUID id) {
        var row = orders.selectById(id.toString());
        if (row == null) throw new OrderNotFoundException(id);
        return row.toDomain();
    }

    private <T> T inScope(DataScope scope, TransactionTemplate transaction, Supplier<T> operation) {
        // 路由先于事务获取连接，分页查询共用同一只读事务。
        return schemas.execute(scope, () -> transaction.execute(status -> operation.get()));
    }

    private static OrderVO toVO(Order order) {
        return new OrderVO(order.id(), order.product().sku(), order.product().name(),
                order.product().unitPrice().amount(), order.quantity(), order.total().amount(),
                order.status().name(), order.version(), order.createdAt());
    }
}
