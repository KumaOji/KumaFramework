package com.kuma.boot.data.jpa.model;

import java.io.Serializable;

@FunctionalInterface
public interface SFunction<T, R> extends Serializable {
   R apply(T t);
}
