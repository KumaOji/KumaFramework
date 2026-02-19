/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.collection.ListUtil
 *  org.apache.kafka.clients.producer.KafkaProducer
 *  org.apache.kafka.common.serialization.Serializer
 */
package com.kuma.boot.mq.kafka.kafkaextend.kafka;

import cn.hutool.core.collection.ListUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.Serializer;

public class KafkaProducerBuilder {
    private final Properties properties = new Properties();
    private final Set<String> bootstrapServers = new HashSet<String>();

    public KafkaProducerBuilder keySerializer(Class<? extends Serializer<?>> c) {
        return this.keySerializer(c.getName());
    }

    public KafkaProducerBuilder keySerializer(String className) {
        return this.put("key.serializer", className);
    }

    public KafkaProducerBuilder valueSerializer(Class<? extends Serializer<?>> c) {
        return this.valueSerializer(c.getName());
    }

    public KafkaProducerBuilder valueSerializer(String className) {
        return this.put("value.serializer", className);
    }

    public KafkaProducerBuilder addBootstrapServers(String uri) {
        this.bootstrapServers.add(uri);
        return this;
    }

    public KafkaProducerBuilder addAllBootstrapServers(Collection<String> uris) {
        this.bootstrapServers.addAll(uris);
        return this;
    }

    public KafkaProducerBuilder put(Object key, Object val) {
        this.properties.put(key, val);
        return this;
    }

    public KafkaProducerBuilder putAll(Properties properties) {
        this.properties.putAll((Map<?, ?>)properties);
        return this;
    }

    public KafkaProducerBuilder putAll(Map<?, ?> map) {
        this.properties.putAll(map);
        return this;
    }

    public <K, V> KafkaExtendProducer<K, V> build(Function<Properties, KafkaExtendProducer<K, V>> function) {
        return function.apply(this.getProperties());
    }

    public <K, V> KafkaExtendProducer<K, V> build(Properties properties) {
        return this.putAll(properties).build();
    }

    public <K, V> KafkaExtendProducer<K, V> build() {
        return this.build((Properties p) -> new KafkaExtendProducer(new KafkaProducer(p)));
    }

    public Set<String> getBootstrapServers() {
        this.getProperties();
        return this.bootstrapServers;
    }

    public Properties getProperties() {
        this.bootstrapServers.addAll(ListUtil.of((Object[])this.properties.getProperty("bootstrap.servers", "").split(",")));
        this.properties.put("bootstrap.servers", String.join((CharSequence)",", this.bootstrapServers));
        return this.properties;
    }
}

