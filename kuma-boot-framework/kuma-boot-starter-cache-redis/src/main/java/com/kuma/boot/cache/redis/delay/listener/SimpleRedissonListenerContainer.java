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

import tools.jackson.databind.JsonNode;
import com.kuma.boot.cache.redis.delay.message.FastJsonCodec;
import com.kuma.boot.cache.redis.delay.message.RedissonMessage;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import cn.hutool.core.util.StrUtil;
import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RFuture;
import org.redisson.client.RedisException;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.decoder.ListObjectDecoder;
import org.redisson.command.CommandAsyncExecutor;

import java.util.Map;

/**
 * SimpleRedissonListenerContainer
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:36:42
 */
public class SimpleRedissonListenerContainer extends AbstractRedissonListenerContainer {

    private RedisCommand<Object> LPOP_VALUE = new RedisCommand<>("LPOP", new ListObjectDecoder<>(1));

    public SimpleRedissonListenerContainer( ContainerProperties containerProperties ) {
        super(containerProperties);
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
            final String queue = SimpleRedissonListenerContainer.this
                    .getContainerProperties()
                    .getQueue();
            final Redisson redisson = (Redisson) SimpleRedissonListenerContainer.this.getRedissonClient();
            final RBlockingQueue<Object> blockingQueue = redisson.getBlockingQueue(queue, FastJsonCodec.INSTANCE);
            if (blockingQueue == null) {
                LogUtils.error("error occurred while create blockingQueue for queue [{}]", queue);
                return;
            }
            CommandAsyncExecutor commandExecutor = redisson.getCommandExecutor();
            this.currentThread = Thread.currentThread();
            this.status = ConsumerStatus.RUNNING;
            final long maxWaitMillis = 100;
            long emptyFetchTimes = 0;
            for (; ; ) {
                try {
                    RFuture<String> asyncResult = commandExecutor.writeAsync(
                            blockingQueue.getName(), blockingQueue.getCodec(), LPOP_VALUE, blockingQueue.getName());
                    String message = commandExecutor.get(asyncResult);

                    if (StrUtil.isBlank(message)) {
                        Thread.sleep(Math.min(++emptyFetchTimes * 5, maxWaitMillis));
                    } else {
                        // reset counting
                        LogUtils.info("message:" + message);
                        emptyFetchTimes = 0;

                        JsonNode jsonNode = JacksonUtils.parse(message);
                        String payload = jsonNode.get("payload").toString();
                        Map<String, Object> headers =
                                JacksonUtils.readMap(jsonNode.get("headers").toString());

                        // RedissonMessage redissonMessage = JsonUtil.(message,
                        // RedissonMessage.class);

                        RedissonMessage redissonMessage = new RedissonMessage(payload, headers);
                        SimpleRedissonMessageListenerAdapter redissonListener = (SimpleRedissonMessageListenerAdapter)
                                SimpleRedissonListenerContainer.this.getRedissonListener();
                        redissonListener.onMessage(redissonMessage);
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

        private void stop() {
            if (this.currentThread != null) {
                this.status = ConsumerStatus.STOPPED;
                this.currentThread.interrupt();
            }
        }
    }
}
