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

import java.util.Set;

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

    @Override
    public void run(ApplicationArguments args) {
        initAdmin();
        initClients();
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

    private void initClients() {
        for (UaaProperties.Client client : properties.getClients()) {
            if (registeredClientRepository.findByClientId(client.getClientId()) != null) {
                continue;
            }
            ClientSaveDTO dto = new ClientSaveDTO();
            dto.setClientId(client.getClientId());
            dto.setClientSecret(client.getClientSecret());
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
            log.info("已注册 OAuth2 客户端: clientId={}", client.getClientId());
        }
    }
}
