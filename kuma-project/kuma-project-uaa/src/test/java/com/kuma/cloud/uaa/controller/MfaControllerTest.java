package com.kuma.cloud.uaa.controller;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.cloud.uaa.config.UaaProperties;
import com.kuma.cloud.uaa.domain.entity.UaaUser;
import com.kuma.cloud.uaa.service.MfaService;
import com.kuma.cloud.uaa.service.UaaUserService;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MfaControllerTest {
    @Test
    void reportsOnlyTheAuthenticatedUsersBindingWithoutExposingSecrets() {
        var users = mock(UaaUserService.class);
        var redis = mock(RedisRepository.class);
        var properties = new UaaProperties();
        properties.getMfa().setEnabled(true);
        var user = new UaaUser();
        user.setMfaStatus(0);
        user.setMfaSecret("must-not-be-returned");
        when(users.requireByUsername("alice")).thenReturn(user);
        when(redis.get("uaa:mfa:pending:alice")).thenReturn("pending-secret");
        var controller = new MfaController(mock(MfaService.class), users, properties, redis);
        var data = controller.status(() -> "alice").getData();
        assertThat(data).containsEntry("featureEnabled", true).containsEntry("enabled", false)
                .containsEntry("secretBound", true).hasSize(3);
        verify(users).requireByUsername("alice");
        verify(redis).get("uaa:mfa:pending:alice");
    }
}
