/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.security.justauth.justauth;

import java.io.Serializable;

/**
 * A data transfer object that allows the internal state of a Connection to be persisted and transferred between layers of an application.
 * Some fields may be null .
 * For example, an OAuth2Connection has a null 'secret' field while an OAuth1Connection has null 'refreshToken' and 'expireTime' fields.
 * @author Keith Donald
 * @author YongWu zheng
 */
public class ConnectionData implements Serializable {

    /**
     * 本地用户id
     */
    private String userId;

    /**
     * 第三方服务商
     */
    private String providerId;

    /**
     * 第三方用户id
     */
    private String providerUserId;

    /**
     * userId 绑定同一个 providerId 的排序
     */
    private Integer rank;

    /**
     * 第三方用户名
     */
    private String displayName;

    /**
     * 主页
     */
    private String profileUrl;

    /**
     * 头像
     */
    private String imageUrl;

    /**
     * accessToken
     */
    private String accessToken;

    /**
     * auth_token.id
     */
    private Long tokenId;

    /**
     * refreshToken
     */
    private String refreshToken;

    /**
     * 过期日期, 基于 1970-01-01T00:00:00Z, 无过期时间默认为 -1
     */
    private Long expireTime;

    /**
     * 用户唯一 ID
     * @return userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * The id of the provider the connection is associated with.
     * @return The id of the provider the connection is associated with.
     */
    public String getProviderId() {
        return providerId;
    }

    /**
     * The id of the provider user this connection is connected to.
     * @return The id of the provider user this connection is connected to.
     */
    public String getProviderUserId() {
        return providerUserId;
    }

    /**
     * userId 绑定同一个 providerId 的排序
     * @return  rank
     */
    @SuppressWarnings("unused")
    public Integer getRank() {
        return rank;
    }

    /**
     * A display name for the connection.
     * @return A display name for the connection.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * A link to the provider's user profile page.
     * @return A link to the provider's user profile page.
     */
    public String getProfileUrl() {
        return profileUrl;
    }

    /**
     * An image visualizing the connection.
     * @return An image visualizing the connection.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * The access token required to make authorized API calls.
     * @return The access token required to make authorized API calls.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * The secret token needed to make authorized API calls.
     * Required for OAuth1-based connections.
     * @return The secret token needed to make authorized API calls.
     */
    public Long getTokenId() {
        return tokenId;
    }

    /**
     * A token use to renew this connection. Optional.
     * Always null for OAuth1-based connections.
     * @return A token use to renew this connection. Optional.
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * The time the connection expires. Optional.
     * Always null for OAuth1-based connections.
     * @return The time the connection expires. Optional.
     */
    public Long getExpireTime() {
        return expireTime;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public static ConnectionDataBuilder builder() {
        return new ConnectionDataBuilder();
    }

    public static final class ConnectionDataBuilder {
        private ConnectionData connectionData;

        private ConnectionDataBuilder() {
            connectionData = new ConnectionData();
        }

        public ConnectionDataBuilder userId(String userId) {
            connectionData.setUserId(userId);
            return this;
        }

        public ConnectionDataBuilder providerId(String providerId) {
            connectionData.setProviderId(providerId);
            return this;
        }

        public ConnectionDataBuilder providerUserId(String providerUserId) {
            connectionData.setProviderUserId(providerUserId);
            return this;
        }

        public ConnectionDataBuilder rank(Integer rank) {
            connectionData.setRank(rank);
            return this;
        }

        public ConnectionDataBuilder displayName(String displayName) {
            connectionData.setDisplayName(displayName);
            return this;
        }

        public ConnectionDataBuilder profileUrl(String profileUrl) {
            connectionData.setProfileUrl(profileUrl);
            return this;
        }

        public ConnectionDataBuilder imageUrl(String imageUrl) {
            connectionData.setImageUrl(imageUrl);
            return this;
        }

        public ConnectionDataBuilder accessToken(String accessToken) {
            connectionData.setAccessToken(accessToken);
            return this;
        }

        public ConnectionDataBuilder tokenId(Long tokenId) {
            connectionData.setTokenId(tokenId);
            return this;
        }

        public ConnectionDataBuilder refreshToken(String refreshToken) {
            connectionData.setRefreshToken(refreshToken);
            return this;
        }

        public ConnectionDataBuilder expireTime(Long expireTime) {
            connectionData.setExpireTime(expireTime);
            return this;
        }

        public ConnectionData build() {
            return connectionData;
        }
    }
}
