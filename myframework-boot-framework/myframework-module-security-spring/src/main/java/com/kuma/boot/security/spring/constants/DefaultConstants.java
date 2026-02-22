/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.constants;

public interface DefaultConstants {
    public static final String AUTHORIZATION_ENDPOINT = "/oauth2/authorize";
    public static final String TOKEN_ENDPOINT = "/oauth2/token";
    public static final String TOKEN_REVOCATION_ENDPOINT = "/oauth2/revoke";
    public static final String TOKEN_INTROSPECTION_ENDPOINT = "/oauth2/introspect";
    public static final String DEVICE_AUTHORIZATION_ENDPOINT = "/oauth2/device_authorization";
    public static final String DEVICE_VERIFICATION_ENDPOINT = "/oauth2/device_verification";
    public static final String JWK_SET_ENDPOINT = "/oauth2/jwks";
    public static final String OIDC_CLIENT_REGISTRATION_ENDPOINT = "/connect/register";
    public static final String OIDC_LOGOUT_ENDPOINT = "/connect/logout";
    public static final String OIDC_USER_INFO_ENDPOINT = "/userinfo";
    public static final String AUTHORIZATION_CONSENT_URI = "/oauth2/consent";
    public static final String DEVICE_ACTIVATION_URI = "/oauth2/device_activation";
    public static final String DEVICE_VERIFICATION_SUCCESS_URI = "/device_activated";
    public static final String TENANT_ID = "public";
    public static final String TREE_ROOT_ID = "0";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String MINIO_MULTIPART_REQUEST_MAPPING = "/oss/minio/multipart";
    public static final String MINIO_PRESIGNED_OBJECT_REQUEST_MAPPING = "/presigned";
    public static final String MINIO_PRESIGNED_OBJECT_PROXY = "/presigned/**";
    public static final String PRESIGNED_OBJECT_URL_PROXY = "/oss/minio/multipart/presigned";
}

