/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pie;

import com.kuma.boot.common.support.pie.Channel;
import com.kuma.boot.common.support.pie.ChannelHandler;
import com.kuma.boot.common.support.pie.ChannelHandlerContext;
import com.kuma.boot.common.support.pie.ChannelPipeline;
import com.kuma.boot.common.support.pie.DefaultChannelPipeline;
import com.kuma.boot.common.support.pie.ObjectUtil;

public abstract class AbstractChannelHandlerContext
implements ChannelHandlerContext {
    volatile AbstractChannelHandlerContext next;
    volatile AbstractChannelHandlerContext prev;
    private DefaultChannelPipeline pipeline;
    private String name;

    AbstractChannelHandlerContext(DefaultChannelPipeline pipeline, String name, Class<? extends ChannelHandler> handlerClass) {
        this.name = (String)ObjectUtil.checkNotNull(name, "name");
        this.pipeline = pipeline;
    }

    @Override
    public Channel channel() {
        return this.pipeline.channel();
    }

    @Override
    public ChannelPipeline pipeline() {
        return this.pipeline;
    }

    @Override
    public ChannelHandlerContext fireExceptionCaught(Throwable cause, Object in, Object out) {
        AbstractChannelHandlerContext.invokeExceptionCaught(this.next, cause, in, out);
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelProcess(Object in, Object out) {
        AbstractChannelHandlerContext.invokeChannelProcess(this.next, in, out);
        return this;
    }

    private void invokeExceptionCaught(Throwable cause, Object in, Object out) {
        try {
            this.handler().exceptionCaught(this, cause, in, out);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void invokeChannelProcess(Object in, Object out) {
        try {
            this.handler().channelProcess(this, in, out);
        }
        catch (Throwable throwable) {
            this.invokeExceptionCaught(throwable, in, out);
        }
    }

    static void invokeExceptionCaught(AbstractChannelHandlerContext next, Throwable cause, Object in, Object out) {
        next.invokeExceptionCaught(cause, in, out);
    }

    static void invokeChannelProcess(AbstractChannelHandlerContext next, Object in, Object out) {
        next.invokeChannelProcess(in, out);
    }

    @Override
    public ChannelHandlerContext process(Object in, Object out) {
        try {
            this.handler().channelProcess(this, in, out);
        }
        catch (Throwable t) {
            this.invokeExceptionCaught(t, in, out);
        }
        return this;
    }
}

