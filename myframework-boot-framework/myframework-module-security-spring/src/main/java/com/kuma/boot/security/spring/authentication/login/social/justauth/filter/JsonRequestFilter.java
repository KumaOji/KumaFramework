/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.ReadListener
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletInputStream
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletRequestWrapper
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.lang.NonNull
 *  org.springframework.web.filter.OncePerRequestFilter
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

public class JsonRequestFilter
extends OncePerRequestFilter {
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter((ServletRequest)new JsonRequest(request), (ServletResponse)response);
    }

    public static class JsonRequest
    extends HttpServletRequestWrapper {
        private final byte[] body;
        private final Map<String, Object> formMap;

        JsonRequest(HttpServletRequest request) {
            super(request);
            boolean isJsonContentType;
            String contentType = request.getContentType();
            String method = request.getMethod();
            boolean isPostOrPutRequest = "POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method);
            boolean bl = isJsonContentType = contentType != null && contentType.contains("application/json");
            if (isPostOrPutRequest && isJsonContentType) {
                Object map = null;
                Object bytes = null;
                this.formMap = Optional.ofNullable(map).orElse(new HashMap(0));
                this.body = bytes;
            } else {
                this.body = null;
                this.formMap = null;
            }
        }

        public ServletInputStream getInputStream() throws IOException {
            if (this.body == null) {
                return super.getInputStream();
            }
            return new BodyInputStream(this.body);
        }

        public String getParameter(String name) {
            String parameter = super.getParameter(name);
            if (parameter == null && this.formMap != null) {
                return (String)this.formMap.get(name);
            }
            return parameter;
        }

        public byte[] getBody() {
            return this.body;
        }

        public Map<String, Object> getFormMap() {
            return this.formMap;
        }
    }

    private static class BodyInputStream
    extends ServletInputStream {
        private final InputStream delegate;

        public BodyInputStream(byte[] body) {
            this.delegate = new ByteArrayInputStream(body);
        }

        public boolean isFinished() {
            return false;
        }

        public boolean isReady() {
            return true;
        }

        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        public int read() throws IOException {
            return this.delegate.read();
        }

        public int read(@NonNull byte[] b, int off, int len) throws IOException {
            return this.delegate.read(b, off, len);
        }

        public int read(@NonNull byte[] b) throws IOException {
            return this.delegate.read(b);
        }

        public long skip(long n) throws IOException {
            return this.delegate.skip(n);
        }

        public int available() throws IOException {
            return this.delegate.available();
        }

        public void close() throws IOException {
            this.delegate.close();
        }

        public synchronized void mark(int readlimit) {
            this.delegate.mark(readlimit);
        }

        public synchronized void reset() throws IOException {
            this.delegate.reset();
        }

        public boolean markSupported() {
            return this.delegate.markSupported();
        }
    }
}

