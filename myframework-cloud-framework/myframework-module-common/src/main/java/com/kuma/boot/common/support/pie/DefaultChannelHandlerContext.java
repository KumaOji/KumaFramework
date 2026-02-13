/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pie;

import com.kuma.boot.common.support.pie.AbstractChannelHandlerContext;
import com.kuma.boot.common.support.pie.ChannelHandler;
import com.kuma.boot.common.support.pie.DefaultChannelPipeline;

public class DefaultChannelHandlerContext
extends AbstractChannelHandlerContext {
    private final ChannelHandler handler;

    DefaultChannelHandlerContext(DefaultChannelPipeline pipeline, String name, ChannelHandler handler) {
        super(pipeline, name, handler.getClass());
        this.handler = handler;
    }

    @Override
    public ChannelHandler handler() {
        return this.handler;
    }
}

