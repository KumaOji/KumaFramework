/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pie;

import com.kuma.boot.common.support.pie.ChannelHandlerContext;

public interface ChannelHandler {
    public void channelProcess(ChannelHandlerContext var1, Object var2, Object var3) throws Exception;

    public void exceptionCaught(ChannelHandlerContext var1, Throwable var2, Object var3, Object var4) throws Exception;
}

