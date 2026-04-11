package com.kuma.boot.data.mongodb.helper.reflection;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface SerializableFunction<E, R> extends Function<E, R>, Serializable {
}
