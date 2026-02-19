/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.kuma.boot.mq.kafka.kafkafactory;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="mq.kafka.producer")
public class KafkaProducerProperties {
    private String bootstrapServers;
    private String acks;
    private String clientId;

    public String getBootstrapServers() {
        return this.bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getAcks() {
        return this.acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}

