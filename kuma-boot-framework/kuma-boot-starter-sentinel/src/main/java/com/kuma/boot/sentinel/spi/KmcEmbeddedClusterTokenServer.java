package com.kuma.boot.sentinel.spi;

import com.alibaba.csp.sentinel.cluster.TokenResult;
import com.alibaba.csp.sentinel.cluster.server.EmbeddedClusterTokenServer;

import java.util.Collection;

//由 EmbeddedClusterTokenServerProvider 类触发
public class KmcEmbeddedClusterTokenServer implements EmbeddedClusterTokenServer {
    @Override
    public TokenResult requestToken(Long aLong, int i, boolean b) {
        return null;
    }

    @Override
    public TokenResult requestParamToken(Long aLong, int i, Collection<Object> collection) {
        return null;
    }

    @Override
    public TokenResult requestConcurrentToken(String s, Long aLong, int i) {
        return null;
    }

    @Override
    public void releaseConcurrentToken(Long aLong) {

    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {

    }
}
