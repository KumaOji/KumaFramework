package com.kuma.cloud.uaa.support;

import com.kuma.cloud.uaa.config.UaaProperties;
import com.kuma.cloud.uaa.domain.dto.ClientSaveDTO;
import com.kuma.cloud.uaa.domain.dto.UserSaveDTO;
import com.kuma.cloud.uaa.service.UaaClientService;
import com.kuma.cloud.uaa.service.UaaUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 启动时按配置幂等初始化管理员账号与接入客户端。
 *
 * <p>这两类数据无法用纯 SQL 落库：管理员密码需经 PasswordEncoder 编码，
 * 客户端的 client_settings / token_settings 是 Spring Authorization Server 的内部 JSON 结构，
 * 都必须走 Java 侧的正规写入路径。
 *
 * @author kuma
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UaaDataInitializer implements ApplicationRunner {

    private final UaaProperties properties;
    private final UaaUserService userService;
    private final UaaClientService clientService;
    private final RegisteredClientRepository registeredClientRepository;
    private final AtomicBoolean clientsChanged = new AtomicBoolean();
    private final Map<String, String> configuredSecrets = new HashMap<>();

    @Override
    public void run(ApplicationArguments args) {
        initAdmin();
        initClients(false);
    }

    private void initAdmin() {
        UaaProperties.Admin admin = properties.getAdmin();
        if (!admin.isInitEnabled()) {
            return;
        }
        if (userService.getByUsername(admin.getUsername()) != null) {
            return;
        }

        UserSaveDTO dto = new UserSaveDTO();
        dto.setUsername(admin.getUsername());
        dto.setPassword(admin.getPassword());
        dto.setNickname(admin.getNickname());
        dto.setEmail(admin.getEmail());
        dto.setRoleCodes(Set.of("ADMIN"));
        userService.create(dto);

        log.warn(
                "已创建初始管理员账号 {}，请立即登录并修改密码（kuma.uaa.admin.password）",
                admin.getUsername());
    }

    @EventListener
    public void configurationChanged(EnvironmentChangeEvent event) {
        if (event.getKeys().stream().anyMatch(key -> key.startsWith("kuma.uaa.clients"))) {
            clientsChanged.set(true);
        }
    }

    @EventListener(RefreshScopeRefreshedEvent.class)
    public void refreshClients() {
        if (clientsChanged.getAndSet(false)) {
            initClients(true);
        }
    }

    private synchronized void initClients(boolean refresh) {
        for (UaaProperties.Client client : properties.getClients()) {
            boolean exists =
                    registeredClientRepository.findByClientId(client.getClientId()) != null;
            ClientSaveDTO dto = new ClientSaveDTO();
            dto.setClientId(client.getClientId());
            // Preserve API-managed secrets unless the configured secret itself has changed.
            if (!exists || (refresh && !Objects.equals(
                    configuredSecrets.get(client.getClientId()), client.getClientSecret()))) {
                dto.setClientSecret(client.getClientSecret());
            }
            dto.setClientName(
                    client.getClientName() == null ? client.getClientId() : client.getClientName());
            dto.setGrantTypes(client.getGrantTypes());
            dto.setAuthenticationMethod(client.getAuthenticationMethod());
            dto.setRedirectUris(client.getRedirectUris());
            dto.setPostLogoutRedirectUris(client.getPostLogoutRedirectUris());
            dto.setScopes(client.getScopes());
            dto.setRequireAuthorizationConsent(client.isRequireAuthorizationConsent());
            dto.setRequireProofKey(client.isRequireProofKey());
            clientService.save(dto);
            configuredSecrets.put(client.getClientId(), client.getClientSecret());
            log.info("OAuth2 客户端已同步: clientId={}, 新建={}", client.getClientId(), !exists);
        }
    }
}
