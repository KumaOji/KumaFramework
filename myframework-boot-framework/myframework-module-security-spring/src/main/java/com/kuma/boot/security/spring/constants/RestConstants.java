/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.constants;

public interface RestConstants
extends BaseConstants {
    public static final String PROPERTY_FEIGN_OKHTTP = "feign.okhttp";
    public static final String PROPERTY_FEIGN_HTTPCLIENT = "feign.httpclient";
    public static final String PROPERTY_REST_SCAN = "ttc.rest.scan";
    public static final String ITEM_SCAN_ENABLED = "ttc.rest.scan.enabled";
    public static final String ITEM_FEIGN_OKHTTP_ENABLED = "feign.okhttp.enabled";
    public static final String ITEM_FEIGN_HTTPCLIENT_ENABLED = "feign.httpclient.enabled";
    public static final String ITEM_PROTECT_CRYPTO_STRATEGY = "ttc.crypto.crypto-strategy";
    public static final String CACHE_NAME_TOKEN_IDEMPOTENT = "cache:token:idempotent:";
    public static final String CACHE_NAME_TOKEN_ACCESS_LIMITED = "cache:token:access_limited:";
    public static final String CACHE_NAME_TOKEN_SECURE_KEY = "cache:token:secure_key:";
}

