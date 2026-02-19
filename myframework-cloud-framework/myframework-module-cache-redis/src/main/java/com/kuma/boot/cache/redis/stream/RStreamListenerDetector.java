/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.ReflectUtil
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.data.redis.RedisSystemException
 *  org.springframework.data.redis.connection.stream.Consumer
 *  org.springframework.data.redis.connection.stream.MapRecord
 *  org.springframework.data.redis.connection.stream.ObjectRecord
 *  org.springframework.data.redis.connection.stream.ReadOffset
 *  org.springframework.data.redis.connection.stream.StreamInfo$XInfoGroups
 *  org.springframework.data.redis.connection.stream.StreamOffset
 *  org.springframework.data.redis.core.RedisTemplate
 *  org.springframework.data.redis.core.StreamOperations
 *  org.springframework.data.redis.stream.StreamMessageListenerContainer
 *  org.springframework.data.redis.stream.StreamMessageListenerContainer$ConsumerStreamReadRequest
 *  org.springframework.data.redis.stream.StreamMessageListenerContainer$StreamReadRequest
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.ReflectionUtils$MethodFilter
 */
package com.kuma.boot.cache.redis.stream;

import cn.hutool.core.util.ReflectUtil;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class RStreamListenerDetector
implements BeanPostProcessor,
InitializingBean {
    private final StreamMessageListenerContainer<String, MapRecord<String, String, byte[]>> streamMessageListenerContainer;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String consumerGroup;
    private final String consumerName;

    public RStreamListenerDetector(StreamMessageListenerContainer<String, MapRecord<String, String, byte[]>> streamMessageListenerContainer, RedisTemplate<String, Object> redisTemplate, String consumerGroup, String consumerName) {
        this.streamMessageListenerContainer = streamMessageListenerContainer;
        this.redisTemplate = redisTemplate;
        this.consumerGroup = consumerGroup;
        this.consumerName = consumerName;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class userClass = ClassUtils.getUserClass((Object)bean);
        ReflectionUtils.doWithMethods((Class)userClass, method -> {
            RStreamListener listener = (RStreamListener)AnnotationUtils.findAnnotation((Method)method, RStreamListener.class);
            if (listener != null) {
                String streamKey = listener.name();
                Assert.hasText((String)streamKey, (String)"@RStreamListener name must not be empty.");
                LogUtils.info((String)"Found @RStreamListener on bean:{} method:{}", (Object[])new Object[]{beanName, method});
                int paramCount = method.getParameterCount();
                if (paramCount > 1) {
                    throw new IllegalArgumentException("@RStreamListener on method " + String.valueOf(method) + " parameter count must less or equal to 1.");
                }
                ReadOffset readOffset = listener.offsetModel().getReadOffset();
                StreamOffset streamOffset = StreamOffset.create((Object)streamKey, (ReadOffset)readOffset);
                MessageModel messageModel = listener.messageModel();
                if (MessageModel.BROADCASTING == messageModel) {
                    this.broadCast((StreamOffset<String>)streamOffset, bean, method, listener.readRawBytes());
                } else {
                    String groupId = StringUtils.isNotBlank((String)listener.group()) ? listener.group() : this.consumerGroup;
                    Consumer consumer = Consumer.from((String)groupId, (String)this.consumerName);
                    RStreamListenerDetector.createGroupIfNeed(this.redisTemplate, streamKey, readOffset, groupId);
                    this.cluster(consumer, (StreamOffset<String>)streamOffset, listener, bean, method);
                }
            }
        }, (ReflectionUtils.MethodFilter)ReflectionUtils.USER_DECLARED_METHODS);
        return bean;
    }

    private void broadCast(StreamOffset<String> streamOffset, Object bean, Method method, boolean isReadRawBytes) {
        this.streamMessageListenerContainer.receive(streamOffset, message -> this.invokeMethod(bean, method, (MapRecord<String, String, byte[]>)message, isReadRawBytes));
    }

    private void cluster(Consumer consumer, StreamOffset<String> streamOffset, RStreamListener listener, Object bean, Method method) {
        boolean autoAcknowledge = listener.autoAcknowledge();
        StreamMessageListenerContainer.ConsumerStreamReadRequest readRequest = StreamMessageListenerContainer.StreamReadRequest.builder(streamOffset).consumer(consumer).autoAcknowledge(autoAcknowledge).build();
        StreamOperations opsForStream = this.redisTemplate.opsForStream();
        this.streamMessageListenerContainer.register((StreamMessageListenerContainer.StreamReadRequest)readRequest, message -> {
            this.invokeMethod(bean, method, (MapRecord<String, String, byte[]>)message, listener.readRawBytes());
            if (!autoAcknowledge) {
                opsForStream.acknowledge(consumer.getGroup(), message);
            }
        });
    }

    private static void createGroupIfNeed(RedisTemplate<String, Object> redisTemplate, String streamKey, ReadOffset readOffset, String group) {
        StreamOperations opsForStream = redisTemplate.opsForStream();
        try {
            StreamInfo.XInfoGroups groups = opsForStream.groups((Object)streamKey);
            if (groups.stream().noneMatch(x -> group.equals(x.groupName()))) {
                opsForStream.createGroup((Object)streamKey, readOffset, group);
            }
        }
        catch (RedisSystemException e) {
            opsForStream.createGroup((Object)streamKey, group);
        }
    }

    private void invokeMethod(Object bean, Method method, MapRecord<String, String, byte[]> mapRecord, boolean isReadRawBytes) {
        if (method.getParameterCount() == 0) {
            ReflectUtil.invoke((Object)bean, (Method)method, (Object[])new Object[0]);
            return;
        }
        if (isReadRawBytes) {
            ReflectUtil.invoke((Object)bean, (Method)method, (Object[])new Object[]{mapRecord});
        } else {
            ReflectUtil.invoke((Object)bean, (Method)method, (Object[])new Object[]{this.getRecordValue(mapRecord)});
        }
    }

    private Object getRecordValue(MapRecord<String, String, byte[]> mapRecord) {
        Map messageValue = (Map)mapRecord.getValue();
        if (messageValue.containsKey("@payload")) {
            byte[] payloads = (byte[])messageValue.get("@payload");
            Object deserialize = this.redisTemplate.getValueSerializer().deserialize(payloads);
            return ObjectRecord.create((Object)((String)mapRecord.getStream()), (Object)deserialize);
        }
        return mapRecord.mapEntries(entry -> {
            String key = (String)entry.getKey();
            Object value = this.redisTemplate.getValueSerializer().deserialize((byte[])entry.getValue());
            return Collections.singletonMap(key, value).entrySet().iterator().next();
        });
    }

    public void afterPropertiesSet() throws Exception {
        this.streamMessageListenerContainer.start();
    }
}

