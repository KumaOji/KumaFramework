package com.kuma.cloud.uaa.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 吊销指定用户的 OAuth2 授权记录（Access Token / Refresh Token / 授权码）。
 *
 * <p>禁用账号、重置密码或用户主动改密后调用，使在途令牌立即失效。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UaaTokenRevocationService {

    private final JdbcTemplate jdbcTemplate;

    public void revokeByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return;
        }
        int removed = jdbcTemplate.update(
                "DELETE FROM oauth2_authorization WHERE principal_name = ?", username);
        if (removed > 0) {
            log.info("已吊销用户 {} 的 {} 条 OAuth2 授权记录", username, removed);
        }
    }
}
