//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.ddd.gateway.model;

@FunctionalInterface
public interface GatewayRouter<P> {
   Object execute(P param);
}
