/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.support.thread.ThreadFactoryCreator
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.redisson.api.RScript$Mode
 *  org.redisson.api.RScript$ReturnType
 *  org.redisson.api.RedissonClient
 *  org.redisson.client.RedisException
 *  org.redisson.client.codec.Codec
 *  org.springframework.core.task.SimpleAsyncTaskExecutor
 *  org.springframework.util.Assert
 */
package com.kuma.boot.cache.redis.delay.listener;

import com.kuma.boot.cache.redis.delay.message.FastJsonCodec;
import com.kuma.boot.cache.redis.delay.message.RedissonMessage;
import com.kuma.boot.common.support.thread.ThreadFactoryCreator;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.redisson.client.codec.Codec;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.Assert;

public class BatchRedissonListenerContainer
extends AbstractRedissonListenerContainer {
    private final String fetchScript;
    private final int maxFetch;
    private AsyncMessageProcessingConsumer takeMessageTask;

    public int getMaxFetch() {
        return this.maxFetch;
    }

    public BatchRedissonListenerContainer(ContainerProperties containerProperties, int maxFetch) {
        super(containerProperties);
        Assert.isTrue((maxFetch > 0 ? 1 : 0) != 0, (String)"maxFetch must be greater than 0");
        this.maxFetch = maxFetch;
        this.fetchScript = "local expiredValues = redis.call('lrange', KEYS[1], 0, ARGV[1]); if #expiredValues > 0 then redis.call('ltrim', KEYS[1], ARGV[2], -1); end; return expiredValues;";
        this.setTaskExecutor((Executor)new SimpleAsyncTaskExecutor(ThreadFactoryCreator.create((String)"kmc-redisson-batch-consume-thread")));
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
        final /* synthetic */ BatchRedissonListenerContainer this$0;

        private AsyncMessageProcessingConsumer(BatchRedissonListenerContainer batchRedissonListenerContainer) {
            BatchRedissonListenerContainer batchRedissonListenerContainer2 = batchRedissonListenerContainer;
            Objects.requireNonNull(batchRedissonListenerContainer2);
            this.this$0 = batchRedissonListenerContainer2;
            this.currentThread = null;
            this.status = AbstractRedissonListenerContainer.ConsumerStatus.CREATED;
        }

        @Override
        public void run() {
            if (this.status != AbstractRedissonListenerContainer.ConsumerStatus.CREATED) {
                LogUtils.info((String)"consumer currentThread [{}] will exit, because consumer status is {},expected is CREATED", (Object[])new Object[]{this.currentThread.getName(), this.status});
                return;
            }
            this.currentThread = Thread.currentThread();
            this.status = AbstractRedissonListenerContainer.ConsumerStatus.RUNNING;
            long maxWaitMillis = 100L;
            long emptyFetchTimes = 0L;
            do {
                try {
                    List<RedissonMessage> messageList = this.fetch();
                    if (messageList == null || messageList.isEmpty()) {
                        long delay = ++emptyFetchTimes * 5L;
                        delay = Math.min(delay, 100L);
                        Thread.sleep(delay);
                        continue;
                    }
                    emptyFetchTimes = 0L;
                    BatchRedissonMessageListenerAdapter redissonListener = (BatchRedissonMessageListenerAdapter)this.this$0.getRedissonListener();
                    redissonListener.onMessage(messageList);
                }
                catch (InterruptedException | RedisException messageList) {
                }
                catch (Exception e) {
                    LogUtils.error((String)"error occurred while take message from redisson", (Object[])new Object[]{e});
                }
            } while (this.status != AbstractRedissonListenerContainer.ConsumerStatus.STOPPED);
            LogUtils.info((String)"consumer currentThread [{}] will exit, because of STOPPED status", (Object[])new Object[]{this.currentThread.getName()});
            this.currentThread = null;
        }

        private List<RedissonMessage> fetch() {
            String queue = this.this$0.getContainerProperties().getQueue();
            RedissonClient redissonClient = this.this$0.getRedissonClient();
            int fetchCount = this.this$0.maxFetch;
            int searchEndIndex = fetchCount - 1;
            List message = (List)redissonClient.getScript((Codec)FastJsonCodec.INSTANCE).eval(RScript.Mode.READ_WRITE, this.this$0.fetchScript, RScript.ReturnType.LIST, Collections.singletonList(queue), new Object[]{searchEndIndex, fetchCount});
            if (message == null || message.isEmpty()) {
                return null;
            }
            return message.stream().map(e -> (RedissonMessage)JacksonUtils.toObject((String)e, RedissonMessage.class)).toList();
        }

        private void stop() {
            if (this.currentThread != null) {
                this.status = AbstractRedissonListenerContainer.ConsumerStatus.STOPPED;
                this.currentThread.interrupt();
            }
        }
    }
}

