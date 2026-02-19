/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.text.CharSequenceUtil
 *  org.apache.kafka.common.serialization.Deserializer
 *  org.apache.kafka.common.serialization.Serializer
 *  org.apache.kafka.common.serialization.StringDeserializer
 *  org.apache.kafka.common.serialization.StringSerializer
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.kuma.boot.mq.kafka.kafkaextend.kafka;

import cn.hutool.core.text.CharSequenceUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="kuma.boot.mq.kafka.extend")
public class KafkaProperties {
    private String groupId;
    private Set<String> bootstrapServers;
    private Class<? extends Serializer<?>> keySerializer = StringSerializer.class;
    private String keySerializerClassName;
    private Class<? extends Serializer<?>> valueSerializer = StringSerializer.class;
    private String valueSerializerClassName;
    private Class<? extends Deserializer<?>> keyDeserializer = StringDeserializer.class;
    private String keyDeserializerClassName;
    private Class<? extends Deserializer<?>> valueDeserializer = StringDeserializer.class;
    private String valueDeserializerClassName;
    private Map<String, Object> extend = new HashMap<String, Object>();

    public String getKeyDeserializerClassName() {
        if (CharSequenceUtil.isNotEmpty((CharSequence)this.keyDeserializerClassName)) {
            return this.keyDeserializerClassName;
        }
        return this.getKeyDeserializer().getName();
    }

    public String getValueDeserializerClassName() {
        if (CharSequenceUtil.isNotEmpty((CharSequence)this.valueDeserializerClassName)) {
            return this.valueDeserializerClassName;
        }
        return this.getValueDeserializer().getName();
    }

    public String getKeySerializerClassName() {
        if (CharSequenceUtil.isNotEmpty((CharSequence)this.keySerializerClassName)) {
            return this.keySerializerClassName;
        }
        return this.getKeySerializer().getName();
    }

    public String getValueSerializerClassName() {
        if (CharSequenceUtil.isNotEmpty((CharSequence)this.valueSerializerClassName)) {
            return this.valueSerializerClassName;
        }
        return this.getValueSerializer().getName();
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Set<String> getBootstrapServers() {
        return this.bootstrapServers;
    }

    public void setBootstrapServers(Set<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public Class<? extends Serializer<?>> getKeySerializer() {
        return this.keySerializer;
    }

    public void setKeySerializer(Class<? extends Serializer<?>> keySerializer) {
        this.keySerializer = keySerializer;
    }

    public void setKeySerializerClassName(String keySerializerClassName) {
        this.keySerializerClassName = keySerializerClassName;
    }

    public Class<? extends Serializer<?>> getValueSerializer() {
        return this.valueSerializer;
    }

    public void setValueSerializer(Class<? extends Serializer<?>> valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public void setValueSerializerClassName(String valueSerializerClassName) {
        this.valueSerializerClassName = valueSerializerClassName;
    }

    public Class<? extends Deserializer<?>> getKeyDeserializer() {
        return this.keyDeserializer;
    }

    public void setKeyDeserializer(Class<? extends Deserializer<?>> keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
    }

    public void setKeyDeserializerClassName(String keyDeserializerClassName) {
        this.keyDeserializerClassName = keyDeserializerClassName;
    }

    public Class<? extends Deserializer<?>> getValueDeserializer() {
        return this.valueDeserializer;
    }

    public void setValueDeserializer(Class<? extends Deserializer<?>> valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
    }

    public void setValueDeserializerClassName(String valueDeserializerClassName) {
        this.valueDeserializerClassName = valueDeserializerClassName;
    }

    public Map<String, Object> getExtend() {
        return this.extend;
    }

    public void setExtend(Map<String, Object> extend) {
        this.extend = extend;
    }
}

