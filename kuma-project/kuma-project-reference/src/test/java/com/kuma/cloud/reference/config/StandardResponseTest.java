package com.kuma.cloud.reference.config;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.web.annotation.BusinessApi;
import com.kuma.boot.web.autoconfigure.WebResponseAutoConfiguration;
import com.kuma.boot.web.error.*;
import com.kuma.boot.web.error.mapper.ErrorCodeMapper;
import com.kuma.boot.web.error.mapper.ErrorMessageMapper;
import com.kuma.boot.web.error.mapper.HttpStatusMapper;
import java.util.ArrayList;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import static org.junit.jupiter.api.Assertions.*;

class StandardResponseTest {
    @Test
    void mappedFailureRetainsCodeMessageAndAllErrorDetailsWithoutNestedEnvelope() {
        var error = new ApiErrorResponse(HttpStatus.BAD_REQUEST, ResultEnum.BAD_REQUEST.codeDesc(), "Invalid input");
        var field = new ApiFieldError(ResultEnum.BAD_REQUEST.codeDesc(), "quantity", "Must be positive", 0);
        var global = new ApiGlobalError("INVALID", "Invalid order");
        var parameter = new ApiParameterError("INVALID", "scope", "Unknown scope", "unknown");
        error.addFieldError(field);
        error.addGlobalError(global);
        error.addParameterError(parameter);
        error.addErrorProperty("help", "Check the input");

        var result = error.toResult();
        assertEquals("FAILURE", result.getStatus());
        assertEquals(error.getCode(), result.getCode());
        assertEquals(error.getMessage(), result.getMessage());
        assertEquals(error.getFieldErrors(), result.getData().get("fieldErrors"));
        assertEquals(error.getGlobalErrors(), result.getData().get("globalErrors"));
        assertEquals(error.getParameterErrors(), result.getData().get("parameterErrors"));
        assertEquals("Check the input", result.getData().get("help"));
        assertFalse(result.getData().containsKey("code"));
        assertFalse(result.getRequestId().isBlank());
    }

    @Test
    void businessApiExceptionsUseStandardResultAndKeepHttpStatus() throws Exception {
        var response = handle(BusinessController.class);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        var result = assertInstanceOf(Result.class, response.getBody());
        assertEquals("FAILURE", result.getStatus());
        assertEquals(ResultEnum.REQUEST_OTHER_OPERATION.codeDesc(), result.getCode());
        assertEquals("Version conflict", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void controllersWithoutBusinessApiKeepTheirExistingErrorContract() throws Exception {
        var response = handle(PlainController.class);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertInstanceOf(ApiErrorResponse.class, response.getBody());
    }

    @Test
    void responseAdviceDoesNotWrapAnExistingResultAgain() throws Exception {
        var result = Result.success("payload");
        var method = new MethodParameter(BusinessController.class.getMethod("get"), -1);
        var actual = new WebResponseAutoConfiguration().beforeBodyWrite(result, method,
                MediaType.APPLICATION_JSON, null,
                new ServletServerHttpRequest(new MockHttpServletRequest()),
                new ServletServerHttpResponse(new MockHttpServletResponse()));
        assertSame(result, actual);
    }

    @Test
    void messageOnlyFailuresUseFailureCode() {
        assertEquals(ResultEnum.FAILED.codeDesc(), Result.fail("Failed").getCode());
        var withData = Result.fail("Failed", "details");
        assertEquals(ResultEnum.FAILED.codeDesc(), withData.getCode());
        assertEquals("FAILURE", withData.getStatus());
        assertEquals("details", withData.getData());
    }

    private org.springframework.http.ResponseEntity<?> handle(Class<?> controller) throws Exception {
        var properties = new ErrorHandlingProperties();
        var fallback = new DefaultFallbackApiExceptionHandler(new HttpStatusMapper(properties),
                new ErrorCodeMapper(properties), new ErrorMessageMapper(properties));
        var advice = new ErrorHandlingControllerAdvice(properties, new ArrayList<>(), fallback);
        var request = new MockHttpServletRequest();
        request.setAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE,
                new HandlerMethod(controller.getDeclaredConstructor().newInstance(), controller.getMethod("get")));
        return advice.handleException(new BusinessException(ResultEnum.REQUEST_OTHER_OPERATION, "Version conflict"),
                new ServletWebRequest(request), Locale.ROOT);
    }

    @BusinessApi
    static class BusinessController {
        public Object get() { return null; }
    }

    static class PlainController {
        public Object get() { return null; }
    }
}
