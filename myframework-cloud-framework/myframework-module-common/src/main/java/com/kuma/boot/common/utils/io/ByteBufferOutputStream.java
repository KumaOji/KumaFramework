/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.utils.io;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * 包装ByteBuffer的OutputStream 注意：ByteBuffer的操作需要自行完成或通过带initFn的构造来完成
 *
 */
public final class ByteBufferOutputStream extends OutputStream {

    private final ByteBuffer byteBuffer;

    public ByteBufferOutputStream(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    /**
     * 可以通过这个构造对ByteBuffer进行初始化 比如 clear 和 compact
     */
    public ByteBufferOutputStream(ByteBuffer byteBuffer, Consumer<ByteBuffer> initFn) {
        this(byteBuffer);
        initFn.accept(this.byteBuffer);
    }

    @Override
    public void write(int b) {
        byteBuffer.put((byte) b);
    }

    @Override
    public void write(byte[] bytes, int off, int len) {
        byteBuffer.put(bytes, off, len);
    }
}
