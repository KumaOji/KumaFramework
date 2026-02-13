/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.constant;

public final class SecurityConstants {
    public static final String OAUTH_TOKEN_URL = "/oauth/token/user";
    public static final String AUTHORIZED = "Authorized";
    public static final String BASE_AUTHORIZED = "BaseAuthorized";
    public static final String PHONE_KEY = "phone";
    public static final String SMS_CODE = "smsCode";
    public static final String CLIENT_FIELDS = "client_id, client_secret, resource_ids, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove";
    public static final String BASE_FIND = "select client_id, client_secret, resource_ids, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove from oauth_client_details";
    public static final String DEFAULT_FIND = "select client_id, client_secret, resource_ids, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove from oauth_client_details order by client_id";
    public static final String DEFAULT_SELECT = "select client_id, client_secret, resource_ids, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove from oauth_client_details where client_id = ?";
    public static final String BASE_ROLE = "ROLE_USER";
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String PASSWORD = "password";
    public static final String DEFAULT_IMAGE_WIDTH = "100";
    public static final String DEFAULT_IMAGE_HEIGHT = "35";
    public static final String DEFAULT_IMAGE_LENGTH = "4";
    public static final int DEFAULT_IMAGE_EXPIRE = 12000000;
    public static final String DEFAULT_COLOR_FONT = "blue";
    public static final String DEFAULT_IMAGE_BORDER = "no";
    public static final String DEFAULT_CHAR_SPACE = "5";
    public static final String DEFAULT_SMS_CODE_KEY = "default_sms_code_key";
    public static final String DEFAULT_IMAGE_FONT_SIZE = "30";
    public static final String CACHE_CLIENT_KEY = "oauth_client_details";
    public static final Integer ACCESS_TOKEN_VALIDITY_SECONDS = 216000;
    public static final String REDIS_TOKEN_AUTH = "auth:";
    public static final String REDIS_CLIENT_ID_TO_ACCESS = "client_id_to_access:";
    public static final String REDIS_UNAME_TO_ACCESS = "uname_to_access:";

    private SecurityConstants() {
    }
}

