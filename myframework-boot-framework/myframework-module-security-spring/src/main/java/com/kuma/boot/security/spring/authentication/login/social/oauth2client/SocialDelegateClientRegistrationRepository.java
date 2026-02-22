/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.security.oauth2.client.registration.ClientRegistration
 *  org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

public class SocialDelegateClientRegistrationRepository
implements ClientRegistrationRepository {
    private Function<String, ClientRegistration> delegate;
    private final Map<String, ClientRegistration> clientRegistrationMap = new HashMap<String, ClientRegistration>();

    public void setDelegate(Function<String, ClientRegistration> delegate) {
        this.delegate = delegate;
    }

    public ClientRegistration findByRegistrationId(String registrationId) {
        if (this.clientRegistrationMap.containsKey(registrationId)) {
            return this.clientRegistrationMap.get(registrationId);
        }
        return this.delegate.apply(registrationId);
    }

    public Map<String, ClientRegistration> getClientRegistrationMap() {
        return this.clientRegistrationMap;
    }

    public void addClientRegistration(ClientRegistration clientRegistration) {
        this.clientRegistrationMap.putIfAbsent(clientRegistration.getRegistrationId(), clientRegistration);
    }
}

