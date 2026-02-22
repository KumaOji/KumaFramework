/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.security.core.context.DeferredSecurityContext
 *  org.springframework.security.core.context.SecurityContext
 *  org.springframework.security.core.context.SecurityContextHolder
 *  org.springframework.security.core.context.SecurityContextHolderStrategy
 *  org.springframework.security.web.context.HttpRequestResponseHolder
 *  org.springframework.security.web.context.SecurityContextRepository
 *  org.springframework.util.ObjectUtils
 */
package com.kuma.boot.security.spring.core.context;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.ObjectUtils;

public class RedisSecurityContextRepository
implements SecurityContextRepository {
    private final RedisRepository redisRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    public static final long DEFAULT_TIMEOUT_SECONDS = 300L;
    public static final String NONCE_HEADER_NAME = "nonce";
    public static final String SECURITY_CONTEXT_PREFIX_KEY = "security_context:";

    public RedisSecurityContextRepository(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Deprecated
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        throw new UnsupportedOperationException("Method deprecated.");
    }

    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        String nonce = request.getHeader(NONCE_HEADER_NAME);
        if (ObjectUtils.isEmpty((Object)nonce) && ObjectUtils.isEmpty((Object)(nonce = request.getParameter(NONCE_HEADER_NAME)))) {
            return;
        }
        SecurityContext emptyContext = this.securityContextHolderStrategy.createEmptyContext();
        if (emptyContext.equals((Object)context)) {
            this.redisRepository.del(new String[]{SECURITY_CONTEXT_PREFIX_KEY + nonce});
        } else {
            this.redisRepository.set(SECURITY_CONTEXT_PREFIX_KEY + nonce, (Object)context, Long.valueOf(300L));
        }
    }

    public boolean containsContext(HttpServletRequest request) {
        String nonce = request.getHeader(NONCE_HEADER_NAME);
        if (ObjectUtils.isEmpty((Object)nonce) && ObjectUtils.isEmpty((Object)(nonce = request.getParameter(NONCE_HEADER_NAME)))) {
            return false;
        }
        return this.redisRepository.get(SECURITY_CONTEXT_PREFIX_KEY + nonce) != null;
    }

    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
        Supplier<SecurityContext> supplier = () -> this.readSecurityContextFromRedis(request);
        return new SupplierDeferredSecurityContext(supplier, this.securityContextHolderStrategy);
    }

    private SecurityContext readSecurityContextFromRedis(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String nonce = request.getHeader(NONCE_HEADER_NAME);
        if (ObjectUtils.isEmpty((Object)nonce) && ObjectUtils.isEmpty((Object)(nonce = request.getParameter(NONCE_HEADER_NAME)))) {
            return null;
        }
        Object o = this.redisRepository.get(SECURITY_CONTEXT_PREFIX_KEY + nonce);
        if (o instanceof SecurityContext) {
            SecurityContext context = (SecurityContext)o;
            return context;
        }
        return null;
    }
}

