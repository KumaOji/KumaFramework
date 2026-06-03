package com.kuma.boot.mq.common.consumer;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.enums.StatusEnum;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.model.Code;

public class MessageQueueConsumerException extends BootException {
    public MessageQueueConsumerException() {
    }

    public MessageQueueConsumerException(String message) {
        super(message);
    }

    public MessageQueueConsumerException(Throwable e) {
        super(e);
    }

    public MessageQueueConsumerException(String message, Throwable e) {
        super(message, e);
    }

    public MessageQueueConsumerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MessageQueueConsumerException(ResultEnum result) {
        super(result);
    }

    public MessageQueueConsumerException(StatusEnum status, ResultEnum result) {
        super(status, result);
    }

    public MessageQueueConsumerException(ResultEnum result, Throwable e) {
        super(result, e);
    }

    public MessageQueueConsumerException(StatusEnum status, ResultEnum result, Throwable e) {
        super(status, result, e);
    }

    public MessageQueueConsumerException(Code code, String message) {
        super(code, message);
    }

    public MessageQueueConsumerException(StatusEnum status, Code code, String message) {
        super(status, code, message);
    }

    public MessageQueueConsumerException(Code code, Throwable e) {
        super(code, e);
    }

    public MessageQueueConsumerException(StatusEnum status, Code code, Throwable e) {
        super(status, code, e);
    }

    public MessageQueueConsumerException(Code code, Throwable e, String message) {
        super(code, e, message);
    }

    public MessageQueueConsumerException(StatusEnum status, Code code, Throwable e, String message) {
        super(status, code, e, message);
    }
}
