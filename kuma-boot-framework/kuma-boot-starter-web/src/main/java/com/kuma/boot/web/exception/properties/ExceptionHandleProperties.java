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

package com.kuma.boot.web.exception.properties;

import com.kuma.boot.web.exception.enums.ExceptionHandleTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = ExceptionHandleProperties.PREFIX)
public class ExceptionHandleProperties {

    public static final String PREFIX = "kuma.boot.web.global-exception";

    /** 处理类型 */
    private ExceptionHandleTypeEnum[] types =
            new ExceptionHandleTypeEnum[] {ExceptionHandleTypeEnum.LOGGER};

    /** 是否开启异常通知 */
    private Boolean enabled = true;

    /** 是否同时忽略 配置的忽略异常类的子类, 默认忽略子类 */
    private Boolean ignoreChild = true;

    /** 忽略指定异常 */
    private Set<Class<? extends Throwable>> ignoreExceptions = new HashSet<>();

    /** 通知间隔时间 单位秒 默认 5分钟 */
    private long time = TimeUnit.MINUTES.toSeconds(5);

    /** 消息阈值 即便间隔时间没有到达设定的时间， 但是异常发生的数量达到阈值 则立即发送消息 */
    private int max = 5;

    /** 堆栈转string 的长度 */
    private int length = 3000;

    /** 接收异常通知邮件的邮箱 */
    private Set<String> receiveEmails = new HashSet<>(0);

    public ExceptionHandleTypeEnum[] getTypes() {
        return types;
    }

    public void setTypes( ExceptionHandleTypeEnum[] types) {
        this.types = types;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getIgnoreChild() {
        return ignoreChild;
    }

    public void setIgnoreChild(Boolean ignoreChild) {
        this.ignoreChild = ignoreChild;
    }

    public Set<Class<? extends Throwable>> getIgnoreExceptions() {
        return ignoreExceptions;
    }

    public void setIgnoreExceptions(Set<Class<? extends Throwable>> ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Set<String> getReceiveEmails() {
        return receiveEmails;
    }

    public void setReceiveEmails(Set<String> receiveEmails) {
        this.receiveEmails = receiveEmails;
    }
}
