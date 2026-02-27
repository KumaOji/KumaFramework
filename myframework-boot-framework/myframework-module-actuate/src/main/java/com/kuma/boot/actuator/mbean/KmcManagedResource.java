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

/**
 * MBeanDemo
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 21:05:36
 */
@ManagedResource(objectName = "com.kuma.boot.actuator.endpoint:name=KmcMBean",
        description = "kmc managed resource")
public class KmcManagedResource {

    private String name = "default";
    private int counter = 0;

    @ManagedAttribute(description = "The name attribute")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManagedAttribute(description = "Current counter value")
    public int getCounter() {
        return counter;
    }

    @ManagedOperation(description = "Reset the counter to zero")
    public void resetCounter() {
        this.counter = 0;
    }
}
