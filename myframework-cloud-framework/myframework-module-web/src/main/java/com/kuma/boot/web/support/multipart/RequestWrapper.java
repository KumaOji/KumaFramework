/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.lambda.StreamUtils
 *  com.kuma.boot.common.utils.lang.ObjectUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  jakarta.servlet.ReadListener
 *  jakarta.servlet.ServletInputStream
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletRequestWrapper
 *  org.springframework.http.HttpHeaders
 *  org.springframework.util.CollectionUtils
 */
package com.kuma.boot.web.support.multipart;

import com.kuma.boot.common.utils.lambda.StreamUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

public class RequestWrapper
extends HttpServletRequestWrapper {
    private final HttpHeaders headers = new HttpHeaders();
    private final Map<String, String[]> parameter = new LinkedHashMap<String, String[]>(16);
    private String body;
    private boolean bodyReviseStatus = false;

    public RequestWrapper(HttpServletRequest request) {
        super(request);
        this.headers.putAll(RequestUtils.headers((HttpServletRequest)request));
        this.parameter.putAll(request.getParameterMap());
    }

    public void setBody(String body) {
        this.bodyReviseStatus = true;
        this.body = body;
    }

    public void addHeader(String name, String value) {
        this.headers.add(name, value);
    }

    public void putParameter(String name, String[] values) {
        this.parameter.merge(name, values, (oldValues, newValues) -> (String[])StreamUtils.concat((Object[][])new String[][]{oldValues, newValues}).distinct().toArray(String[]::new));
    }

    public void putParameter(String name, String value) {
        this.putParameter(name, new String[]{value});
    }

    public ServletInputStream getInputStream() throws IOException {
        return this.bodyReviseStatus ? new RequestServletInputStream(this.getRequest(), this.body) : super.getInputStream();
    }

    public int getContentLength() {
        try {
            return this.bodyReviseStatus ? this.body.getBytes(this.getRequest().getCharacterEncoding()).length : super.getContentLength();
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public long getContentLengthLong() {
        try {
            return this.bodyReviseStatus ? (long)this.body.getBytes(this.getRequest().getCharacterEncoding()).length : super.getContentLengthLong();
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getContentType() {
        return this.bodyReviseStatus ? "application/json" : super.getContentType();
    }

    public String getParameter(String name) {
        Object[] values = this.getParameterValues(name);
        return ObjectUtils.isEmpty((Object[])values) ? null : values[0];
    }

    public Map<String, String[]> getParameterMap() {
        return this.parameter;
    }

    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.parameter.keySet());
    }

    public String[] getParameterValues(String name) {
        return this.parameter.get(name);
    }

    public String getHeader(String name) {
        if ("Content-Type".equalsIgnoreCase(name) && this.bodyReviseStatus) {
            return this.getContentType();
        }
        return this.headers.getFirst(name);
    }

    public Enumeration<String> getHeaderNames() {
        Set headerNames = this.headers.toSingleValueMap().keySet();
        if (this.bodyReviseStatus) {
            headerNames.add("Content-Type");
        }
        return Collections.enumeration(this.headers.toSingleValueMap().keySet());
    }

    public Enumeration<String> getHeaders(String name) {
        List list;
        HashSet<String> headerValues = new HashSet<String>();
        if ("Content-Type".equalsIgnoreCase(name) && this.bodyReviseStatus) {
            headerValues.add(this.getContentType());
        }
        if (!CollectionUtils.isEmpty((Collection)(list = this.headers.get(name)))) {
            headerValues.addAll(list);
        }
        return Collections.enumeration(headerValues);
    }

    private static class RequestServletInputStream
    extends ServletInputStream {
        private final InputStream in;

        public RequestServletInputStream(ServletRequest request, String json) throws UnsupportedEncodingException {
            this.in = new ByteArrayInputStream(json.getBytes(request.getCharacterEncoding()));
        }

        public boolean isFinished() {
            return false;
        }

        public boolean isReady() {
            return false;
        }

        public void setReadListener(ReadListener listener) {
            try {
                listener.onDataAvailable();
            }
            catch (IOException e) {
                LogUtils.error((Throwable)e);
            }
        }

        public int read() throws IOException {
            return this.in.read();
        }
    }

    private static class HeaderEnumeration
    implements Enumeration<String> {
        private final String contentType;
        private boolean hasMoreElements = false;

        public HeaderEnumeration(String contentType) {
            this.contentType = contentType;
        }

        @Override
        public boolean hasMoreElements() {
            return !this.hasMoreElements;
        }

        @Override
        public String nextElement() {
            if (this.hasMoreElements) {
                throw new NoSuchElementException();
            }
            this.hasMoreElements = true;
            return this.contentType;
        }
    }
}

