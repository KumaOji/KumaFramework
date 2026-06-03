package com.kuma.boot.mq.common.consumer;

@FunctionalInterface
public interface Acknowledgement {
    void acknowledge();
}
