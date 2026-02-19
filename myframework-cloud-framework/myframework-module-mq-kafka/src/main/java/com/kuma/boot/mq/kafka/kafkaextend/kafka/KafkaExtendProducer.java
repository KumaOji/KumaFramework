/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.kafka.clients.producer.Callback
 *  org.apache.kafka.clients.producer.KafkaProducer
 *  org.apache.kafka.clients.producer.ProducerRecord
 *  org.apache.kafka.clients.producer.RecordMetadata
 *  org.apache.kafka.common.header.Header
 */
package com.kuma.boot.mq.kafka.kafkaextend.kafka;

import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;

public class KafkaExtendProducer<K, V> {
    private final KafkaProducer<K, V> producer;

    public KafkaProducer<K, V> getProducer() {
        return this.producer;
    }

    public KafkaExtendProducer(KafkaProducer<K, V> producer) {
        this.producer = producer;
    }

    public ProducerRecord<K, V> record(String topic, Integer partition, Long timestamp, K key, V value, Iterable<Header> headers) {
        return new ProducerRecord(topic, partition, timestamp, key, value, headers);
    }

    public ProducerRecord<K, V> record(String topic, Integer partition, Long timestamp, K key, V value) {
        return this.record(topic, partition, timestamp, key, value, null);
    }

    public ProducerRecord<K, V> record(String topic, Integer partition, K key, V value, Iterable<Header> headers) {
        return this.record(topic, partition, null, key, value, headers);
    }

    public ProducerRecord<K, V> record(String topic, Integer partition, K key, V value) {
        return this.record(topic, partition, null, key, value, null);
    }

    public ProducerRecord<K, V> record(String topic, K key, V value) {
        return this.record(topic, null, null, key, value, null);
    }

    public ProducerRecord<K, V> record(String topic, V value) {
        return this.record(topic, null, null, null, value, null);
    }

    public Future<RecordMetadata> send(ProducerRecord<K, V> record, Callback callback) {
        return this.producer.send(record, callback);
    }

    public Future<RecordMetadata> send(String topic, Integer partition, Long timestamp, K key, V value, Iterable<Header> headers) {
        return this.send(this.record(topic, partition, timestamp, key, value, headers), null);
    }

    public Future<RecordMetadata> send(String topic, Integer partition, Long timestamp, K key, V value) {
        return this.send(topic, partition, timestamp, key, value, null);
    }

    public Future<RecordMetadata> send(String topic, Integer partition, K key, V value, Iterable<Header> headers) {
        return this.send(topic, partition, null, key, value, headers);
    }

    public Future<RecordMetadata> send(String topic, Integer partition, K key, V value) {
        return this.send(topic, partition, null, key, value, null);
    }

    public Future<RecordMetadata> send(String topic, K key, V value) {
        return this.send(topic, null, null, key, value, null);
    }

    public Future<RecordMetadata> send(String topic, V value) {
        return this.send(topic, null, null, null, value, null);
    }

    public Future<RecordMetadata> send(String topic, V value, Callback callback) {
        return this.send(this.record(topic, value), callback);
    }
}

