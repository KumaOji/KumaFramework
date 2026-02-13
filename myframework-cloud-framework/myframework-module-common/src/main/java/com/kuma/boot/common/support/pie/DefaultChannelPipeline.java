/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.common.support.pie;

import com.kuma.boot.common.support.pie.AbstractChannelHandlerContext;
import com.kuma.boot.common.support.pie.Channel;
import com.kuma.boot.common.support.pie.ChannelHandler;
import com.kuma.boot.common.support.pie.ChannelHandlerContext;
import com.kuma.boot.common.support.pie.ChannelPipeline;
import com.kuma.boot.common.support.pie.DefaultChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultChannelPipeline
implements ChannelPipeline {
    AbstractChannelHandlerContext head;
    AbstractChannelHandlerContext tail;
    private static final String HEAD_NAME = DefaultChannelPipeline.generateName0(HeadContext.class);
    private static final String TAIL_NAME = DefaultChannelPipeline.generateName0(TailContext.class);
    private Channel channel;

    protected DefaultChannelPipeline(Channel channel) {
        this.channel = channel;
        this.tail = new TailContext(this);
        this.head = new HeadContext(this);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    @Override
    public ChannelPipeline addLast(String name, ChannelHandler handler) {
        AbstractChannelHandlerContext prev;
        DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(this, name, handler);
        newCtx.prev = prev = this.tail.prev;
        newCtx.next = this.tail;
        prev.next = newCtx;
        this.tail.prev = newCtx;
        return this;
    }

    @Override
    public Channel channel() {
        return this.channel;
    }

    @Override
    public ChannelPipeline fireExceptionCaught(Throwable cause, Object in, Object out) {
        AbstractChannelHandlerContext.invokeExceptionCaught(this.head, cause, in, out);
        return this;
    }

    @Override
    public ChannelPipeline fireChannelProcess(Object in, Object out) {
        AbstractChannelHandlerContext.invokeChannelProcess(this.head, in, out);
        return this;
    }

    private static String generateName0(Class<?> handlerType) {
        return handlerType.getSimpleName() + "#0";
    }

    @Override
    public ChannelPipeline process(Object in, Object out) {
        this.head.process(in, out);
        return this;
    }

    @Override
    public ChannelHandlerContext head() {
        return this.head;
    }

    @Override
    public ChannelHandlerContext tail() {
        return this.tail;
    }

    static final class TailContext
    extends AbstractChannelHandlerContext
    implements ChannelHandler {
        private Logger logger = LoggerFactory.getLogger(TailContext.class);

        TailContext(DefaultChannelPipeline pipeline) {
            super(pipeline, TAIL_NAME, TailContext.class);
        }

        @Override
        public ChannelHandler handler() {
            return this;
        }

        @Override
        public void channelProcess(ChannelHandlerContext ctx, Object in, Object out) throws Exception {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("tail:channelProcess:there is no more handler");
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause, Object in, Object out) throws Exception {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("tail:exceptionCaught:there is no more handler");
            }
        }
    }

    static final class HeadContext
    extends AbstractChannelHandlerContext
    implements ChannelHandler {
        private Logger logger = LoggerFactory.getLogger(HeadContext.class);

        HeadContext(DefaultChannelPipeline pipeline) {
            super(pipeline, HEAD_NAME, HeadContext.class);
        }

        @Override
        public ChannelHandler handler() {
            return this;
        }

        @Override
        public void channelProcess(ChannelHandlerContext ctx, Object in, Object out) throws Exception {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("head:channelProcess");
            }
            ctx.fireChannelProcess(in, out);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause, Object in, Object out) throws Exception {
            this.logger.info("head:exceptionCaught");
        }
    }
}

