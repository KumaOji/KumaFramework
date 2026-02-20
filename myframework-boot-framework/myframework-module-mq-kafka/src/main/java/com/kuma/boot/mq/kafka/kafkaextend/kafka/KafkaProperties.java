/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.mq.kafka.kafkaextend.kafka;

import cn.hutool.core.text.CharSequenceUtil;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ConfigurationProperties(prefix = "kuma.boot.mq.kafka.extend")
public class KafkaProperties {

    /**
     * 用于指定分组
     */
    private String groupId;

    /**
     * 所属服务
     */
    private Set<String> bootstrapServers;

    /**
     * key 序列化
     */
    private Class<? extends Serializer<?>> keySerializer = StringSerializer.class;

    /**
     * key 序列化 类名, 如果填写本值则 valueSerializer 无效
     */
    private String keySerializerClassName;

    /**
     * value 序列化
     */
    private Class<? extends Serializer<?>> valueSerializer = StringSerializer.class;

    /**
     * value 序列化 类名, 如果填写本值则 valueSerializer 无效
     */
    private String valueSerializerClassName;

    /**
     * key 反序列化
     */
    private Class<? extends Deserializer<?>> keyDeserializer = StringDeserializer.class;

    /**
     * key 反序列化 类名, 如果填写本值则 keyDeserializer 无效
     */
    private String keyDeserializerClassName;

    /**
     * value 反序列化
     */
    private Class<? extends Deserializer<?>> valueDeserializer = StringDeserializer.class;

    /**
     * value 反序列化 类名, 如果填写本值则 valueDeserializer 无效
     */
    private String valueDeserializerClassName;

    /**
     * 额外参数
     */
    private Map<String, Object> extend = new HashMap<>();

    public String getKeyDeserializerClassName() {
        if (CharSequenceUtil.isNotEmpty(keyDeserializerClassName)) {
            return keyDeserializerClassName;
        }
        return getKeyDeserializer().getName();
    }

    public String getValueDeserializerClassName() {
        if (CharSequenceUtil.isNotEmpty(valueDeserializerClassName)) {
            return valueDeserializerClassName;
        }
        return getValueDeserializer().getName();
    }

    public String getKeySerializerClassName() {
        if (CharSequenceUtil.isNotEmpty(keySerializerClassName)) {
            return keySerializerClassName;
        }
        return getKeySerializer().getName();
    }

    public String getValueSerializerClassName() {
        if (CharSequenceUtil.isNotEmpty(valueSerializerClassName)) {
            return valueSerializerClassName;
        }
        return getValueSerializer().getName();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Set<String> getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(Set<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public Class<? extends Serializer<?>> getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(Class<? extends Serializer<?>> keySerializer) {
        this.keySerializer = keySerializer;
    }

    public void setKeySerializerClassName(String keySerializerClassName) {
        this.keySerializerClassName = keySerializerClassName;
    }

    public Class<? extends Serializer<?>> getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(Class<? extends Serializer<?>> valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public void setValueSerializerClassName(String valueSerializerClassName) {
        this.valueSerializerClassName = valueSerializerClassName;
    }

    public Class<? extends Deserializer<?>> getKeyDeserializer() {
        return keyDeserializer;
    }

    public void setKeyDeserializer(Class<? extends Deserializer<?>> keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
    }

    public void setKeyDeserializerClassName(String keyDeserializerClassName) {
        this.keyDeserializerClassName = keyDeserializerClassName;
    }

    public Class<? extends Deserializer<?>> getValueDeserializer() {
        return valueDeserializer;
    }

    public void setValueDeserializer(Class<? extends Deserializer<?>> valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
    }

    public void setValueDeserializerClassName(String valueDeserializerClassName) {
        this.valueDeserializerClassName = valueDeserializerClassName;
    }

    public Map<String, Object> getExtend() {
        return extend;
    }

    public void setExtend(Map<String, Object> extend) {
        this.extend = extend;
    }
}
