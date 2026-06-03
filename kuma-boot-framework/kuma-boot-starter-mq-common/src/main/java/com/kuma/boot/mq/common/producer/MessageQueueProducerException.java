package com.kuma.boot.mq.common.producer;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class MessageQueueProducerException extends BootException {
    public MessageQueueProducerException() {
    }

    public MessageQueueProducerException(String message) {
        super(message);
    }

    public MessageQueueProducerException(Throwable e) {
        super(e);
    }

    public MessageQueueProducerException(String message, Throwable e) {
        super(message, e);
    }

    public MessageQueueProducerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MessageQueueProducerException(ResultEnum result) {
        super(result);
    }

    public MessageQueueProducerException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public MessageQueueProducerException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public MessageQueueProducerException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public MessageQueueProducerException(Code code, String message) {
        super(code, message);
    }

    public MessageQueueProducerException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public MessageQueueProducerException(Code code, Throwable e) {
        super(code, e);
    }

    public MessageQueueProducerException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public MessageQueueProducerException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public MessageQueueProducerException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}
