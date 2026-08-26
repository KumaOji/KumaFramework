package com.kuma.cloud.lab.javacore.domain.vo;

import java.util.List;

/**
 * Socket 通信演示结果。
 */
public record SocketDemoVO(
        int port,
        boolean serverRunning,
        String request,
        String response,
        List<String> protocolNotes
) {
}
