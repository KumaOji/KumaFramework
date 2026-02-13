/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.io;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public final class ByteBufferOutputStream
extends OutputStream {
    private final ByteBuffer byteBuffer;

    public ByteBufferOutputStream(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public ByteBufferOutputStream(ByteBuffer byteBuffer, Consumer<ByteBuffer> initFn) {
        this(byteBuffer);
        initFn.accept(this.byteBuffer);
    }

    @Override
    public void write(int b) {
        this.byteBuffer.put((byte)b);
    }

    @Override
    public void write(byte[] bytes, int off, int len) {
        this.byteBuffer.put(bytes, off, len);
    }
}

