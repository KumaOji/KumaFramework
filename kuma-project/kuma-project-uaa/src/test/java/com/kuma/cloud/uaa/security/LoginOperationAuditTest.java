package com.kuma.cloud.uaa.security;

import com.kuma.boot.auditlog.core.ServletAuthenticationAuditRecorder;
import com.kuma.boot.auditlog.model.AuditLogEntry;
import com.kuma.cloud.uaa.mapper.UaaLoginLogMapper;
import com.kuma.cloud.uaa.support.LoginAuditService;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginOperationAuditTest {
    @Test
    void loginLogsSurviveDatabaseAuditFailureWithoutTrustingAttemptedIdentity() {
        var records = new ArrayList<AuditLogEntry>();
        var mapper = mock(UaaLoginLogMapper.class);
        doThrow(new IllegalStateException("offline")).when(mapper).insert(any(com.kuma.cloud.uaa.domain.entity.UaaLoginLog.class));
        var service = new LoginAuditService(mapper, new ServletAuthenticationAuditRecorder(records::add));
        var request = new MockHttpServletRequest("POST", "/login");
        request.setParameter("password", "not-for-logs");
        service.recordSuccess("authenticated-user", request);
        service.recordFailure("unverified-user", "bad_credentials", request);
        service.recordLogout("authenticated-user", request);
        assertThat(records).hasSize(3);
        assertThat(records.get(0).getOperatorId()).isEqualTo("authenticated-user");
        assertThat(records.get(0).isSuccess()).isTrue();
        assertThat(records.get(1).getOperatorId()).isNull();
        assertThat(records.get(1).isSuccess()).isFalse();
        assertThat(records.get(1).getDescription()).isEqualTo("login_failed");
        assertThat(records.get(2).getType()).isEqualTo(com.kuma.boot.auditlog.enums.OperateType.LOGOUT);
        assertThat(records).allSatisfy(record -> {
            assertThat(record.getParams()).isNull();
            assertThat(record.getResult()).isNull();
        });
        verify(mapper, times(2)).insert(any(com.kuma.cloud.uaa.domain.entity.UaaLoginLog.class));
    }
}
