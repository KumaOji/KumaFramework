/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.Version
 *  com.fasterxml.jackson.databind.Module$SetupContext
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.module.SimpleModule
 *  me.zhyd.oauth.model.AuthToken
 *  me.zhyd.oauth.model.AuthUser
 *  org.springframework.security.authentication.AnonymousAuthenticationToken
 *  org.springframework.security.authentication.RememberMeAuthenticationToken
 *  org.springframework.security.core.userdetails.User
 *  org.springframework.security.jackson2.SecurityJackson2Modules
 *  org.springframework.security.web.authentication.WebAuthenticationDetails
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.deserializes;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.kuma.boot.security.spring.authentication.login.social.justauth.JustAuthAuthenticationToken;
import com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails.TemporaryUser;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class Auth2Jackson2Module
extends SimpleModule {
    public Auth2Jackson2Module() {
        super(Auth2Jackson2Module.class.getName(), new Version(1, 0, 0, null, null, null));
    }

    public void setupModule(Module.SetupContext context) {
        SecurityJackson2Modules.enableDefaultTyping((ObjectMapper)((ObjectMapper)context.getOwner()));
        context.setMixInAnnotations(JustAuthAuthenticationToken.class, Auth2AuthenticationTokenJsonDeserializer.Auth2AuthenticationTokenMixin.class);
        context.setMixInAnnotations(RememberMeAuthenticationToken.class, RememberMeAuthenticationTokenJsonDeserializer.RememberMeAuthenticationTokenMixin.class);
        context.setMixInAnnotations(AnonymousAuthenticationToken.class, AnonymousAuthenticationTokenJsonDeserializer.AnonymousAuthenticationTokenMixin.class);
        context.setMixInAnnotations(User.class, UserDeserializer.UserMixin.class);
        context.setMixInAnnotations(TemporaryUser.class, TemporaryUserDeserializer.TemporaryUserMixin.class);
        context.setMixInAnnotations(WebAuthenticationDetails.class, WebAuthenticationDetailsDeserializer.WebAuthenticationDetailsMixin.class);
        context.setMixInAnnotations(AuthUser.class, AuthUserJsonDeserializer.AuthUserMixin.class);
        context.setMixInAnnotations(AuthToken.class, AuthUserJsonDeserializer.AuthTokenMixin.class);
    }
}

