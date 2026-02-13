/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.exception;

import com.kuma.boot.common.utils.exception.FastStringWriter;
import java.io.PrintWriter;

public class FastStringPrintWriter
extends PrintWriter {
    private final FastStringWriter writer;

    public FastStringPrintWriter() {
        this(256);
    }

    public FastStringPrintWriter(int initialSize) {
        super(new FastStringWriter(initialSize));
        this.writer = (FastStringWriter)this.out;
    }

    @Override
    public void println(Object x) {
        this.writer.write(String.valueOf(x));
        this.writer.write("\n");
    }

    public String toString() {
        return this.writer.toString();
    }
}

