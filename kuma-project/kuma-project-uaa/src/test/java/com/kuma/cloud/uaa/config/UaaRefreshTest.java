package com.kuma.cloud.uaa.config;

import com.kuma.cloud.uaa.support.RefreshingRegisteredClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import com.kuma.cloud.uaa.security.UaaOidcUserInfoMapper;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import jakarta.servlet.Filter;
import java.time.Duration;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UaaRefreshTest {
    @Test
    void onlyClientChangesSynchronizeClientsAndOnlyChangedSecretsAreRotated() {
        var properties = new UaaProperties();
        properties.getAdmin().setInitEnabled(false);
        var client = new UaaProperties.Client();
        client.setClientId("blog");
        client.setClientSecret("initial");
        properties.getClients().add(client);
        var users = mock(com.kuma.cloud.uaa.service.UaaUserService.class);
        var service = mock(com.kuma.cloud.uaa.service.UaaClientService.class);
        var repository = mock(org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository.class);
        org.mockito.Mockito.when(repository.findByClientId("blog")).thenReturn(RegisteredClient.withId("id")
                .clientId("blog").authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS).build());
        var initializer = new com.kuma.cloud.uaa.support.UaaDataInitializer(properties, users, service, repository);
        initializer.run(null);
        var dto = org.mockito.ArgumentCaptor.forClass(com.kuma.cloud.uaa.domain.dto.ClientSaveDTO.class);
        org.mockito.Mockito.verify(service).save(dto.capture());
        assertThat(dto.getValue().getClientSecret()).isNull();
        org.mockito.Mockito.clearInvocations(service);

        initializer.configurationChanged(new EnvironmentChangeEvent(java.util.Set.of("kuma.uaa.issuer")));
        initializer.refreshClients();
        org.mockito.Mockito.verifyNoInteractions(service);

        client.setClientSecret("rotated");
        initializer.configurationChanged(new EnvironmentChangeEvent(java.util.Set.of("kuma.uaa.clients[0].client-secret")));
        initializer.refreshClients();
        org.mockito.Mockito.verify(service).save(dto.capture());
        assertThat(dto.getValue().getClientSecret()).isEqualTo("rotated");
        org.mockito.Mockito.verifyNoInteractions(users);
    }

    @Test
    void authorizationMetadataAndExistingClientsUseRefreshedIssuerAndTokenTtl() {
        new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(RefreshAutoConfiguration.class))
                .withUserConfiguration(Fixtures.class, AuthorizationServerConfig.class)
                .withPropertyValues("kuma.uaa.issuer=https://old.example", "kuma.uaa.token.access-token-ttl=30m")
                .run(context -> {
                    var mvc = MockMvcBuilders.webAppContextSetup(context)
                            .addFilters(context.getBean("springSecurityFilterChain", Filter.class)).build();
                    mvc.perform(get("/.well-known/oauth-authorization-server"))
                            .andExpect(status().isOk()).andExpect(jsonPath("$.issuer").value("https://old.example"));

                    var stored = RegisteredClient.withId("existing")
                            .clientId("client").authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS).build();
                    var clients = new RefreshingRegisteredClientRepository(
                            new InMemoryRegisteredClientRepository(stored), context.getBean(UaaProperties.class));
                    assertThat(clients.findByClientId("client").getTokenSettings().getAccessTokenTimeToLive())
                            .isEqualTo(Duration.ofMinutes(30));

                    var changed = new HashMap<String, Object>();
                    changed.put("kuma.uaa.issuer", "https://new.example");
                    changed.put("kuma.uaa.token.access-token-ttl", "5m");
                    context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("runtime", changed));
                    context.publishEvent(new EnvironmentChangeEvent(changed.keySet()));
                    context.getBean(RefreshScope.class).refreshAll();

                    mvc.perform(get("/.well-known/oauth-authorization-server"))
                            .andExpect(status().isOk()).andExpect(jsonPath("$.issuer").value("https://new.example"));
                    assertThat(clients.findById("existing").getTokenSettings().getAccessTokenTimeToLive())
                            .isEqualTo(Duration.ofMinutes(5));
                    assertThat(clients.findByClientId("missing")).isNull();
                });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableWebSecurity
    @EnableConfigurationProperties(UaaProperties.class)
    static class Fixtures {
        @Bean JwtDecoder jwtDecoder() { return mock(JwtDecoder.class); }
        @Bean JdbcTemplate jdbcTemplate() { return mock(JdbcTemplate.class); }
        @Bean UaaOidcUserInfoMapper userInfoMapper() { return mock(UaaOidcUserInfoMapper.class); }
    }
}
