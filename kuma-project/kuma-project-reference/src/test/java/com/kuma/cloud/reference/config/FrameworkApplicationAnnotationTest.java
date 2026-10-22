package com.kuma.cloud.reference.config;

import com.kuma.boot.security.spring.annotation.EnableOauth2ResourceServer;
import com.kuma.boot.web.annotation.KumaBootApplication;
import com.kuma.boot.web.annotation.KumaBootApplicationImportSelector;
import com.kuma.cloud.reference.ReferenceApplication;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.core.type.AnnotationMetadata;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FrameworkApplicationAnnotationTest {
    @Test
    void existingApplicationsStillEnableResourceServerByDefault() throws Exception {
        var metadata = mock(AnnotationMetadata.class);
        var enabled = KumaBootApplication.class.getMethod("oauth2ResourceServer").getDefaultValue();
        assertEquals(true, enabled);
        when(metadata.getAnnotationAttributes(KumaBootApplication.class.getName()))
                .thenReturn(Map.of("oauth2ResourceServer", enabled));
        var imports = new KumaBootApplicationImportSelector().selectImports(metadata);
        assertEquals(1, imports.length);
        assertTrue(Class.forName(imports[0]).isAnnotationPresent(EnableOauth2ResourceServer.class));
    }

    @Test
    void anonymousReferenceExplicitlyOptsOutOfResourceServerImports() {
        assertEquals(0, new KumaBootApplicationImportSelector()
                .selectImports(AnnotationMetadata.introspect(ReferenceApplication.class)).length);
    }
}
