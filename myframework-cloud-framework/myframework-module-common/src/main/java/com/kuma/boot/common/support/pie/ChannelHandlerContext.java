/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pie;

import com.kuma.boot.common.support.pie.Channel;
import com.kuma.boot.common.support.pie.ChannelHandler;
import com.kuma.boot.common.support.pie.ChannelPipeline;

public interface ChannelHandlerContext {
    public Channel channel();

    public ChannelHandler handler();

    public ChannelPipeline pipeline();

    public ChannelHandlerContext process(Object var1, Object var2);

    public ChannelHandlerContext fireExceptionCaught(Throwable var1, Object var2, Object var3);

    public ChannelHandlerContext fireChannelProcess(Object var1, Object var2);
}

