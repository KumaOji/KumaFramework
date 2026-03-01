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

package com.kuma.cloud.stream.framework.trigger.model;

import java.io.Serial;
import java.io.Serializable;

/** 延时任务消息 */
public class TimeTriggerMsg implements Serializable {

    @Serial
    private static final long serialVersionUID = 8897917127201859535L;

    /** 执行器beanId */
    private String triggerExecutor;

    /** 执行器 执行时间 */
    private Long triggerTime;

    /** 执行器参数 */
    private Object param;

    /** 唯一KEY */
    private String uniqueKey;

    /** 信息队列主题 */
    private String topic;

    private String type;

    public TimeTriggerMsg() {}

    public TimeTriggerMsg(String triggerExecutor, Long triggerTime, Object param, String uniqueKey, String topic) {
        this.triggerExecutor = triggerExecutor;
        this.triggerTime = triggerTime;
        this.param = param;
        this.uniqueKey = uniqueKey;
        this.topic = topic;
    }

    public String getTriggerExecutor() {
        return triggerExecutor;
    }

    public void setTriggerExecutor(String triggerExecutor) {
        this.triggerExecutor = triggerExecutor;
    }

    public Long getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(Long triggerTime) {
        this.triggerTime = triggerTime;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
