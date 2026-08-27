package com.kuma.cloud.uaa.service.impl;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.cloud.uaa.config.UaaProperties;
import com.kuma.cloud.uaa.domain.dto.ClientSaveDTO;
import com.kuma.cloud.uaa.domain.vo.ClientVO;
import com.kuma.cloud.uaa.service.UaaClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.kuma.boot.security.spring.utils.OAuth2AuthorizationUtils;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UaaClientServiceImpl implements UaaClientService {

    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final UaaProperties properties;

    /**
     * RegisteredClientRepository 只定义了按 id / clientId 的单条查询与保存，
     * 列表与删除需要直接落到 SAS 的客户端表上。
     */
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<ClientVO> listAll() {
        List<String> clientIds = jdbcTemplate.queryForList(
                "SELECT client_id FROM oauth2_registered_client ORDER BY client_id", String.class);
        List<ClientVO> result = new ArrayList<>(clientIds.size());
        for (String clientId : clientIds) {
            RegisteredClient client = registeredClientRepository.findByClientId(clientId);
            if (client != null) {
                result.add(toVO(client));
            }
        }
        return result;
    }

    @Override
    public ClientVO getByClientId(String clientId) {
        return toVO(requireClient(clientId));
    }

    @Override
    public void save(ClientSaveDTO dto) {
        RegisteredClient existing = registeredClientRepository.findByClientId(dto.getClientId());
        ClientAuthenticationMethod authenticationMethod =
                OAuth2AuthorizationUtils.resolveClientAuthenticationMethod(dto.getAuthenticationMethod());
        boolean publicClient = ClientAuthenticationMethod.NONE.equals(authenticationMethod);

        if (existing == null && !publicClient && !StringUtils.hasText(dto.getClientSecret())) {
            throw new BusinessException("机密客户端必须设置密钥");
        }

        RegisteredClient.Builder builder = existing == null
                ? RegisteredClient.withId(UUID.randomUUID().toString())
                : RegisteredClient.from(existing);

        builder.clientId(dto.getClientId())
                .clientName(dto.getClientName())
                .clientAuthenticationMethods(methods -> {
                    methods.clear();
                    methods.add(authenticationMethod);
                })
                .authorizationGrantTypes(grantTypes -> {
                    grantTypes.clear();
                    for (String grantType : dto.getGrantTypes()) {
                        grantTypes.add(OAuth2AuthorizationUtils.resolveAuthorizationGrantType(grantType));
                    }
                })
                .redirectUris(uris -> {
                    uris.clear();
                    if (dto.getRedirectUris() != null) {
                        uris.addAll(dto.getRedirectUris());
                    }
                })
                .postLogoutRedirectUris(uris -> {
                    uris.clear();
                    if (dto.getPostLogoutRedirectUris() != null) {
                        uris.addAll(dto.getPostLogoutRedirectUris());
                    }
                })
                .scopes(scopes -> {
                    scopes.clear();
                    scopes.addAll(dto.getScopes());
                })
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(dto.isRequireAuthorizationConsent())
                        .requireProofKey(dto.isRequireProofKey())
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        .accessTokenTimeToLive(properties.getToken().getAccessTokenTtl())
                        .refreshTokenTimeToLive(properties.getToken().getRefreshTokenTtl())
                        .authorizationCodeTimeToLive(properties.getToken().getAuthorizationCodeTtl())
                        .reuseRefreshTokens(properties.getToken().isReuseRefreshTokens())
                        .build());

        if (StringUtils.hasText(dto.getClientSecret())) {
            builder.clientSecret(passwordEncoder.encode(dto.getClientSecret()));
        } else if (publicClient) {
            builder.clientSecret(null);
        }

        registeredClientRepository.save(builder.build());
        log.info("OAuth2 客户端已保存: clientId={}, 新建={}", dto.getClientId(), existing == null);
    }

    @Override
    public void delete(String clientId) {
        RegisteredClient client = requireClient(clientId);
        jdbcTemplate.update("DELETE FROM oauth2_authorization WHERE registered_client_id = ?", client.getId());
        jdbcTemplate.update(
                "DELETE FROM oauth2_authorization_consent WHERE registered_client_id = ?", client.getId());
        jdbcTemplate.update("DELETE FROM oauth2_registered_client WHERE id = ?", client.getId());
        log.info("OAuth2 客户端已删除: clientId={}", clientId);
    }

    private RegisteredClient requireClient(String clientId) {
        RegisteredClient client = registeredClientRepository.findByClientId(clientId);
        if (client == null) {
            throw new BusinessException("客户端不存在: " + clientId);
        }
        return client;
    }

    private ClientVO toVO(RegisteredClient client) {
        ClientVO vo = new ClientVO();
        vo.setId(client.getId());
        vo.setClientId(client.getClientId());
        vo.setClientName(client.getClientName());
        vo.setClientIdIssuedAt(client.getClientIdIssuedAt());

        Set<String> grantTypes = new LinkedHashSet<>();
        client.getAuthorizationGrantTypes().forEach(grantType -> grantTypes.add(grantType.getValue()));
        vo.setGrantTypes(grantTypes);

        Set<String> methods = new LinkedHashSet<>();
        client.getClientAuthenticationMethods().forEach(method -> methods.add(method.getValue()));
        vo.setAuthenticationMethods(methods);

        vo.setRedirectUris(new LinkedHashSet<>(client.getRedirectUris()));
        vo.setPostLogoutRedirectUris(new LinkedHashSet<>(client.getPostLogoutRedirectUris()));
        vo.setScopes(new LinkedHashSet<>(client.getScopes()));
        vo.setRequireAuthorizationConsent(client.getClientSettings().isRequireAuthorizationConsent());
        vo.setRequireProofKey(client.getClientSettings().isRequireProofKey());
        vo.setAccessTokenTtlSeconds(client.getTokenSettings().getAccessTokenTimeToLive().toSeconds());
        vo.setRefreshTokenTtlSeconds(client.getTokenSettings().getRefreshTokenTimeToLive().toSeconds());
        return vo;
    }
}
