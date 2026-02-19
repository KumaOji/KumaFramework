/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.core.annotation.Order
 *  org.springframework.web.bind.annotation.ControllerAdvice
 *  org.springframework.web.bind.annotation.ExceptionHandler
 *  org.springframework.web.bind.annotation.ResponseBody
 */
package com.kuma.boot.web.gracefulresponse.advice;

import com.kuma.boot.web.gracefulresponse.ExceptionAliasRegister;
import com.kuma.boot.web.gracefulresponse.GracefulResponseException;
import com.kuma.boot.web.gracefulresponse.GracefulResponseProperties;
import com.kuma.boot.web.gracefulresponse.api.ExceptionAliasFor;
import com.kuma.boot.web.gracefulresponse.api.ExceptionMapper;
import com.kuma.boot.web.gracefulresponse.api.ResponseFactory;
import com.kuma.boot.web.gracefulresponse.api.ResponseStatusFactory;
import com.kuma.boot.web.gracefulresponse.data.Response;
import com.kuma.boot.web.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Order(value=200)
public class GlobalExceptionAdvice
implements ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionAdvice.class);
    @Resource
    private ResponseStatusFactory responseStatusFactory;
    @Resource
    private ResponseFactory responseFactory;
    private ExceptionAliasRegister exceptionAliasRegister;
    @Resource
    private GracefulResponseProperties gracefulResponseProperties;
    @Resource
    private GracefulResponseProperties properties;

    @ExceptionHandler(value={Throwable.class})
    @ResponseBody
    public Response exceptionHandler(Throwable throwable) {
        if (this.gracefulResponseProperties.isPrintExceptionInGlobalAdvice()) {
            this.logger.error("Graceful Response:GlobalExceptionAdvice\u6355\u83b7\u5230\u5f02\u5e38,message=[{}]", (Object)throwable.getMessage(), (Object)throwable);
        }
        ResponseStatus statusLine = throwable instanceof GracefulResponseException ? this.fromGracefulResponseExceptionInstance((GracefulResponseException)throwable) : this.fromExceptionInstance(throwable);
        return this.responseFactory.newInstance(statusLine);
    }

    private ResponseStatus fromGracefulResponseExceptionInstance(GracefulResponseException exception) {
        String code = exception.getCode();
        if (code == null) {
            code = this.properties.getDefaultErrorCode();
        }
        return this.responseStatusFactory.newInstance(code, exception.getMsg());
    }

    private ResponseStatus fromExceptionInstance(Throwable throwable) {
        String throwableMessage;
        ExceptionAliasFor exceptionAliasFor;
        Class<?> clazz = throwable.getClass();
        ExceptionMapper exceptionMapper = clazz.getAnnotation(ExceptionMapper.class);
        if (exceptionMapper != null) {
            String throwableMessage2;
            boolean msgReplaceable = exceptionMapper.msgReplaceable();
            if (msgReplaceable && (throwableMessage2 = throwable.getMessage()) != null) {
                return this.responseStatusFactory.newInstance(exceptionMapper.code(), throwableMessage2);
            }
            return this.responseStatusFactory.newInstance(exceptionMapper.code(), exceptionMapper.msg());
        }
        if (this.exceptionAliasRegister != null && (exceptionAliasFor = this.exceptionAliasRegister.getExceptionAliasFor(clazz)) != null) {
            return this.responseStatusFactory.newInstance(exceptionAliasFor.code(), exceptionAliasFor.msg());
        }
        ResponseStatus defaultError = this.responseStatusFactory.defaultError();
        if (this.properties.getOriginExceptionUsingDetailMessage().booleanValue() && (throwableMessage = throwable.getMessage()) != null) {
            defaultError.setMsg(throwableMessage);
        }
        return defaultError;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.exceptionAliasRegister = (ExceptionAliasRegister)applicationContext.getBean(ExceptionAliasRegister.class);
    }
}

