/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.configuration.properties;

import com.google.common.base.MoreObjects;
import com.kuma.boot.security.spring.constants.DefaultConstants;
import com.kuma.boot.security.spring.utils.WellFormedUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>平台端点属性
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:10:02
 */
@ConfigurationProperties(prefix = OAuth2EndpointProperties.PREFIX)
public class OAuth2EndpointProperties {

    public static final String PREFIX = "kuma.boot.security.oauth2.endpoint";

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(OAuth2EndpointProperties.class);

    /**
     * 认证中心服务名称
     */
    private String uaaServiceName;

    /**
     * 用户中心服务名称
     */
    private String upmsServiceName;

    /**
     * 对象存储服务名称
     */
    private String ossServiceName;

    /**
     * 统一网关服务地址。可以是IP+端口，可以是域名
     */
    private String gatewayServiceUri;

    /**
     * 统一认证中心服务地址
     */
    private String uaaServiceUri;

    /**
     * 统一权限管理服务地址
     */
    private String upmsServiceUri;

    /**
     * 对象存储服务地址
     */
    private String ossServiceUri;

    /**
     * OAuth2 Authorization Code 模式认证端点，/oauth2/authorize uri 地址，可修改为自定义地址
     */
    private String authorizationUri;

    /**
     * OAuth2 Authorization Code 模式认证端点，/oauth2/authorize端点地址，可修改为自定义地址
     */
    private String authorizationEndpoint = DefaultConstants.AUTHORIZATION_ENDPOINT;

    /**
     * OAuth2 /oauth2/token 申请 Token uri 地址，可修改为自定义地址
     */
    private String accessTokenUri;

    /**
     * OAuth2 /oauth2/token 申请 Token 端点地址，可修改为自定义地址
     */
    private String accessTokenEndpoint = DefaultConstants.TOKEN_ENDPOINT;

    /**
     * OAuth2 /oauth2/jwks uri 地址，可修改为自定义地址
     */
    private String jwkSetUri;

    /**
     * OAuth2 /oauth2/jwks 端点地址，可修改为自定义地址
     */
    private String jwkSetEndpoint = DefaultConstants.JWK_SET_ENDPOINT;

    /**
     * OAuth2 /oauth2/revoke 撤销 Token uri 地址，可修改为自定义地址
     */
    private String tokenRevocationUri;

    /**
     * OAuth2 /oauth2/revoke 撤销 Token 端点地址，可修改为自定义地址
     */
    private String tokenRevocationEndpoint = DefaultConstants.TOKEN_REVOCATION_ENDPOINT;

    /**
     * OAuth2 /oauth2/introspect 查看 Token uri地址，可修改为自定义地址
     */
    private String tokenIntrospectionUri;

    /**
     * OAuth2 /oauth2/introspect 查看 Token 端点地址，可修改为自定义地址
     */
    private String tokenIntrospectionEndpoint = DefaultConstants.TOKEN_INTROSPECTION_ENDPOINT;

    /**
     * OAuth2 /oauth2/device_authorization 设备授权认证 uri地址，可修改为自定义地址
     */
    private String deviceAuthorizationUri;

    /**
     * OAuth2 /oauth2/device_authorization 设备授权认证端点地址，可修改为自定义地址
     */
    private String deviceAuthorizationEndpoint = DefaultConstants.DEVICE_AUTHORIZATION_ENDPOINT;

    /**
     * OAuth2 /oauth2/device_verification 设备授权校验 uri地址，可修改为自定义地址
     */
    private String deviceVerificationUri;

    /**
     * OAuth2 /oauth2/device_verification 设备授权校验端点地址，可修改为自定义地址
     */
    private String deviceVerificationEndpoint = DefaultConstants.DEVICE_VERIFICATION_ENDPOINT;

    /**
     * OAuth2 OIDC /connect/register uri 地址，可修改为自定义地址
     */
    private String oidcClientRegistrationUri;

    /**
     * OAuth2 OIDC /connect/register 端点地址，可修改为自定义地址
     */
    private String oidcClientRegistrationEndpoint =
            DefaultConstants.OIDC_CLIENT_REGISTRATION_ENDPOINT;

    /**
     * OAuth2 OIDC /connect/logout uri 地址，可修改为自定义地址
     */
    private String oidcLogoutUri;

    /**
     * OAuth2 OIDC /connect/logout 端点地址，可修改为自定义地址
     */
    private String oidcLogoutEndpoint = DefaultConstants.OIDC_LOGOUT_ENDPOINT;

    /**
     * OAuth2 OIDC /userinfo uri 地址，可修改为自定义地址
     */
    private String oidcUserInfoUri;

    /**
     * OAuth2 OIDC /userinfo 端点地址，可修改为自定义地址
     */
    private String oidcUserInfoEndpoint = DefaultConstants.OIDC_USER_INFO_ENDPOINT;

    /**
     * Spring Authorization Server Issuer Url
     */
    private String issuerUri;

    /**
     * 把默认端点
     *
     * @param endpoint                  端点
     * @param pathAuthorizationEndpoint 授权路径端点
     * @return {@link String }
     * @since 2023-07-04 10:10:02
     */
    private String getDefaultEndpoint(String endpoint, String pathAuthorizationEndpoint) {
        if (StringUtils.isNotBlank(endpoint)) {
            return endpoint;
        } else {
            if (StringUtils.isNotBlank(pathAuthorizationEndpoint)) {
                return getUaaServiceUri() + pathAuthorizationEndpoint;
            } else {
                return getUaaServiceUri();
            }
        }
    }

    /**
     * 得到uaa服务名称
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:02
     */
    public String getUaaServiceName() {
        return uaaServiceName;
    }

    /**
     * 设置uaa服务名称
     *
     * @param uaaServiceName uaa服务名称
     * @since 2023-07-04 10:10:02
     */
    public void setUaaServiceName(String uaaServiceName) {
        this.uaaServiceName = uaaServiceName;
    }

    /**
     * 得到其服务名称
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:02
     */
    public String getUpmsServiceName() {
        return upmsServiceName;
    }

    /**
     * 得到oss服务名称
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:03
     */
    public String getOssServiceName() {
        return ossServiceName;
    }

    /**
     * 设置oss服务名称
     *
     * @param ossServiceName oss服务名称
     * @since 2023-07-04 10:10:03
     */
    public void setOssServiceName(String ossServiceName) {
        this.ossServiceName = ossServiceName;
    }

    /**
     * 设置其服务名称
     *
     * @param upmsServiceName 其服务名称
     * @since 2023-07-04 10:10:03
     */
    public void setUpmsServiceName(String upmsServiceName) {
        this.upmsServiceName = upmsServiceName;
    }

    /**
     * 获得网关服务uri
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:03
     */
    public String getGatewayServiceUri() {
        return gatewayServiceUri;
    }

    /**
     * 设置网关服务uri
     *
     * @param gatewayServiceUri 网关服务uri
     * @since 2023-07-04 10:10:03
     */
    public void setGatewayServiceUri(String gatewayServiceUri) {
        this.gatewayServiceUri = gatewayServiceUri;
    }

    /**
     * 得到uaa服务uri
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:03
     */
    public String getUaaServiceUri() {
        return WellFormedUtils.serviceUri(
                getGatewayServiceUri(), uaaServiceUri, uaaServiceName, "UAA");
    }

    /**
     * 设置uaa服务uri
     *
     * @param uaaServiceUri uaa服务uri
     * @since 2023-07-04 10:10:03
     */
    public void setUaaServiceUri(String uaaServiceUri) {
        this.uaaServiceUri = uaaServiceUri;
    }

    /**
     * 得到oss服务uri
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:04
     */
    public String getOssServiceUri() {
        return WellFormedUtils.serviceUri(
                getGatewayServiceUri(), ossServiceUri, ossServiceName, "OSS");
    }

    /**
     * 设置oss服务uri
     *
     * @param ossServiceUri oss服务uri
     * @since 2023-07-04 10:10:04
     */
    public void setOssServiceUri(String ossServiceUri) {
        this.ossServiceUri = ossServiceUri;
    }

    /**
     * 得到其服务uri
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:04
     */
    public String getUpmsServiceUri() {
        return WellFormedUtils.serviceUri(
                getGatewayServiceUri(), upmsServiceUri, upmsServiceName, "UPMS");
    }

    /**
     * 设置其服务uri
     *
     * @param upmsServiceUri 其服务uri
     * @since 2023-07-04 10:10:04
     */
    public void setUpmsServiceUri(String upmsServiceUri) {
        this.upmsServiceUri = upmsServiceUri;
    }

    /**
     * 获得授权uri
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:04
     */
    public String getAuthorizationUri() {
        return getDefaultEndpoint(authorizationUri, getAuthorizationEndpoint());
    }

    /**
     * 设置授权uri
     *
     * @param authorizationUri 授权uri
     * @since 2023-07-04 10:10:04
     */
    public void setAuthorizationUri(String authorizationUri) {
        this.authorizationUri = authorizationUri;
    }

    /**
     * uri获取访问令牌
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:05
     */
    public String getAccessTokenUri() {
        return getDefaultEndpoint(accessTokenUri, getAccessTokenEndpoint());
    }

    /**
     * 将uri访问令牌
     *
     * @param accessTokenUri uri访问令牌
     * @since 2023-07-04 10:10:05
     */
    public void setAccessTokenUri(String accessTokenUri) {
        this.accessTokenUri = accessTokenUri;
    }

    /**
     * 获取jwk设置uri
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:05
     */
    public String getJwkSetUri() {
        return getDefaultEndpoint(jwkSetUri, getJwkSetEndpoint());
    }

    /**
     * jwk集合uri
     *
     * @param jwkSetUri jwk集合uri
     * @since 2023-07-04 10:10:05
     */
    public void setJwkSetUri(String jwkSetUri) {
        this.jwkSetUri = jwkSetUri;
    }

    /**
     * 获得令牌撤销uri
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:05
     */
    public String getTokenRevocationUri() {
        return getDefaultEndpoint(tokenRevocationUri, getTokenRevocationEndpoint());
    }

    /**
     * 设置令牌撤销uri
     *
     * @param tokenRevocationUri 令牌撤销uri
     * @since 2023-07-04 10:10:05
     */
    public void setTokenRevocationUri(String tokenRevocationUri) {
        this.tokenRevocationUri = tokenRevocationUri;
    }

    /**
     * 获得令牌内省uri
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:06
     */
    public String getTokenIntrospectionUri() {
        return getDefaultEndpoint(tokenIntrospectionUri, getTokenIntrospectionEndpoint());
    }

    /**
     * 设置令牌内省uri
     *
     * @param tokenIntrospectionUri 令牌内省uri
     * @since 2023-07-04 10:10:06
     */
    public void setTokenIntrospectionUri(String tokenIntrospectionUri) {
        this.tokenIntrospectionUri = tokenIntrospectionUri;
    }

    /**
     * 得到设备授权uri
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:06
     */
    public String getDeviceAuthorizationUri() {
        return getDefaultEndpoint(deviceAuthorizationUri, getDeviceAuthorizationEndpoint());
    }

    /**
     * 设置设备授权uri
     *
     * @param deviceAuthorizationUri 设备授权uri
     * @since 2023-07-04 10:10:06
     */
    public void setDeviceAuthorizationUri(String deviceAuthorizationUri) {
        this.deviceAuthorizationUri = deviceAuthorizationUri;
    }

    /**
     * 得到设备验证uri
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:06
     */
    public String getDeviceVerificationUri() {
        return getDefaultEndpoint(deviceVerificationUri, getDeviceVerificationEndpoint());
    }

    /**
     * 设置设备验证uri
     *
     * @param deviceVerificationUri 设备验证uri
     * @since 2023-07-04 10:10:06
     */
    public void setDeviceVerificationUri(String deviceVerificationUri) {
        this.deviceVerificationUri = deviceVerificationUri;
    }

    /**
     * 得到oidc客户登记uri
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:07
     */
    public String getOidcClientRegistrationUri() {
        return getDefaultEndpoint(oidcClientRegistrationUri, getOidcClientRegistrationEndpoint());
    }

    /**
     * 设置oidc客户登记uri
     *
     * @param oidcClientRegistrationUri uri oidc客户登记
     * @since 2023-07-04 10:10:07
     */
    public void setOidcClientRegistrationUri(String oidcClientRegistrationUri) {
        this.oidcClientRegistrationUri = oidcClientRegistrationUri;
    }

    /**
     * 得到oidc注销uri
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:07
     */
    public String getOidcLogoutUri() {
        return getDefaultEndpoint(oidcLogoutUri, getOidcLogoutEndpoint());
    }

    /**
     * 设置oidc注销uri
     *
     * @param oidcLogoutUri oidc注销uri
     * @since 2023-07-04 10:10:07
     */
    public void setOidcLogoutUri(String oidcLogoutUri) {
        this.oidcLogoutUri = oidcLogoutUri;
    }

    /**
     * 得到oidc用户信息uri
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:07
     */
    public String getOidcUserInfoUri() {
        return getDefaultEndpoint(oidcUserInfoUri, getOidcUserInfoEndpoint());
    }

    /**
     * 设置oidc用户信息uri
     *
     * @param oidcUserInfoUri uri oidc用户信息
     * @since 2023-07-04 10:10:08
     */
    public void setOidcUserInfoUri(String oidcUserInfoUri) {
        this.oidcUserInfoUri = oidcUserInfoUri;
    }

    /**
     * 获得发行人uri
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:08
     */
    public String getIssuerUri() {
        return this.issuerUri;
    }

    /**
     * 设置发行人uri
     *
     * @param issuerUri 发行人uri
     * @since 2023-07-04 10:10:08
     */
    public void setIssuerUri(String issuerUri) {
        this.issuerUri = issuerUri;
    }

    /**
     * 获得授权端点
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:08
     */
    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    /**
     * 设置授权端点
     *
     * @param authorizationEndpoint 授权端点
     * @since 2023-07-04 10:10:08
     */
    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    /**
     * 获得访问令牌端点
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:08
     */
    public String getAccessTokenEndpoint() {
        return accessTokenEndpoint;
    }

    /**
     * 访问令牌设置端点
     *
     * @param accessTokenEndpoint 访问令牌端点
     * @since 2023-07-04 10:10:09
     */
    public void setAccessTokenEndpoint(String accessTokenEndpoint) {
        this.accessTokenEndpoint = accessTokenEndpoint;
    }

    /**
     * 获取jwk设置端点
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:09
     */
    public String getJwkSetEndpoint() {
        return jwkSetEndpoint;
    }

    /**
     * 设置jwk设置端点
     *
     * @param jwkSetEndpoint jwk设置端点
     * @since 2023-07-04 10:10:09
     */
    public void setJwkSetEndpoint(String jwkSetEndpoint) {
        this.jwkSetEndpoint = jwkSetEndpoint;
    }

    /**
     * 获得令牌撤销端点
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:09
     */
    public String getTokenRevocationEndpoint() {
        return tokenRevocationEndpoint;
    }

    /**
     * 设置令牌撤销端点
     *
     * @param tokenRevocationEndpoint 令牌撤销端点
     * @since 2023-07-04 10:10:09
     */
    public void setTokenRevocationEndpoint(String tokenRevocationEndpoint) {
        this.tokenRevocationEndpoint = tokenRevocationEndpoint;
    }

    /**
     * 获得令牌内省端点
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:09
     */
    public String getTokenIntrospectionEndpoint() {
        return tokenIntrospectionEndpoint;
    }

    /**
     * 设置令牌内省端点
     *
     * @param tokenIntrospectionEndpoint 令牌内省端点
     * @since 2023-07-04 10:10:09
     */
    public void setTokenIntrospectionEndpoint(String tokenIntrospectionEndpoint) {
        this.tokenIntrospectionEndpoint = tokenIntrospectionEndpoint;
    }

    /**
     * 得到设备授权端点
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:10
     */
    public String getDeviceAuthorizationEndpoint() {
        return deviceAuthorizationEndpoint;
    }

    /**
     * 设置设备授权端点
     *
     * @param deviceAuthorizationEndpoint 设备授权端点
     * @since 2023-07-04 10:10:10
     */
    public void setDeviceAuthorizationEndpoint(String deviceAuthorizationEndpoint) {
        this.deviceAuthorizationEndpoint = deviceAuthorizationEndpoint;
    }

    /**
     * 得到设备验证端点
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:10
     */
    public String getDeviceVerificationEndpoint() {
        return deviceVerificationEndpoint;
    }

    /**
     * 设置设备验证端点
     *
     * @param deviceVerificationEndpoint 设备验证端点
     * @since 2023-07-04 10:10:10
     */
    public void setDeviceVerificationEndpoint(String deviceVerificationEndpoint) {
        this.deviceVerificationEndpoint = deviceVerificationEndpoint;
    }

    /**
     * 得到oidc客户注册端点
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:10
     */
    public String getOidcClientRegistrationEndpoint() {
        return oidcClientRegistrationEndpoint;
    }

    /**
     * 设置oidc客户注册端点
     *
     * @param oidcClientRegistrationEndpoint oidc客户注册端点
     * @since 2023-07-04 10:10:10
     */
    public void setOidcClientRegistrationEndpoint(String oidcClientRegistrationEndpoint) {
        this.oidcClientRegistrationEndpoint = oidcClientRegistrationEndpoint;
    }

    /**
     * 得到oidc用户信息端点
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:11
     */
    public String getOidcUserInfoEndpoint() {
        return oidcUserInfoEndpoint;
    }

    /**
     * 设置oidc用户信息端点
     *
     * @param oidcUserInfoEndpoint oidc端点用户信息
     * @since 2023-07-04 10:10:11
     */
    public void setOidcUserInfoEndpoint(String oidcUserInfoEndpoint) {
        this.oidcUserInfoEndpoint = oidcUserInfoEndpoint;
    }

    /**
     * 得到oidc注销端点
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:11
     */
    public String getOidcLogoutEndpoint() {
        return oidcLogoutEndpoint;
    }

    /**
     * 设置oidc注销端点
     *
     * @param oidcLogoutEndpoint oidc注销端点
     * @since 2023-07-04 10:10:11
     */
    public void setOidcLogoutEndpoint(String oidcLogoutEndpoint) {
        this.oidcLogoutEndpoint = oidcLogoutEndpoint;
    }

    /**
     * 字符串
     *
     * @return {@link String }
     * @since 2023-07-04 10:10:11
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uaaServiceName", uaaServiceName)
                .add("upmsServiceName", upmsServiceName)
                .add("gatewayServiceUri", gatewayServiceUri)
                .add("uaaServiceUri", uaaServiceUri)
                .add("upmsServiceUri", upmsServiceUri)
                .add("authorizationUri", authorizationUri)
                .add("authorizationEndpoint", authorizationEndpoint)
                .add("accessTokenUri", accessTokenUri)
                .add("accessTokenEndpoint", accessTokenEndpoint)
                .add("jwkSetUri", jwkSetUri)
                .add("jwkSetEndpoint", jwkSetEndpoint)
                .add("tokenRevocationUri", tokenRevocationUri)
                .add("tokenRevocationEndpoint", tokenRevocationEndpoint)
                .add("tokenIntrospectionUri", tokenIntrospectionUri)
                .add("tokenIntrospectionEndpoint", tokenIntrospectionEndpoint)
                .add("deviceAuthorizationUri", deviceAuthorizationUri)
                .add("deviceAuthorizationEndpoint", deviceAuthorizationEndpoint)
                .add("deviceVerificationUri", deviceVerificationUri)
                .add("deviceVerificationEndpoint", deviceVerificationEndpoint)
                .add("oidcClientRegistrationUri", oidcClientRegistrationUri)
                .add("oidcClientRegistrationEndpoint", oidcClientRegistrationEndpoint)
                .add("oidcLogoutUri", oidcLogoutUri)
                .add("oidcLogoutEndpoint", oidcLogoutEndpoint)
                .add("oidcUserInfoUri", oidcUserInfoUri)
                .add("oidcUserInfoEndpoint", oidcUserInfoEndpoint)
                .add("issuerUri", issuerUri)
                .toString();
    }
}
