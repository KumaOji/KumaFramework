package com.kuma.cloud.reference.exception.handler;

import com.kuma.cloud.reference.exception.OrderNotFoundException;
import com.kuma.cloud.reference.exception.CatalogException;
import com.kuma.cloud.reference.exception.OrderConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** 使用标准 ProblemDetail；不向调用方暴露 SQL、堆栈、第三方响应体或凭据。 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(OrderNotFoundException.class)
    ProblemDetail notFound(OrderNotFoundException error) { return problem(HttpStatus.NOT_FOUND, error.getMessage()); }

    @ExceptionHandler(OrderConflictException.class)
    ProblemDetail conflict(OrderConflictException error) { return problem(HttpStatus.CONFLICT, error.getMessage()); }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail invalid(IllegalArgumentException error) { return problem(HttpStatus.BAD_REQUEST, "请求不符合业务约束"); }

    @ExceptionHandler(CatalogException.class)
    ProblemDetail catalog(CatalogException error) {
        HttpStatus status = switch (error.reason()) {
            case NOT_FOUND -> HttpStatus.UNPROCESSABLE_CONTENT;
            case TIMEOUT -> HttpStatus.GATEWAY_TIMEOUT;
            case UNAVAILABLE -> HttpStatus.BAD_GATEWAY;
        };
        log.warn("Catalog operation failed: {}", error.reason());
        return problem(status, error.getMessage());
    }

    @ExceptionHandler({TaskRejectedException.class, DataAccessException.class})
    ProblemDetail unavailable(Exception error) {
        log.error("Reference service resource unavailable", error);
        return problem(HttpStatus.SERVICE_UNAVAILABLE, "服务暂时繁忙，请稍后重试");
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail unexpected(Exception error) {
        log.error("Unexpected reference service failure", error);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "服务内部错误");
    }

    private static ProblemDetail problem(HttpStatus status, String detail) {
        return ProblemDetail.forStatusAndDetail(status, detail);
    }
}
