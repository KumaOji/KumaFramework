/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.rocketmq.client.producer.SendCallback
 *  org.apache.rocketmq.client.producer.SendResult
 */
package com.kuma.cloud.stream.framework.rocketmq;

import com.kuma.boot.common.utils.log.LogUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;

public class RocketmqSendCallback
implements SendCallback {
    public void onSuccess(SendResult sendResult) {
        LogUtils.info((String)"async onSuccess SendResult={}", (Object[])new Object[]{sendResult});
    }

    public void onException(Throwable throwable) {
        LogUtils.error((String)"async onException Throwable", (Object[])new Object[]{throwable});
    }
}

