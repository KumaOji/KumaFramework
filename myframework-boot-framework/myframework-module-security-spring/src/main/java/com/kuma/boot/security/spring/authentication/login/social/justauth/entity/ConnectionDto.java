/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.entity;

import java.io.Serializable;

public class ConnectionDto
implements Serializable {
    private static final long serialVersionUID = 620L;
    private String id;
    private String userId;
    private String providerId;
    private String providerUserId;
    private Long tokenId;

    public ConnectionDto() {
    }

    public ConnectionDto(String id, String userId, String providerId, String providerUserId, Long tokenId) {
        this.id = id;
        this.userId = userId;
        this.providerId = providerId;
        this.providerUserId = providerUserId;
        this.tokenId = tokenId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProviderId() {
        return this.providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderUserId() {
        return this.providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public Long getTokenId() {
        return this.tokenId;
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

        private ConnectionDtoBuilder() {
        }

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
            connectionDto.setId(this.id);
            connectionDto.setUserId(this.userId);
            connectionDto.setProviderId(this.providerId);
            connectionDto.setProviderUserId(this.providerUserId);
            connectionDto.setTokenId(this.tokenId);
            return connectionDto;
        }
    }
}

