/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.compliance.processor.changer;

import com.kuma.boot.security.spring.event.ApplicationStrategyEvent;
import com.kuma.boot.security.spring.event.domain.TtcUserStatus;

public interface AccountStatusChanger
extends ApplicationStrategyEvent<TtcUserStatus> {
    public String getDestinationServiceName();

    default public void process(TtcUserStatus status) {
        this.postProcess(this.getDestinationServiceName(), status);
    }
}

