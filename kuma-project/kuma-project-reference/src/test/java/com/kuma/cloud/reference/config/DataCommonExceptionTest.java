package com.kuma.cloud.reference.config;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.model.Code;
import com.kuma.boot.data.common.exception.DataCommonException;
import com.kuma.boot.data.datasource.dynamic.mybatisplus.DataSourceExecutor;
import com.kuma.boot.data.datasource.tx.TransactionalUtils;
import com.kuma.boot.web.error.DefaultFallbackApiExceptionHandler;
import com.kuma.boot.web.error.ErrorHandlingProperties;
import com.kuma.boot.web.error.mapper.*;
import com.kuma.cloud.reference.domain.dto.CreateOrderDTO;
import com.kuma.cloud.reference.domain.enums.DataScope;
import com.kuma.cloud.reference.mapper.OrderMapper;
import com.kuma.cloud.reference.service.impl.OrderServiceImpl;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataCommonExceptionTest {
    @Test
    void constructorsPreserveFrameworkCodeMessageCauseAndStatus() {
        var cause = new SQLException("database failure");
        var failure = new DataCommonException(ResultEnum.FAILED, "订单保存失败", cause);
        assertEquals(ResultEnum.FAILED.codeDesc(), failure.getCode().getCode());
        assertEquals("订单保存失败", failure.getMessage());
        assertSame(cause, failure.getCause());
        var code = Code.raw("REFERENCE-SAVE-FAILED");
        var custom = new DataCommonException(StatusEnum.FAILURE, code, cause, "自定义数据库错误");
        assertSame(code, custom.getCode());
        assertSame(cause, custom.getCause());
        assertEquals(StatusEnum.FAILURE, custom.getStatus());
        assertEquals("自定义数据库错误", custom.getMessage());
        assertEquals(ResultEnum.FAILED.getDesc(), new DataCommonException(ResultEnum.FAILED).getMessage());
        assertEquals("保存失败", new DataCommonException("保存失败", cause).getMessage());
    }

    @Test
    void unsuccessfulInsertRollsBackAndUsesFrameworkDataExceptionResponse() {
        var orders = mock(OrderMapper.class);
        var manager = mock(PlatformTransactionManager.class);
        var service = service(orders, manager);
        when(orders.insert(any(com.kuma.cloud.reference.domain.entity.OrderDO.class))).thenReturn(0);
        var failure = assertThrows(DataCommonException.class, () -> service.create(request(), DataScope.MAIN));
        assertEquals("订单插入未生效", failure.getMessage());
        assertEquals(ResultEnum.FAILED.codeDesc(), failure.getCode().getCode());
        verify(manager).rollback(any());
        verify(manager, never()).commit(any());

        var properties = new ErrorHandlingProperties();
        properties.setSearchSuperClassHierarchy(true);
        properties.getMessages().put(Exception.class.getName(), "服务内部错误");
        var handler = new DefaultFallbackApiExceptionHandler(new HttpStatusMapper(properties),
                new ErrorCodeMapper(properties), new ErrorMessageMapper(properties));
        var response = handler.handle(failure);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertEquals(ResultEnum.FAILED.codeDesc(), response.getCode());
        // Keep diagnostics on the exception; retain the configured public error message.
        assertEquals("服务内部错误", response.getMessage());
    }

    @Test
    void actualDatabaseFailureRetainsItsOriginalExceptionAndRollsBack() {
        var orders = mock(OrderMapper.class);
        var manager = mock(PlatformTransactionManager.class);
        var service = service(orders, manager);
        var failure = new DataAccessResourceFailureException("connection unavailable");
        when(orders.insert(any(com.kuma.cloud.reference.domain.entity.OrderDO.class))).thenThrow(failure);
        assertSame(failure, assertThrows(DataAccessResourceFailureException.class,
                () -> service.create(request(), DataScope.MAIN)));
        verify(manager).rollback(any());
    }

    private OrderServiceImpl service(OrderMapper orders, PlatformTransactionManager manager) {
        var schemas = mock(DataSourceExecutor.class);
        when(schemas.execute(eq(DataScope.MAIN.dataSource()), eq(DataScope.MAIN.tenant()), any()))
                .thenAnswer(call -> call.getArgument(2, Supplier.class).get());
        when(manager.getTransaction(any())).thenAnswer(call -> new SimpleTransactionStatus());
        return new OrderServiceImpl(orders, Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC),
                schemas, new TransactionalUtils(new TransactionTemplate(manager)));
    }

    private CreateOrderDTO request() {
        return new CreateOrderDTO("BOOK-001", "示例商品", new BigDecimal("99.00"), 1);
    }
}
