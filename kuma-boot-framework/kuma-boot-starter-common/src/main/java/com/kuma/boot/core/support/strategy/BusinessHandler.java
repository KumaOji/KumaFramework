package com.kuma.boot.core.support.strategy;

@FunctionalInterface
public interface BusinessHandler {

    <T, R> R businessHandler(T t);
}
