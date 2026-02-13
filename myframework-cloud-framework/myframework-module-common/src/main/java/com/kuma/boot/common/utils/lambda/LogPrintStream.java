/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.lambda;

import com.kuma.boot.common.utils.exception.ExceptionUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class LogPrintStream
extends PrintStream {
    private final boolean error;

    private LogPrintStream(boolean error) throws UnsupportedEncodingException {
        super((OutputStream)(error ? System.err : System.out), false, StandardCharsets.UTF_8);
        this.error = error;
    }

    public static LogPrintStream log(boolean isError) {
        try {
            return new LogPrintStream(isError);
        }
        catch (UnsupportedEncodingException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    @Override
    public void print(String s) {
        if (this.error) {
            LogUtils.error(s, new Object[0]);
        } else {
            LogUtils.info(s, new Object[0]);
        }
    }

    @Override
    public void println() {
    }

    @Override
    public void println(String x) {
        if (this.error) {
            LogUtils.error(x, new Object[0]);
        } else {
            LogUtils.info(x, new Object[0]);
        }
    }

    @Override
    public PrintStream printf(String format, Object ... args) {
        if (this.error) {
            LogUtils.error(String.format(format, args), new Object[0]);
        } else {
            LogUtils.info(String.format(format, args), new Object[0]);
        }
        return this;
    }

    @Override
    public PrintStream printf(Locale l, String format, Object ... args) {
        if (this.error) {
            LogUtils.error(String.format(l, format, args), new Object[0]);
        } else {
            LogUtils.info(String.format(l, format, args), new Object[0]);
        }
        return this;
    }
}

