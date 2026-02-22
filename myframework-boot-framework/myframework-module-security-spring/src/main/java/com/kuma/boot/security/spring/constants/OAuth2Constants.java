/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.constants;

public interface OAuth2Constants
extends BaseConstants {
    public static final String PROPERTY_OAUTH2_AUTHENTICATION = "ttc.oauth2.authentication";
    public static final String PROPERTY_OAUTH2_AUTHORIZATION = "ttc.oauth2.authorization";
    public static final String ITEM_COMPLIANCE_AUTO_UNLOCK = "ttc.oauth2.authentication.auto-unlock";
    public static final String REGION_OAUTH2_AUTHORIZATION = "data:oauth2:authorization";
    public static final String REGION_OAUTH2_AUTHORIZATION_CONSENT = "data:oauth2:authorization:consent";
    public static final String REGION_OAUTH2_REGISTERED_CLIENT = "data:oauth2:registered:client";
    public static final String REGION_OAUTH2_APPLICATION = "data:oauth2:application";
    public static final String REGION_OAUTH2_COMPLIANCE = "data:oauth2:compliance";
    public static final String REGION_OAUTH2_PERMISSION = "data:oauth2:permission";
    public static final String REGION_OAUTH2_SCOPE = "data:oauth2:scope";
    public static final String REGION_OAUTH2_APPLICATION_SCOPE = "data:oauth2:application:scope";
    public static final String REGION_OAUTH2_IOT_PRODUCT = "data:oauth2:iot:product";
    public static final String REGION_OAUTH2_IOT_DEVICE = "data:oauth2:iot:device";
    public static final String CACHE_NAME_TOKEN_SIGN_IN_FAILURE_LIMITED = "cache:token:sign_in:failure_limited:";
    public static final String CACHE_NAME_TOKEN_LOCKED_USER_DETAIL = "cache:token:locked:user_details:";
    public static final String CACHE_SECURITY_PREFIX = "cache:security:";
    public static final String CACHE_SECURITY_METADATA_PREFIX = "cache:security:metadata:";
    public static final String CACHE_NAME_SECURITY_METADATA_ATTRIBUTES = "cache:security:metadata:attributes:";
    public static final String CACHE_NAME_SECURITY_METADATA_INDEXES = "cache:security:metadata:indexes:";
    public static final String CACHE_NAME_SECURITY_METADATA_INDEXABLE = "cache:security:metadata:indexable:";
    public static final String CACHE_NAME_SECURITY_METADATA_COMPATIBLE = "cache:security:metadata:compatible:";
}

