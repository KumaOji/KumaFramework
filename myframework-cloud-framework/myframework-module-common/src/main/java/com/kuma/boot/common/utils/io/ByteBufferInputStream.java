/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.io;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public final class ByteBufferInputStream
extends InputStream {
    private final ByteBuffer byteBuffer;

    public ByteBufferInputStream(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public ByteBufferInputStream(ByteBuffer byteBuffer, Consumer<ByteBuffer> initFn) {
        this(byteBuffer);
        initFn.accept(this.byteBuffer);
    }

    @Override
    public int available() {
        return this.byteBuffer.remaining();
    }

    @Override
    public int read() {
        return this.byteBuffer.hasRemaining() ? this.byteBuffer.get() & 0xFF : -1;
    }

    @Override
    public int read(byte[] bytes, int off, int len) {
        if (!this.byteBuffer.hasRemaining()) {
            return -1;
        }
        len = Math.min(len, this.byteBuffer.remaining());
        this.byteBuffer.get(bytes, off, len);
        return len;
    }
}

