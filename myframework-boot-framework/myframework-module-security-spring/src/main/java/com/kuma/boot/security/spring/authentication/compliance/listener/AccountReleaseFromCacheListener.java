/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationListener
 */
package com.kuma.boot.security.spring.authentication.compliance.listener;

import com.kuma.boot.security.spring.authentication.compliance.OAuth2AccountStatusManager;
import com.kuma.boot.security.spring.authentication.compliance.event.AccountReleaseFromCacheEvent;
import org.springframework.context.ApplicationListener;

public class AccountReleaseFromCacheListener
implements ApplicationListener<AccountReleaseFromCacheEvent> {
    private final OAuth2AccountStatusManager accountStatusManager;

    public AccountReleaseFromCacheListener(OAuth2AccountStatusManager accountStatusManager) {
        this.accountStatusManager = accountStatusManager;
    }

    public void onApplicationEvent(AccountReleaseFromCacheEvent event) {
        String username = (String)event.getData();
        this.accountStatusManager.releaseFromCache(username);
    }
}

