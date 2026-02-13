/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.common.utils.exception;

import java.io.Writer;
import org.jspecify.annotations.Nullable;

public class FastStringWriter
extends Writer {
    private char[] buf;
    private int count;

    public FastStringWriter() {
        this(64);
    }

    public FastStringWriter(int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException("Negative initial size: " + initialSize);
        }
        this.buf = new char[initialSize];
        this.count = 0;
    }

    @Override
    public void write(int c) {
        int newCount = this.count + 1;
        this.ensureCapacityInternal(newCount);
        this.buf[this.count] = (char)c;
        this.count = newCount;
    }

    @Override
    public void write(char[] c, int off, int len) {
        if (off < 0 || off > c.length || len < 0 || off + len > c.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        int newCount = this.count + len;
        this.ensureCapacityInternal(newCount);
        System.arraycopy(c, off, this.buf, this.count, len);
        this.count = newCount;
    }

    @Override
    public void write(@Nullable String str) {
        if (str == null) {
            return;
        }
        this.write(str, 0, str.length());
    }

    @Override
    public void write(@Nullable String str, int off, int len) {
        if (str == null) {
            return;
        }
        int newCount = this.count + len;
        this.ensureCapacityInternal(newCount);
        str.getChars(off, off + len, this.buf, this.count);
        this.count = newCount;
    }

    private void write(CharSequence s, int start, int end) {
        int len = end - start;
        this.ensureCapacityInternal(this.count + len);
        int i = start;
        int j = this.count;
        while (i < end) {
            this.buf[j] = s.charAt(i);
            ++i;
            ++j;
        }
        this.count += len;
    }

    @Override
    public FastStringWriter append(@Nullable CharSequence csq) {
        if (csq == null) {
            return this;
        }
        int length = csq.length();
        if (csq instanceof String) {
            this.write((String)csq, 0, length);
        } else {
            this.write(csq, 0, csq.length());
        }
        return this;
    }

    @Override
    public FastStringWriter append(@Nullable CharSequence csq, int start, int end) {
        if (csq == null) {
            return this;
        }
        if (csq instanceof String) {
            this.write((String)csq, start, end);
        } else {
            this.write(csq, start, end);
        }
        return this;
    }

    @Override
    public FastStringWriter append(char c) {
        this.write(c);
        return this;
    }

    public String toString() {
        return new String(this.buf, 0, this.count);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    private void ensureCapacityInternal(int minimumCapacity) {
        if (minimumCapacity > this.buf.length) {
            this.expandCapacity(minimumCapacity);
        }
    }

    private void expandCapacity(int minimumCapacity) {
        int newCapacity = Math.max(this.buf.length << 1, minimumCapacity);
        char[] newBuff = new char[newCapacity];
        if (this.count > 0) {
            System.arraycopy(this.buf, 0, newBuff, 0, this.count);
        }
        this.buf = newBuff;
    }
}

