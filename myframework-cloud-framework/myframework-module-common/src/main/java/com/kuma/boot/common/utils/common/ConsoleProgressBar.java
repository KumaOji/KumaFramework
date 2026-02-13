/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.common;

import java.text.DecimalFormat;

public final class ConsoleProgressBar {
    private long minimum = 0L;
    private long maximum = 100L;
    private long barLen = 100L;
    private char showChar = (char)61;
    private final DecimalFormat formater = new DecimalFormat("#.##%");

    public ConsoleProgressBar() {
    }

    public ConsoleProgressBar(long minimum, long maximum, long barLen) {
        this(minimum, maximum, barLen, '=');
    }

    public ConsoleProgressBar(long minimum, long maximum, long barLen, char showChar) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.barLen = barLen;
        this.showChar = showChar;
    }

    public void show(long value) {
        if (value < this.minimum || value > this.maximum) {
            return;
        }
        this.reset();
        this.minimum = value;
        float rate = (float)((double)this.minimum * 1.0 / (double)this.maximum);
        long len = (long)(rate * (float)this.barLen);
        this.draw(len, rate);
        if (this.minimum == this.maximum) {
            this.afterComplete();
        }
    }

    private void draw(long len, float rate) {
        int i = 0;
        while ((long)i < len) {
            System.out.print(this.showChar);
            ++i;
        }
        System.out.print(' ');
        System.out.print(this.format(rate));
    }

    private void reset() {
        System.out.print('\r');
    }

    private void afterComplete() {
        System.out.print('\n');
    }

    private String format(float num) {
        return this.formater.format(num);
    }
}

