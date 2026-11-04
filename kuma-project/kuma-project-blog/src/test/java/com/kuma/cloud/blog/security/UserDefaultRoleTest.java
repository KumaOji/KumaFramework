package com.kuma.cloud.blog.security;

import com.kuma.cloud.blog.domain.entity.User;
import com.kuma.cloud.blog.mapper.UserMapper;
import com.kuma.cloud.blog.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserDefaultRoleTest {
    @Test
    void newUsersReceiveTheCorrespondingDatabaseRoleWithoutDirectGrants() {
        var mapper = mock(UserMapper.class);
        var jdbc = mock(JdbcTemplate.class);
        var service = new UserServiceImpl(mapper, mock(TotpAttemptLimiter.class), jdbc);
        doAnswer(invocation -> {
            ((User) invocation.getArgument(0)).setId(10L);
            return 1;
        }).when(mapper).insert(any(User.class));

        var ordinary = new User();
        assertThat(service.createUser(ordinary)).isEqualTo(10L);
        assertThat(ordinary.getIsAdmin()).isZero();
        verify(jdbc).update(contains("INSERT IGNORE INTO sys_user_role"), eq(10L), eq("ROLE_USER"));

        var admin = new User();
        admin.setIsAdmin(1);
        service.createUser(admin);
        verify(jdbc).update(contains("INSERT IGNORE INTO sys_user_role"), eq(10L), eq("ROLE_ADMIN"));
        verifyNoMoreInteractions(jdbc);
    }
}
