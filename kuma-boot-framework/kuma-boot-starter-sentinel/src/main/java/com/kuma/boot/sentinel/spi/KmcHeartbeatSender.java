package com.kuma.boot.sentinel.spi;

import com.alibaba.csp.sentinel.transport.HeartbeatSender;

//这个功能是与 Sentinel 的 dashboard 进行心跳连接。
public class KmcHeartbeatSender implements HeartbeatSender {
    @Override
    public boolean sendHeartbeat() throws Exception {
        return false;
    }

    @Override
    public long intervalMs() {
        return 30L;
    }
}
