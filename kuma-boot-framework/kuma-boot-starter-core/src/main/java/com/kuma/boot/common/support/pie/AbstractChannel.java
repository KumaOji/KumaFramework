/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.support.pie;

/**
 * 抽象通道
 *
 */
public abstract class AbstractChannel implements Channel {

    private DefaultChannelPipeline pipeline;

    private ChannelProcessor processor = new DefaultChannelProcessorImpl();

    protected AbstractChannel() {
        pipeline = newChannelPipeline();
    }

    protected DefaultChannelPipeline newChannelPipeline() {
        return new DefaultChannelPipeline(this);
    }

    @Override
    public ChannelPipeline pipeline() {
        return pipeline;
    }

    public Channel process( Object inWrapper, Object outWrapper ) {
        processor.doProcess(inWrapper, outWrapper);
        return this;
    }

    /**
     * DefaultChannelProcessorImpl
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-17 10:30:45
     */
    private class DefaultChannelProcessorImpl implements ChannelProcessor {

        @Override
        public void doProcess( Object inWrapper, Object outWrapper ) {
            pipeline.process(inWrapper, outWrapper);
        }
    }
}
