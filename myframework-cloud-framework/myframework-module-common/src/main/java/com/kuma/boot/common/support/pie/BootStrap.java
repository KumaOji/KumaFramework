/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pie;

import com.kuma.boot.common.support.pie.Channel;
import com.kuma.boot.common.support.pie.ChannelHandler;
import com.kuma.boot.common.support.pie.OutboundFactory;

public class BootStrap {
    private Channel channel;
    private Object in;
    private OutboundFactory outboundFactory;

    public BootStrap channel(Channel channel) {
        this.channel = channel;
        return this;
    }

    public BootStrap inboundParameter(Object in) {
        this.in = in;
        return this;
    }

    public BootStrap outboundFactory(OutboundFactory outboundFactory) {
        this.outboundFactory = outboundFactory;
        return this;
    }

    public BootStrap addChannelHandlerAtLast(String name, ChannelHandler channelHandler) {
        this.channel.pipeline().addLast(name, channelHandler);
        return this;
    }

    public Object process() {
        Object out = this.outboundFactory.newInstance();
        this.channel.process(this.in, out);
        return out;
    }
}

