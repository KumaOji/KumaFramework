package com.kuma.boot.mq.kafka.kafkafactory;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class KafkaProducerFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;

    @Autowired
    private KafkaProducer kafkaProducer;


    public KafkaProducerFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public T getObject() throws Exception {
        InvocationHandler handler = ((proxy, method, args) -> {
            String name = method.getName();
            if ("send".equals(name)) {
                ProducerRecord<String, String> record = new ProducerRecord<>(args[0].toString(), args[1].toString());
                return kafkaProducer.send(record).get();
            }
            return "不支持的方法名称";
        });
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{mapperInterface}, handler);
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }
}
