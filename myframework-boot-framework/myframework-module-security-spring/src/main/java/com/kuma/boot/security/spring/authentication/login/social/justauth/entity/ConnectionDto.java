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

package com.kuma.boot.security.spring.authentication.login.social.justauth.entity;

import java.io.Serializable;
import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * 查询本地账号下的第三方绑定账号 dto
 * @author YongWu zheng
 * @weixin z56133
 * @since 2021.3.3 20:03
 */
public class ConnectionDto implements Serializable {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    /**
     * 本地主键 id
     */
    private String id;

    /**
     * 本地用户 id
     */
    private String userId;

    /**
     * 第三方服务商 id
     */
    private String providerId;

    /**
     * 第三方用户 id
     */
    private String providerUserId;

    /**
     * 第三方用户 token id
     */
    private Long tokenId;

    public ConnectionDto() {}

    public ConnectionDto(
            String id, String userId, String providerId, String providerUserId, Long tokenId) {
        this.id = id;
        this.userId = userId;
        this.providerId = providerId;
        this.providerUserId = providerUserId;
        this.tokenId = tokenId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public static ConnectionDtoBuilder builder() {
        return new ConnectionDtoBuilder();
    }

    public static final class ConnectionDtoBuilder {

        private String id;
        private String userId;
        private String providerId;
        private String providerUserId;
        private Long tokenId;

        private ConnectionDtoBuilder() {}

        public ConnectionDtoBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ConnectionDtoBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public ConnectionDtoBuilder providerId(String providerId) {
            this.providerId = providerId;
            return this;
        }

        public ConnectionDtoBuilder providerUserId(String providerUserId) {
            this.providerUserId = providerUserId;
            return this;
        }

        public ConnectionDtoBuilder tokenId(Long tokenId) {
            this.tokenId = tokenId;
            return this;
        }

        public ConnectionDto build() {
            ConnectionDto connectionDto = new ConnectionDto();
            connectionDto.setId(id);
            connectionDto.setUserId(userId);
            connectionDto.setProviderId(providerId);
            connectionDto.setProviderUserId(providerUserId);
            connectionDto.setTokenId(tokenId);
            return connectionDto;
        }
    }
}
