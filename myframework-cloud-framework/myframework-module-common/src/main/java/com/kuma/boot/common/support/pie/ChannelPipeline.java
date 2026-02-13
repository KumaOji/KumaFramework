/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pie;

import com.kuma.boot.common.support.pie.Channel;
import com.kuma.boot.common.support.pie.ChannelHandler;
import com.kuma.boot.common.support.pie.ChannelHandlerContext;

public interface ChannelPipeline {
    public ChannelPipeline process(Object var1, Object var2);

    public ChannelPipeline addLast(String var1, ChannelHandler var2);

    public Channel channel();

    public ChannelPipeline fireExceptionCaught(Throwable var1, Object var2, Object var3);

    public ChannelPipeline fireChannelProcess(Object var1, Object var2);

    public ChannelHandlerContext head();

    public ChannelHandlerContext tail();
}

