package com.kuma.cloud.reference.service.impl;

import com.kuma.cloud.reference.domain.dto.CreateOrderDTO;
import com.kuma.cloud.reference.domain.entity.OrderDO;
import com.kuma.cloud.reference.domain.enums.DataScope;
import com.kuma.cloud.reference.domain.model.Order;
import com.kuma.cloud.reference.domain.qo.OrderPageQO;
import com.kuma.cloud.reference.domain.vo.OrderVO;
import com.kuma.boot.common.model.result.PageResultRecord;
import com.kuma.boot.data.datasource.tx.TransactionalUtils;
import com.kuma.boot.data.mybatis.mybatisplus.MpUtils;
import com.kuma.boot.data.mybatis.mybatisplus.PageUtils;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.data.common.exception.DataCommonException;
import com.kuma.boot.common.utils.number.Money;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kuma.cloud.reference.domain.model.ProductSnapshot;
import com.kuma.cloud.reference.mapper.OrderMapper;
import com.kuma.cloud.reference.service.OrderService;
import com.kuma.boot.data.datasource.dynamic.mybatisplus.DataSourceExecutor;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.function.Supplier;
import com.kuma.boot.web.annotation.Manager;
import org.springframework.transaction.TransactionDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Manager
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderMapper orders;
    private final Clock clock;
    private final DataSourceExecutor schemas;
    private final TransactionalUtils transactions;

    public OrderServiceImpl(OrderMapper orders, Clock clock,
                            DataSourceExecutor schemas, TransactionalUtils transactions) {
        this.schemas = schemas;
        this.transactions = transactions;
        this.orders = orders;
        this.clock = clock;
    }

    @Override
    public OrderVO create(CreateOrderDTO request, DataScope scope) {
        var product = new ProductSnapshot(request.sku(), request.productName(), Money.of(request.unitPrice(), "CNY"));
        var order = Order.create(product, request.quantity(), clock.instant());
        return inScope(scope, false, () -> {
            var row = OrderDO.from(order);
            if (orders.insert(row) != 1) {
                throw new DataCommonException(ResultEnum.FAILED, "订单插入未生效");
            }
            return toVO(row.toDomain());
        });
    }

    @Override
    public OrderVO get(Long id, DataScope scope) {
        return inScope(scope, true, () -> toVO(requireOrder(id)));
    }

    @Override
    public PageResultRecord<OrderVO> page(OrderPageQO query) {
        return inScope(query.getScope(), true, () -> {
            var filters = new LambdaQueryWrapper<OrderDO>()
                    .eq(query.getSku() != null, OrderDO::getSku, query.getSku())
                    .eq(query.getStatus() != null, OrderDO::getOrderStatus, query.getStatus())
                    .orderByDesc(OrderDO::getCreateDate).orderByDesc(OrderDO::getId);
            var page = orders.selectPage(MpUtils.<OrderPageQO, OrderDO>buildMpPage(query,
                    Map.of("createdAt", "create_date", "id", "id", "sku", "sku")), filters);
            return PageUtils.toResult(page, row -> toVO(row.toDomain()));
        });
    }

    @Override
    public OrderVO confirm(Long id, DataScope scope) {
        return inScope(scope, false, () -> {
            var previous = requireOrder(id);
            var confirmed = previous.confirm();
            var row = OrderDO.from(previous);
            row.setOrderStatus(confirmed.status().name());
            row.setModifyDate(LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
            // @Version and the framework optimistic-lock interceptor perform the compare-and-set.
            if (orders.updateById(row) != 1) {
                throw new BusinessException(ResultEnum.REQUEST_OTHER_OPERATION, "订单已被其他请求修改，请重新读取后重试");
            }
            return toVO(row.toDomain());
        });
    }

    private Order requireOrder(Long id) {
        var row = orders.selectById(id);
        if (row == null) throw new BusinessException(ResultEnum.DATA_NOT_EXIST_ERROR, "订单不存在：" + id);
        return row.toDomain();
    }

    private <T> T inScope(DataScope scope, boolean readOnly, Supplier<T> operation) {
        // MAIN -> referenceMain / kuma_reference_main；MYSQL -> referenceMysql / kuma_reference_mysql。
        // SchemaInterceptor combines tenant-database-prefix=kuma_reference with the tenant segment.
        log.debug("Routing order operation to datasource '{}' and schema '{}'", scope.dataSource(), scope.schema());
        // 路由先于事务获取连接，分页查询共用同一只读事务。
        return schemas.execute(scope.dataSource(), scope.tenant(), () -> transactions.defaultExecute(
                status -> operation.get(), readOnly ? TransactionDefinition.ISOLATION_REPEATABLE_READ
                        : TransactionDefinition.ISOLATION_READ_COMMITTED, readOnly));
    }

    private static OrderVO toVO(Order order) {
        return new OrderVO(order.id(), order.product().sku(), order.product().name(),
                order.product().unitPrice().getAmount(), order.quantity(), order.total().getAmount(),
                order.status().name(), order.version(), order.createdAt());
    }
}
