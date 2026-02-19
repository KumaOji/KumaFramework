/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.redisson.Redisson
 *  org.redisson.api.RBlockingQueue
 *  org.redisson.api.RFuture
 *  org.redisson.client.RedisException
 *  org.redisson.client.codec.Codec
 *  org.redisson.client.protocol.RedisCommand
 *  org.redisson.client.protocol.decoder.ListObjectDecoder
 *  org.redisson.client.protocol.decoder.MultiDecoder
 *  org.redisson.command.CommandAsyncExecutor
 *  tools.jackson.databind.JsonNode
 */
package com.kuma.boot.cache.redis.delay.listener;

import cn.hutool.core.util.StrUtil;
import com.kuma.boot.cache.redis.delay.message.FastJsonCodec;
import com.kuma.boot.cache.redis.delay.message.RedissonMessage;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Map;
import java.util.Objects;
import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RFuture;
import org.redisson.client.RedisException;
import org.redisson.client.codec.Codec;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.decoder.ListObjectDecoder;
import org.redisson.client.protocol.decoder.MultiDecoder;
import org.redisson.command.CommandAsyncExecutor;
import tools.jackson.databind.JsonNode;

public class SimpleRedissonListenerContainer
extends AbstractRedissonListenerContainer {
    private RedisCommand<Object> LPOP_VALUE = new RedisCommand("LPOP", (MultiDecoder)new ListObjectDecoder(1));
    private AsyncMessageProcessingConsumer takeMessageTask;

    public SimpleRedissonListenerContainer(ContainerProperties containerProperties) {
        super(containerProperties);
    }

    @Override
    protected void doStart() {
        this.takeMessageTask = new AsyncMessageProcessingConsumer(this);
        this.getTaskExecutor().execute(this.takeMessageTask);
    }

    @Override
    protected void doStop() {
        this.takeMessageTask.stop();
    }

    private final class AsyncMessageProcessingConsumer
    implements Runnable {
        private volatile Thread currentThread;
        private volatile AbstractRedissonListenerContainer.ConsumerStatus status;
        final /* synthetic */ SimpleRedissonListenerContainer this$0;

        private AsyncMessageProcessingConsumer(SimpleRedissonListenerContainer simpleRedissonListenerContainer) {
            SimpleRedissonListenerContainer simpleRedissonListenerContainer2 = simpleRedissonListenerContainer;
            Objects.requireNonNull(simpleRedissonListenerContainer2);
            this.this$0 = simpleRedissonListenerContainer2;
            this.currentThread = null;
            this.status = AbstractRedissonListenerContainer.ConsumerStatus.CREATED;
        }

        @Override
        public void run() {
            if (this.status != AbstractRedissonListenerContainer.ConsumerStatus.CREATED) {
                LogUtils.info((String)"consumer currentThread [{}] will exit, because consumer status is {},expected is CREATED", (Object[])new Object[]{this.currentThread.getName(), this.status});
                return;
            }
            String queue = this.this$0.getContainerProperties().getQueue();
            Redisson redisson = (Redisson)this.this$0.getRedissonClient();
            RBlockingQueue blockingQueue = redisson.getBlockingQueue(queue, (Codec)FastJsonCodec.INSTANCE);
            if (blockingQueue == null) {
                LogUtils.error((String)"error occurred while create blockingQueue for queue [{}]", (Object[])new Object[]{queue});
                return;
            }
            CommandAsyncExecutor commandExecutor = redisson.getCommandExecutor();
            this.currentThread = Thread.currentThread();
            this.status = AbstractRedissonListenerContainer.ConsumerStatus.RUNNING;
            long maxWaitMillis = 100L;
            long emptyFetchTimes = 0L;
            do {
                try {
                    RFuture asyncResult = commandExecutor.writeAsync(blockingQueue.getName(), blockingQueue.getCodec(), this.this$0.LPOP_VALUE, new Object[]{blockingQueue.getName()});
                    String message = (String)commandExecutor.get(asyncResult);
                    if (StrUtil.isBlank((CharSequence)message)) {
                        Thread.sleep(Math.min(++emptyFetchTimes * 5L, 100L));
                        continue;
                    }
                    LogUtils.info((String)("message:" + message), (Object[])new Object[0]);
                    emptyFetchTimes = 0L;
                    JsonNode jsonNode = JacksonUtils.parse((String)message);
                    String payload = jsonNode.get("payload").toString();
                    Map headers = JacksonUtils.readMap((String)jsonNode.get("headers").toString());
                    RedissonMessage redissonMessage = new RedissonMessage(payload, headers);
                    SimpleRedissonMessageListenerAdapter redissonListener = (SimpleRedissonMessageListenerAdapter)this.this$0.getRedissonListener();
                    redissonListener.onMessage(redissonMessage);
                }
                catch (InterruptedException | RedisException asyncResult) {
                }
                catch (Exception e) {
                    LogUtils.error((String)"error occurred while take message from redisson", (Object[])new Object[]{e});
                }
            } while (this.status != AbstractRedissonListenerContainer.ConsumerStatus.STOPPED);
            LogUtils.info((String)"consumer currentThread [{}] will exit, because of STOPPED status", (Object[])new Object[]{this.currentThread.getName()});
            this.currentThread = null;
        }

        private void stop() {
            if (this.currentThread != null) {
                this.status = AbstractRedissonListenerContainer.ConsumerStatus.STOPPED;
                this.currentThread.interrupt();
            }
        }
    }
}

