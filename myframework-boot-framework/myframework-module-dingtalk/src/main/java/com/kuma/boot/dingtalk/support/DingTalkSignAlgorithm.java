/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.support;

public class DingTalkSignAlgorithm
implements DingerSignAlgorithm<SignResult> {
    @Override
    public SignResult sign(String secret) throws Exception {
        Long timestamp = System.currentTimeMillis();
        String sign = this.algorithm(timestamp, secret);
        return new SignResult(sign, timestamp);
    }
}

