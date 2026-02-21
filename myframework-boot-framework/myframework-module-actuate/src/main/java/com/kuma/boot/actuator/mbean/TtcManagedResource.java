/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.jmx.export.annotation.ManagedAttribute
 *  org.springframework.jmx.export.annotation.ManagedOperation
 *  org.springframework.jmx.export.annotation.ManagedResource
 */
package com.kuma.boot.actuator.mbean;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(objectName="com.kuma.boot.actuator.endpoint:name=KmcMBean", description="kmc managed resource")
public class KmcManagedResource {
    private String name = "default";
    private int counter = 0;

    @ManagedAttribute(description="The name attribute")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManagedAttribute(description="Current counter value")
    public int getCounter() {
        return this.counter;
    }

    @ManagedOperation(description="Reset the counter to zero")
    public void resetCounter() {
        this.counter = 0;
    }
}

