/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.support;

import java.util.UUID;

public class DefaultDingerIdGenerator
implements DingerIdGenerator {
    @Override
    public String dingerId() {
        StringBuilder dkid = new StringBuilder("D");
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        dkid.append(uuid);
        return dkid.toString();
    }
}

