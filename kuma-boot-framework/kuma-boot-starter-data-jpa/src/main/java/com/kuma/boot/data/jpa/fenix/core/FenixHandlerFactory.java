package com.kuma.boot.data.jpa.fenix.core;

@FunctionalInterface
public interface FenixHandlerFactory {
   FenixHandler newInstance();
}
