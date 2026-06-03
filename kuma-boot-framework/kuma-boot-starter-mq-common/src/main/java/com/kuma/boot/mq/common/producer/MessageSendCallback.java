package com.kuma.boot.mq.common.producer;

public interface MessageSendCallback {
    void onSuccess(MessageSendResult result);

    void onFailed(Throwable throwable);
}
