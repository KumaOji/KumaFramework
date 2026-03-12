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

package com.kuma.boot.actuator.mbean;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * JMX MBean + REST 双路访问的业务状态资源。
 *
 * @author kuma
 * @since 2021-09-02
 */
@ManagedResource(objectName = "com.kuma.boot.actuator.endpoint:name=KmcMBean",
        description = "kmc managed resource")
public class KmcManagedResource {

    private volatile String name = "default";
    private final AtomicInteger counter = new AtomicInteger(0);

    @ManagedAttribute(description = "The name attribute")
    public String getName() {
        return name;
    }

    @ManagedAttribute(description = "The name attribute")
    public void setName(String name) {
        this.name = name;
    }

    @ManagedAttribute(description = "Current counter value")
    public int getCounter() {
        return counter.get();
    }

    @ManagedOperation(description = "Increment the counter by one")
    public void incrementCounter() {
        counter.incrementAndGet();
    }

    @ManagedOperation(description = "Reset the counter to zero")
    public void resetCounter() {
        counter.set(0);
    }
}
