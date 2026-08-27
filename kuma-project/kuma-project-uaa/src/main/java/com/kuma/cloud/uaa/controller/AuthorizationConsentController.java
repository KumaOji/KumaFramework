package com.kuma.cloud.uaa.controller;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.cloud.uaa.config.AuthorizationServerConfig;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.consent.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 授权确认页。
 *
 * <p>客户端开启 {@code requireAuthorizationConsent} 后，Spring Authorization Server 会把用户重定向到
 * {@link AuthorizationServerConfig#CONSENT_PAGE}；页面需把用户勾选的 scope 连同 state 原样 POST 回
 * {@code /oauth2/authorize} 以继续授权码流程。
 *
 * @author kuma
 */
@Hidden
@Controller
@RequiredArgsConstructor
public class AuthorizationConsentController {

    private final RegisteredClientRepository registeredClientRepository;
    private final OAuth2AuthorizationConsentService authorizationConsentService;

    @GetMapping(AuthorizationServerConfig.CONSENT_PAGE)
    public String consent(
            Principal principal,
            Model model,
            @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
            @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
            @RequestParam(OAuth2ParameterNames.STATE) String state,
            @RequestParam(value = OAuth2ParameterNames.USER_CODE, required = false) String userCode) {
        RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);
        if (registeredClient == null) {
            throw new BusinessException("客户端不存在: " + clientId);
        }

        OAuth2AuthorizationConsent currentConsent =
                authorizationConsentService.findById(registeredClient.getId(), principal.getName());
        Set<String> approvedScopes =
                currentConsent == null ? Set.of() : currentConsent.getScopes();

        Set<String> scopesToApprove = new LinkedHashSet<>();
        Set<String> previouslyApprovedScopes = new LinkedHashSet<>();
        for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
            // openid 是协议层必需的范围，不作为用户可勾选项
            if (OidcScopes.OPENID.equals(requestedScope)) {
                continue;
            }
            if (approvedScopes.contains(requestedScope)) {
                previouslyApprovedScopes.add(requestedScope);
            } else {
                scopesToApprove.add(requestedScope);
            }
        }

        model.addAttribute("clientId", clientId);
        model.addAttribute("clientName", registeredClient.getClientName());
        model.addAttribute("state", state);
        model.addAttribute("userCode", userCode);
        model.addAttribute("principalName", principal.getName());
        model.addAttribute("scopes", scopesToApprove);
        model.addAttribute("previouslyApprovedScopes", previouslyApprovedScopes);
        return "consent";
    }
}
