/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.compliance.processor.loginrisk;

import java.util.List;
import java.util.Map;

public abstract class AbstractLoginHandle {
    public AbstractLoginHandle nextHandle;

    public void setNextHandle(AbstractLoginHandle nextHandle) {
        this.nextHandle = nextHandle;
    }

    public abstract void filterRisk(List<RiskRule> var1, Map<Integer, RiskRule> var2, UserAccount var3);
}

