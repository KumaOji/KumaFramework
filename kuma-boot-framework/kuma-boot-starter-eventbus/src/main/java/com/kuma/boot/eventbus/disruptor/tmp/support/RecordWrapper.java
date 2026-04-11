package com.kuma.boot.eventbus.disruptor.tmp.support;

record RecordWrapper<V>(V unwrap) implements GenericWrapper<V> {
}
