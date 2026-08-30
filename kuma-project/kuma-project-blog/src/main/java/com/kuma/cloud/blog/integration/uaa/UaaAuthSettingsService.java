package com.kuma.cloud.blog.integration.uaa;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.cloud.blog.domain.vo.UaaAuthSettingsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UaaAuthSettingsService {

    private static final long CACHE_TTL_MILLIS = 30_000L;

    private final UaaAuthSettingsClient client;

    private volatile UaaAuthSettingsVO cache;
    private volatile long cacheExpiresAt;

    public boolean isMfaEnabled() {
        return settings().isMfaEnabled();
    }

    public String mfaIssuer() {
        String issuer = settings().getMfaIssuer();
        return StringUtils.hasText(issuer) ? issuer : "KumaCloud";
    }

    public void requireMfaEnabled() {
        if (!isMfaEnabled()) {
            throw new BusinessException("二次验证功能未开启");
        }
    }

    private UaaAuthSettingsVO settings() {
        long now = System.currentTimeMillis();
        UaaAuthSettingsVO current = cache;
        if (current != null && now < cacheExpiresAt) {
            return current;
        }
        UaaAuthSettingsVO fresh = client.fetch();
        cache = fresh;
        cacheExpiresAt = now + CACHE_TTL_MILLIS;
        return fresh;
    }
}
