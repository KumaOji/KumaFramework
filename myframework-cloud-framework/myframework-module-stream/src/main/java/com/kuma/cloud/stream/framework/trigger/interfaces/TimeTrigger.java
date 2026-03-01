/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.cloud.stream.framework.trigger.interfaces;

import com.kuma.cloud.stream.framework.trigger.model.TimeTriggerMsg;

public interface TimeTrigger {
    public void addDelay(TimeTriggerMsg var1);

    public void execute(TimeTriggerMsg var1);

    public void edit(String var1, Object var2, Long var3, Long var4, String var5, int var6, String var7);

    public void delete(String var1, Long var2, String var3, String var4);
}

