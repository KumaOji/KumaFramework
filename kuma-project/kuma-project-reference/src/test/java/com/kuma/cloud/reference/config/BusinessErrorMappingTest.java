package com.kuma.cloud.reference.config;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.Code;
import com.kuma.boot.web.error.ErrorHandlingProperties;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class BusinessErrorMappingTest {
    private final ErrorHandlingProperties properties = new ErrorHandlingProperties();
    private final ErrorCodeMapper codes = new ErrorCodeMapper(properties);
    private final ErrorMessageMapper messages = new ErrorMessageMapper(properties);

    @Test
    void preservesFrameworkEnumAndExplicitCodeInsteadOfExceptionClassMappings() {
        properties.getCodes().put(BusinessException.class.getName(), "CLASS_MAPPING");
        assertEquals(ResultEnum.BAD_REQUEST.code().getCode(),
                codes.getErrorCode(new BusinessException(ResultEnum.BAD_REQUEST)));
        assertEquals("502001", codes.getErrorCode(new BusinessException(Code.raw("502001"), "订单错误")));
    }

    @Test
    void frameworkBusinessExceptionsDetermineHttpStatusesWithoutProjectSubclasses() {
        var statuses = new HttpStatusMapper(properties);
        var missing = new BusinessException(ResultEnum.DATA_NOT_EXIST_ERROR, "订单不存在：1");
        var conflict = new BusinessException(ResultEnum.REQUEST_OTHER_OPERATION, "版本冲突");
        assertEquals(ResultEnum.DATA_NOT_EXIST_ERROR.code().getCode(), codes.getErrorCode(missing));
        assertEquals(ResultEnum.REQUEST_OTHER_OPERATION.code().getCode(), codes.getErrorCode(conflict));
        assertEquals(HttpStatus.NOT_FOUND, statuses.getHttpStatus(missing));
        assertEquals(HttpStatus.CONFLICT, statuses.getHttpStatus(conflict));
        assertEquals(HttpStatus.BAD_REQUEST, statuses.getHttpStatus(new BusinessException(ResultEnum.BAD_REQUEST)));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statuses.getHttpStatus(new BusinessException(Code.raw("404-UNKNOWN"), "自定义错误")));
        properties.getHttpStatuses().put(BusinessException.class.getName(), HttpStatus.SERVICE_UNAVAILABLE);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, statuses.getHttpStatus(conflict));
    }

    @Test
    void genericInternalErrorMessageDoesNotHideBusinessMessages() {
        properties.setSearchSuperClassHierarchy(true);
        properties.getMessages().put(Exception.class.getName(), "服务内部错误");
        assertEquals("版本冲突", messages.getErrorMessage(new BusinessException(ResultEnum.REQUEST_OTHER_OPERATION, "版本冲突")));
        assertEquals("服务内部错误", messages.getErrorMessage(new IllegalStateException("internal detail")));
        properties.getCodes().put(IllegalStateException.class.getName(), "CONFIGURED_CODE");
        assertEquals("CONFIGURED_CODE", codes.getErrorCode(new IllegalStateException()));
    }

    @Test
    void resultEnumStrategyAlsoCoversTechnicalAndFieldValidationErrors() {
        properties.setDefaultErrorCodeStrategy(ErrorHandlingProperties.DefaultErrorCodeStrategy.RESULT_ENUM);
        assertEquals(ResultEnum.BAD_REQUEST.codeDesc(), codes.getErrorCode(new ArithmeticException("overflow")));
        assertEquals(ResultEnum.BAD_REQUEST.codeDesc(), codes.getErrorCode("quantity.Min", "Min"));
        assertEquals(ResultEnum.INNER_ERROR.codeDesc(), codes.getErrorCode(new IllegalStateException("internal")));
    }
}
