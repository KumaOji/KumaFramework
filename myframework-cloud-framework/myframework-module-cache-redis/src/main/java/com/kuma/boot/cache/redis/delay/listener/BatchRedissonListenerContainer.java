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

package com.kuma.boot.cache.redis.delay.listener;

import com.kuma.boot.cache.redis.delay.message.FastJsonCodec;
import com.kuma.boot.cache.redis.delay.message.RedissonMessage;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.support.thread.ThreadFactoryCreator;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Collections;
import java.util.List;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.Assert;

/**
 * BatchRedissonListenerContainer
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:26:12
 */
public class BatchRedissonListenerContainer extends AbstractRedissonListenerContainer {

    private final String fetchScript;

    private final int maxFetch;

    public int getMaxFetch() {
        return maxFetch;
    }

    public BatchRedissonListenerContainer( ContainerProperties containerProperties, int maxFetch ) {
        super(containerProperties);
        Assert.isTrue(maxFetch > 0, "maxFetch must be greater than 0");
        this.maxFetch = maxFetch;
        this.fetchScript = "local expiredValues = redis.call('lrange', KEYS[1], 0, ARGV[1]); if #expiredValues"
                + " > 0 then redis.call('ltrim', KEYS[1], ARGV[2], -1); end; return"
                + " expiredValues;";
        this.setTaskExecutor(
                new SimpleAsyncTaskExecutor(ThreadFactoryCreator.create("kmc-redisson-batch-consume-thread")));
    }

    private AsyncMessageProcessingConsumer takeMessageTask;

    @Override
    protected void doStart() {
        this.takeMessageTask = new AsyncMessageProcessingConsumer();
        this.getTaskExecutor().execute(this.takeMessageTask);
    }

    @Override
    protected void doStop() {
        this.takeMessageTask.stop();
    }

    /**
     * AsyncMessageProcessingConsumer
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    private final class AsyncMessageProcessingConsumer implements Runnable {

        private volatile Thread currentThread = null;

        private volatile ConsumerStatus status = ConsumerStatus.CREATED;

        @Override
        public void run() {
            if (this.status != ConsumerStatus.CREATED) {
                LogUtils.info(
                        "consumer currentThread [{}] will exit, because consumer status is" + " {},expected is CREATED",
                        this.currentThread.getName(),
                        this.status);
                return;
            }
            this.currentThread = Thread.currentThread();
            this.status = ConsumerStatus.RUNNING;
            final long maxWaitMillis = 100;
            long emptyFetchTimes = 0;
            for (; ; ) {
                try {
                    List<RedissonMessage> messageList = this.fetch();
                    if (messageList == null || messageList.isEmpty()) {
                        long delay = ++emptyFetchTimes * 5;
                        delay = Math.min(delay, maxWaitMillis);
                        Thread.sleep(delay);
                    } else {
                        // reset counting
                        emptyFetchTimes = 0;
                        BatchRedissonMessageListenerAdapter redissonListener = (BatchRedissonMessageListenerAdapter)
                                BatchRedissonListenerContainer.this.getRedissonListener();
                        redissonListener.onMessage(messageList);
                    }
                } catch (InterruptedException | RedisException e) {
                    // ignore
                } catch (Exception e) {
                    LogUtils.error("error occurred while take message from redisson", e);
                }
                if (this.status == ConsumerStatus.STOPPED) {
                    LogUtils.info(
                            "consumer currentThread [{}] will exit, because of STOPPED status",
                            this.currentThread.getName());
                    break;
                }
            }
            this.currentThread = null;
        }

        @SuppressWarnings("unchecked")
        private List<RedissonMessage> fetch() {
            final String queue =
                    BatchRedissonListenerContainer.this.getContainerProperties().getQueue();
            final RedissonClient redissonClient = BatchRedissonListenerContainer.this.getRedissonClient();
            final int fetchCount = BatchRedissonListenerContainer.this.maxFetch;
            final int searchEndIndex = fetchCount - 1;
            List<String> message = (List<String>) redissonClient
                    .getScript(FastJsonCodec.INSTANCE)
                    .eval(
                            RScript.Mode.READ_WRITE,
                            BatchRedissonListenerContainer.this.fetchScript,
                            RScript.ReturnType.LIST,
                            Collections.singletonList(queue),
                            searchEndIndex,
                            fetchCount);
            if (message == null || message.isEmpty()) {
                return null;
            }
            return message.stream()
                    .map(e -> JacksonUtils.toObject(e, RedissonMessage.class))
                    .toList();
        }

        private void stop() {
            if (this.currentThread != null) {
                this.status = ConsumerStatus.STOPPED;
                this.currentThread.interrupt();
            }
        }
    }
}
