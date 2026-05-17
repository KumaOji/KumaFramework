package com.kuma.boot.common.support.strategy;

@FunctionalInterface
public interface BusinessHandler {

    <T, R> R businessHandler(T t);
}
