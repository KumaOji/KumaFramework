/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.interceptor.sqlanalysis.util;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.config.JmqConfig;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author huhaitao21
 * @Description 关键节点日志工具
 * @since 20:03 2023/2/20
 **/
public class KeyNodeLogUtil {

    private static Logger log = LoggerFactory.getLogger(KeyNodeLogUtil.class);

    private static RingBuffer<KeyNodeLogModel> ringBuffer = null;

    static {
        initDisruptor();
    }

    public static void initDisruptor() {
        // 指定RingBuffer的大小
        int bufferSize = 1024;
        // 批量提交日志大小
        int batchLogSize = 10;

        // 生产者的线程工厂
        ThreadFactory threadFactory =
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "logDisruptorThread");
                    }
                };

        // RingBuffer生产工厂,初始化RingBuffer的时候使用
        EventFactory<KeyNodeLogModel> factory =
                new EventFactory<KeyNodeLogModel>() {
                    @Override
                    public KeyNodeLogModel newInstance() {
                        return new KeyNodeLogModel();
                    }
                };

        // 阻塞策略
        BlockingWaitStrategy strategy = new BlockingWaitStrategy();

        // 创建disruptor，采用单生产者模式
        Disruptor<KeyNodeLogModel> disruptor =
                new Disruptor(factory, bufferSize, threadFactory, ProducerType.SINGLE, strategy);
        ringBuffer = disruptor.getRingBuffer();

        // 处理Event的handler
        EventHandler<KeyNodeLogModel> handler =
                new EventHandler<KeyNodeLogModel>() {
                    @Override
                    public void onEvent(KeyNodeLogModel element, long sequence, boolean endOfBatch)
                            throws InterruptedException {
                        if (Objects.isNull(element)) {
                            return;
                        }
                        sendSingleLog(element);
                    }
                };
        // 设置EventHandler
        disruptor.handleEventsWith(handler);

        // 启动disruptor的线程
        disruptor.start();
    }

    /**
     * 发布disruptor事件
     *
     * @param model
     */
    public static void applyLogEvent(KeyNodeLogModel model) {
        if (ringBuffer == null) {
            // disruptor未初始化
            log.error("disruptor未初始化，使用单发mq");
            sendSingleLog(model);
            return;
        }

        // 获取下一个可用位置的下标
        long sequence;
        try {
            sequence = ringBuffer.tryNext();
        } catch (InsufficientCapacityException e) {
            log.error("disruptor队列不足，使用单发mq");
            sendSingleLog(model);
            return;
        }
        try {
            // 返回可用位置的元素
            KeyNodeLogModel event = ringBuffer.get(sequence);
            // 设置该位置元素的值
            event.setBusinessId(model.getBusinessId());
            event.setBusinessTime(model.getBusinessTime());
            event.setDescribe(model.getDescribe());
            event.setLogTime(model.getLogTime());
            event.setModuleName(model.getModuleName());
            event.setNodeName(model.getNodeName());
        } catch (Exception e) {
            log.error("disruptor发布event失败", e);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    /**
     * 单条日志发送
     *
     * @param model
     */
    public static void sendSingleLog(KeyNodeLogModel model) {
        String content = GsonUtil.bean2Json(model);
        try {
            sendMessage(JmqConfig.getTopic(), model.getBusinessId(), content);
        } catch (Exception e) {
            log.error("sql analysis out mq error" + model.getBusinessId(), e);
        }
    }

    public static void sendLog(
            String businessId,
            String modelName,
            String nodeName,
            String describe,
            Date businessTime) {
        KeyNodeLogModel model =
                KeyNodeLogModel.builder()
                        .businessId(businessId)
                        .businessTime(businessTime)
                        .logTime(new Date())
                        .moduleName(modelName)
                        .nodeName(nodeName)
                        .describe(describe)
                        .build();

        if (!checkModel(model)) {
            return;
        }

        applyLogEvent(model);
    }

    /**
     * 检测模块 是否需要发送
     *
     * @param model
     * @return
     */
    public static boolean checkModel(KeyNodeLogModel model) {
        if (StringUtils.isBlank(model.getBusinessId())
                || StringUtils.isBlank(model.getModuleName())
                || StringUtils.isBlank(model.getNodeName())) {
            return false;
        }
        return true;
    }

    public static void sendMessage(String topic, String businessId, String messageBody) {
        try {
            // todo 替换开源mq消息发送
            //            if(producer==null){
            //                initProducer();
            //            }
            //            MessageProducer producer = JmqConfig.getProducer();
            //            if(StringUtils.isNotBlank(businessId) && businessId.length()>16){
            //                businessId = businessId.substring(0,16);
            //            }
            //            Message message = new Message(topic, messageBody, businessId);
            //            producer.send(message);
        } catch (Exception e) {
            LogUtils.error(e);
            log.error("sql analysis send mq error : {}", e);
        }
    }

    public static synchronized void initProducer() {}
}
