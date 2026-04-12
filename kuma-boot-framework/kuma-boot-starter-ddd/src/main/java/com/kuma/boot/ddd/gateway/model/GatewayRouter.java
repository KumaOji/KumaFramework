package com.kuma.boot.ddd.gateway.model;

@FunctionalInterface
public interface GatewayRouter {
   Object execute(Object param);
}
