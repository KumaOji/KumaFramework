/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.kafka.clients.producer.KafkaProducer
 *  org.apache.kafka.clients.producer.ProducerRecord
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.kuma.boot.mq.kafka.kafkafactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

public class KafkaProducerFactoryBean<T>
implements FactoryBean<T> {
    private Class<T> mapperInterface;
    @Autowired
    private KafkaProducer kafkaProducer;

    public KafkaProducerFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T getObject() throws Exception {
        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if ("send".equals(name)) {
                ProducerRecord record = new ProducerRecord(args[0].toString(), (Object)args[1].toString());
                return this.kafkaProducer.send(record).get();
            }
            return "\u4e0d\u652f\u6301\u7684\u65b9\u6cd5\u540d\u79f0";
        };
        return (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{this.mapperInterface}, handler);
    }

    public Class<?> getObjectType() {
        return this.mapperInterface;
    }
}

