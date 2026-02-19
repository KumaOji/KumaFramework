/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.servlet.FilterChain
 *  jakarta.servlet.ServletException
 *  jakarta.servlet.ServletRequest
 *  jakarta.servlet.ServletResponse
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  jakarta.xml.bind.DatatypeConverter
 *  org.apache.commons.io.IOUtils
 *  org.springframework.web.filter.OncePerRequestFilter
 *  org.springframework.web.util.ContentCachingRequestWrapper
 *  org.springframework.web.util.ContentCachingResponseWrapper
 *  org.springframework.web.util.WebUtils
 */
package com.kuma.boot.web.servlet.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.io.IOUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

public class ContentFilter
extends OncePerRequestFilter {
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, 0);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        String requestBody = this.getRequestBody((HttpServletRequest)requestWrapper);
        filterChain.doFilter((ServletRequest)requestWrapper, (ServletResponse)responseWrapper);
        byte[] responseBody = responseWrapper.getContentAsByteArray();
        MessageDigest md5Digest = null;
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] md5Hash = md5Digest.digest(responseBody);
        String md5HashString = DatatypeConverter.printHexBinary((byte[])md5Hash);
        responseWrapper.setHeader("X-API-SIGN", md5HashString);
        responseWrapper.copyBodyToResponse();
    }

    private String getRequestBody(HttpServletRequest request) {
        String requestBody = "";
        ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper)WebUtils.getNativeRequest((ServletRequest)request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            requestBody = IOUtils.toString((byte[])wrapper.getContentAsByteArray(), (String)wrapper.getCharacterEncoding());
        }
        return requestBody;
    }
}

