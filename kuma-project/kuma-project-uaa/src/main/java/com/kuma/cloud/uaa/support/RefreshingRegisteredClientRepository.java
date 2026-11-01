package com.kuma.cloud.uaa.support;

import com.kuma.cloud.uaa.config.UaaProperties;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

/** Applies current token defaults when a client is read, including clients created through the API. */
public class RefreshingRegisteredClientRepository implements RegisteredClientRepository {
    private final RegisteredClientRepository delegate;
    private final UaaProperties properties;

    public RefreshingRegisteredClientRepository(RegisteredClientRepository delegate, UaaProperties properties) {
        this.delegate = delegate;
        this.properties = properties;
    }

    @Override
    public void save(RegisteredClient client) {
        delegate.save(client);
    }

    @Override
    public RegisteredClient findById(String id) {
        return withCurrentTokenSettings(delegate.findById(id));
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return withCurrentTokenSettings(delegate.findByClientId(clientId));
    }

    private RegisteredClient withCurrentTokenSettings(RegisteredClient client) {
        if (client == null) return null;
        UaaProperties.Token token = properties.getToken();
        return RegisteredClient.from(client)
                .tokenSettings(TokenSettings.withSettings(client.getTokenSettings().getSettings())
                        .accessTokenTimeToLive(token.getAccessTokenTtl())
                        .refreshTokenTimeToLive(token.getRefreshTokenTtl())
                        .authorizationCodeTimeToLive(token.getAuthorizationCodeTtl())
                        .reuseRefreshTokens(token.isReuseRefreshTokens())
                        .build())
                .build();
    }
}
