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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kuma.boot.common.utils.log.LogUtils;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 创建超时消息处理服务实现类
 */
@Service
public class AppDelayMessageServiceImpl extends ServiceImpl<AppDelayMessageMapper, AppDelayMessage>
        implements AppDelayMessageService, ApplicationEventPublisherAware {

    /**
     * ApplicationEventPublisher
     */
    private ApplicationEventPublisher eventPublisher;

    /**
     * 注入事件发布器
     *
     * @param eventPublisher event publisher to be used by this object
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 发布延迟消息
     *
     * @param appId    应用ID
     * @param timeout  超时时长(H)
     * @param type     FOTA; SOTA;
     * @param stage    编译工程:COMPILE; 测试配置:TEST;
     * @param callback 回调函数
     */
    @Async("toolThreadPool")
    @Override
    public void publish(
            String appId,
            Integer timeout,
            AppDelayMessage.Type type,
            AppDelayMessage.Stage stage,
            Class callback) {
        LogUtils.info("@@ 发布延迟消息, appId:{}, type:{}, stage:{}", appId, type, stage);
        AppDelayMessage message =
                AppDelayMessage.builder()
                        .appId(appId)
                        .ttl(timeout)
                        .type(type)
                        .stage(stage)
                        .callback(callback.getSimpleName())
                        .build();
        // 发布延时消息时间事件
        com.kuma.boot.data.mybatis.delay.InvokeTimeoutEvent event = new com.kuma.boot.data.mybatis.delay.InvokeTimeoutEvent(message);
        eventPublisher.publishEvent(event);
    }

    /**
     * 超时回调
     *
     * @param message 消息体
     */
    @Async("toolThreadPool")
    @Override
    public void callback(AppDelayMessage message) {
        LogUtils.info("@@ 超时回调函数处理, message:{}", JSON.toJSONString(message));
        // 发布延时消息时间事件
        com.kuma.boot.data.mybatis.delay.InvokeTimeoutEvent event = new com.kuma.boot.data.mybatis.delay.InvokeTimeoutEvent(message);
        eventPublisher.publishEvent(event);
    }

    /**
     * 修改延迟消息状态
     *
     * @param appId 应用ID
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public boolean changeToProcessed(String appId, AppDelayMessage.Stage stage) {
        LogUtils.info("@@ 修改延迟消息状态, appId:{}, Stage:{}", appId, stage);

        // 获取消息
        AppDelayMessage one =
                lambdaQuery()
                        .eq(AppDelayMessage::getAppId, appId)
                        .eq(AppDelayMessage::getStage, stage)
                        .orderByDesc(AppDelayMessage::getCreateTime)
                        .last("limit 1")
                        .one();

        // 无消息数据跳过处理
        if (Objects.isNull(one)) {
            return Boolean.TRUE;
        }

        // 消息已超时
        if (AppDelayMessage.Status.TIMEOUT.equals(one.getStatus())) {
            LogUtils.error("@@ 接口调用超时, 消息内容:{}", JSON.toJSONString(one));
            return Boolean.FALSE;
        }

        // 修改状态为[以处理]
        lambdaUpdate()
                .set(AppDelayMessage::getStatus, AppDelayMessage.Status.PROCESSED)
                .set(AppDelayMessage::getModifyTime, LocalDateTime.now())
                .eq(AppDelayMessage::getId, one.getId())
                .update();
        return Boolean.TRUE;
    }
}
