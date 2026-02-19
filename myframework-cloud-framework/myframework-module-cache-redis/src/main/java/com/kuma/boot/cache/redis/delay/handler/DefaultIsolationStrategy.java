/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.cache.redis.delay.handler;

import com.kuma.boot.common.utils.log.LogUtils;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DefaultIsolationStrategy
implements IsolationStrategy {
    @Override
    public String getRedisQueueName(String queue) {
        Object prefix;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String hostAddress = localHost.getHostAddress();
            String hostName = localHost.getHostName();
            prefix = hostName + "@" + hostAddress;
        }
        catch (UnknownHostException e) {
            LogUtils.warn((String)"can not detect host info,instead with localhost@127.0.0.1", (Object[])new Object[0]);
            prefix = "localhost@127.0.0.1";
        }
        return (String)prefix + "-" + queue;
    }
}

