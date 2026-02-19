/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.collection.ListUtil
 *  cn.hutool.core.text.CharSequenceUtil
 *  org.apache.kafka.clients.consumer.KafkaConsumer
 *  org.apache.kafka.common.serialization.Deserializer
 */
package com.kuma.boot.mq.kafka.kafkaextend.kafka;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;

public class KafkaConsumerBuilder {
    private final Properties properties = new Properties();
    private final Set<String> bootstrapServers = new HashSet<String>();
    private final Set<String> topics = new HashSet<String>();

    public Set<String> getTopics() {
        return this.topics;
    }

    public KafkaConsumerBuilder keyDeserializer(Class<? extends Deserializer<?>> c) {
        return this.keyDeserializer(c.getName());
    }

    public KafkaConsumerBuilder keyDeserializer(String className) {
        return this.put("key.deserializer", className);
    }

    public KafkaConsumerBuilder valueDeserializer(Class<? extends Deserializer<?>> c) {
        return this.valueDeserializer(c.getName());
    }

    public KafkaConsumerBuilder valueDeserializer(String className) {
        return this.put("value.deserializer", className);
    }

    public KafkaConsumerBuilder addBootstrapServers(String uri) {
        this.bootstrapServers.add(uri);
        return this;
    }

    public KafkaConsumerBuilder addAllBootstrapServers(Collection<String> uris) {
        this.bootstrapServers.addAll(uris);
        return this;
    }

    public KafkaConsumerBuilder put(Object key, Object val) {
        this.properties.put(key, val);
        return this;
    }

    public KafkaConsumerBuilder putAll(Properties properties) {
        this.properties.putAll((Map<?, ?>)properties);
        return this;
    }

    public KafkaConsumerBuilder groupId(String groupId) {
        return this.put("group.id", groupId);
    }

    public KafkaConsumerBuilder addTopic(String topic) {
        this.topics.add(topic);
        return this;
    }

    public KafkaConsumerBuilder addAllTopic(Collection<String> topics) {
        this.topics.addAll(topics);
        return this;
    }

    public <K, V> KafkaConsumer<K, V> build(Function<Properties, KafkaConsumer<K, V>> function) {
        KafkaConsumer<K, V> consumer = function.apply(this.getProperties());
        consumer.subscribe(this.topics);
        return consumer;
    }

    public <K, V> KafkaConsumer<K, V> build(Properties properties) {
        return this.putAll(properties).build();
    }

    public <K, V> KafkaConsumer<K, V> build() {
        return this.build(KafkaConsumer::new);
    }

    public Set<String> getBootstrapServers() {
        this.getProperties();
        return this.bootstrapServers;
    }

    public Properties getProperties() {
        String nowServes = this.properties.getProperty("bootstrap.servers", "");
        if (CharSequenceUtil.isNotBlank((CharSequence)nowServes)) {
            this.bootstrapServers.addAll(ListUtil.of((Object[])nowServes.split(",")));
        }
        this.properties.put("bootstrap.servers", String.join((CharSequence)",", this.bootstrapServers));
        return this.properties;
    }
}

