/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.enums.DataItemStatusEnum
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.security.core.userdetails.UserDetailsService
 */
package com.kuma.boot.security.spring.authentication.compliance;

import com.kuma.boot.common.enums.DataItemStatusEnum;
import com.kuma.boot.security.spring.authentication.compliance.processor.changer.AccountStatusChanger;
import com.kuma.boot.security.spring.authentication.stamp.LockedUserDetailsStampManager;
import com.kuma.boot.security.spring.core.userdetails.EnhanceUserDetailsService;
import com.kuma.boot.security.spring.core.userdetails.TtcUser;
import com.kuma.boot.security.spring.event.domain.TtcUserStatus;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;

public class OAuth2AccountStatusManager {
    private static final Logger log = LoggerFactory.getLogger(OAuth2AccountStatusManager.class);
    private final UserDetailsService userDetailsService;
    private final AccountStatusChanger accountStatusChanger;
    private final LockedUserDetailsStampManager lockedUserDetailsStampManager;

    public OAuth2AccountStatusManager(UserDetailsService userDetailsService, AccountStatusChanger accountStatusChanger, LockedUserDetailsStampManager lockedUserDetailsStampManager) {
        this.userDetailsService = userDetailsService;
        this.lockedUserDetailsStampManager = lockedUserDetailsStampManager;
        this.accountStatusChanger = accountStatusChanger;
    }

    private EnhanceUserDetailsService getUserDetailsService() {
        return (EnhanceUserDetailsService)this.userDetailsService;
    }

    private String getUserId(String username) {
        EnhanceUserDetailsService enhanceUserDetailsService = this.getUserDetailsService();
        TtcUser user = enhanceUserDetailsService.loadTtcUserByUsername(username);
        if (ObjectUtils.isNotEmpty((Object)((Object)user))) {
            return String.valueOf(user.getUserId());
        }
        log.warn(" Can not found the userid for [{}]", (Object)username);
        return null;
    }

    public void lock(String username) {
        String userId = this.getUserId(username);
        if (ObjectUtils.isNotEmpty((Object)userId)) {
            this.accountStatusChanger.process(new TtcUserStatus(userId, DataItemStatusEnum.LOCKING.name()));
            this.lockedUserDetailsStampManager.put(userId, username);
            log.info(" User count [{}] has been locked, and record into cache!", (Object)username);
        }
    }

    public void enable(String userId) {
        if (ObjectUtils.isNotEmpty((Object)userId)) {
            this.accountStatusChanger.process(new TtcUserStatus(userId, DataItemStatusEnum.ENABLE.name()));
        }
    }

    public void releaseFromCache(String username) {
        String value;
        String userId = this.getUserId(username);
        if (ObjectUtils.isNotEmpty((Object)userId) && StringUtils.isNotEmpty((CharSequence)(value = (String)this.lockedUserDetailsStampManager.get(userId)))) {
            this.lockedUserDetailsStampManager.delete(userId);
            log.info(" User count [{}] locked info has been release!", (Object)username);
        }
    }
}

