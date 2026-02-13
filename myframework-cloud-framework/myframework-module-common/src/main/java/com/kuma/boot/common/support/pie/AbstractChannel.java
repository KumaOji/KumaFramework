/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.pie;

import com.kuma.boot.common.support.pie.Channel;
import com.kuma.boot.common.support.pie.ChannelPipeline;
import com.kuma.boot.common.support.pie.DefaultChannelPipeline;
import java.util.Objects;

public abstract class AbstractChannel
implements Channel {
    private DefaultChannelPipeline pipeline;
    private Channel.ChannelProcessor processor = new DefaultChannelProcessorImpl(this);

    protected AbstractChannel() {
        this.pipeline = this.newChannelPipeline();
    }

    protected DefaultChannelPipeline newChannelPipeline() {
        return new DefaultChannelPipeline(this);
    }

    @Override
    public ChannelPipeline pipeline() {
        return this.pipeline;
    }

    @Override
    public Channel process(Object inWrapper, Object outWrapper) {
        this.processor.doProcess(inWrapper, outWrapper);
        return this;
    }

    private class DefaultChannelProcessorImpl
    implements Channel.ChannelProcessor {
        final /* synthetic */ AbstractChannel this$0;

        private DefaultChannelProcessorImpl(AbstractChannel abstractChannel) {
            AbstractChannel abstractChannel2 = abstractChannel;
            Objects.requireNonNull(abstractChannel2);
            this.this$0 = abstractChannel2;
        }

        @Override
        public void doProcess(Object inWrapper, Object outWrapper) {
            this.this$0.pipeline.process(inWrapper, outWrapper);
        }
    }
}

