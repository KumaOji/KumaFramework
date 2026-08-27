package com.kuma.cloud.uaa.service;

import com.kuma.cloud.uaa.domain.entity.UaaUser;
import com.kuma.cloud.uaa.domain.vo.MfaBindVO;

/**
 * 基于 TOTP（RFC 6238）的二次校验，复用 kuma-boot-starter-totp 的密钥生成、
 * 动态码校验与二维码渲染能力。
 */
public interface MfaService {

    /**
     * 生成待确认的共享密钥与二维码，密钥暂存 Redis，确认后才落库。
     */
    MfaBindVO startBind(String username);

    void confirmBind(String username, String code);

    void unbind(String username, String code);

    /**
     * 校验动态码；用户未开启 MFA 时直接返回 true。
     */
    boolean verify(UaaUser user, String code);
}
