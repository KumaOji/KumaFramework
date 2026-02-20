/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.ServletOutputStream
 *  jakarta.servlet.WriteListener
 *  jakarta.servlet.http.HttpServletResponse
 *  jakarta.servlet.http.HttpServletResponseWrapper
 *  org.jspecify.annotations.NonNull
 */
package com.kuma.boot.web.support.multipart;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import org.jspecify.annotations.NonNull;

public class ResponseWrapper
extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final ServletOutputStream out = new WrapperOutputStream(this.buffer);
    private final PrintWriter writer = new PrintWriter(new OutputStreamWriter((OutputStream)this.buffer, this.getCharacterEncoding()));

    public ResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
    }

    public ServletOutputStream getOutputStream() {
        return this.out;
    }

    public PrintWriter getWriter() {
        return this.writer;
    }

    public void flushBuffer() throws IOException {
        this.out.flush();
        this.writer.flush();
    }

    public void reset() {
        this.buffer.reset();
    }

    public byte[] getResponseData() throws IOException {
        this.flushBuffer();
        return this.buffer.toByteArray();
    }

    private static class WrapperOutputStream
    extends ServletOutputStream {
        private final ByteArrayOutputStream stream;

        public WrapperOutputStream(ByteArrayOutputStream stream) {
            this.stream = stream;
        }

        public void write(int b) {
            this.stream.write(b);
        }

        public void write(@NonNull byte[] b) {
            this.stream.writeBytes(b);
        }

        public void setWriteListener(WriteListener writeListener) {
            try {
                writeListener.onWritePossible();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean isReady() {
            return false;
        }
    }
}

