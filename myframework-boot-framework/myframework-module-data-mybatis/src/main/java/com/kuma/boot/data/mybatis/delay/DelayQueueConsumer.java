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

package com.kuma.boot.data.mybatis.delay;

import com.alibaba.fastjson2.JSON;
import com.kuma.boot.common.utils.log.LogUtils;
import java.time.LocalDateTime;
import java.util.concurrent.DelayQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 延迟消息消费者
 */
@Component
public class DelayQueueConsumer implements Runnable {

    /**
     * 延迟队列
     */
    private DelayQueue<DelayMessage> delayQueue;

    /**
     * 设置延迟队列
     *
     * @param delayQueue 延迟队列
     */
    public void setDelayQueue(DelayQueue<DelayMessage> delayQueue) {
        this.delayQueue = delayQueue;
    }

    /**
     * 超时消息处理服务
     */
    @Autowired private AppDelayMessageService service;

    @Override
    public void run() {
        while (true) {
            try {
                LogUtils.info("@@ 启动异步线程 [{}] 消费以超时的消息", Thread.currentThread().getName());
                // 如果暂时没有过期消息或者队列为空, 则 take 方法会被阻塞, 直到有过期的消息为止
                DelayMessage delayMessage = delayQueue.take();
                AppDelayMessage message =
                        JSON.parseObject(delayMessage.getMessage(), AppDelayMessage.class);
                // 处理 TIMEOUT 异常
                handleTimeoutError(message);
                LogUtils.info("@@ 以消费消息:{}", delayMessage.getMessage());
            } catch (InterruptedException e) {
                LogUtils.error("@@ 线程 [{}] 消费消息异常", Thread.currentThread().getName(), e);
            }
        }
    }

    /**
     * 超时错误处理
     *
     * @param message 消息内容
     */
    @Transactional
    public void handleTimeoutError(AppDelayMessage message) {
        LogUtils.info("@@ 处理超时错误, AppDelayMessage:{}", message);

        // 更新消息状态 [PENDING -> TIMEOUT]
        boolean update =
                service.lambdaUpdate()
                        .set(AppDelayMessage::getStatus, AppDelayMessage.Status.TIMEOUT)
                        .set(AppDelayMessage::getModifyTime, LocalDateTime.now())
                        .eq(AppDelayMessage::getId, message.getId())
                        .eq(AppDelayMessage::getStatus, AppDelayMessage.Status.PENDING)
                        .update();

        if (update) {
            LogUtils.info("@@ 处理超时调用回调函数, message:{}", JSON.toJSONString(message));
            service.callback(message);
        }
    }
}
