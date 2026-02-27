//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.security.justauth.factory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.security.justauth.autoconfigure.properties.ExtendProperties;
import com.kuma.boot.security.justauth.autoconfigure.properties.JustAuthProperties;
import com.xkcoding.http.config.HttpConfig;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.enums.AuthResponseStatus;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.request.AuthAliyunRequest;
import me.zhyd.oauth.request.AuthAmazonRequest;
import me.zhyd.oauth.request.AuthBaiduRequest;
import me.zhyd.oauth.request.AuthCodingRequest;
import me.zhyd.oauth.request.AuthDingTalkAccountRequest;
import me.zhyd.oauth.request.AuthDingTalkRequest;
import me.zhyd.oauth.request.AuthDouyinRequest;
import me.zhyd.oauth.request.AuthElemeRequest;
import me.zhyd.oauth.request.AuthFacebookRequest;
import me.zhyd.oauth.request.AuthFeishuRequest;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthGitlabRequest;
import me.zhyd.oauth.request.AuthGoogleRequest;
import me.zhyd.oauth.request.AuthHuaweiRequest;
import me.zhyd.oauth.request.AuthJdRequest;
import me.zhyd.oauth.request.AuthKujialeRequest;
import me.zhyd.oauth.request.AuthLineRequest;
import me.zhyd.oauth.request.AuthLinkedinRequest;
import me.zhyd.oauth.request.AuthMeituanRequest;
import me.zhyd.oauth.request.AuthMiRequest;
import me.zhyd.oauth.request.AuthMicrosoftRequest;
import me.zhyd.oauth.request.AuthOktaRequest;
import me.zhyd.oauth.request.AuthOschinaRequest;
import me.zhyd.oauth.request.AuthPinterestRequest;
import me.zhyd.oauth.request.AuthQqRequest;
import me.zhyd.oauth.request.AuthRenrenRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.request.AuthSlackRequest;
import me.zhyd.oauth.request.AuthStackOverflowRequest;
import me.zhyd.oauth.request.AuthTaobaoRequest;
import me.zhyd.oauth.request.AuthTeambitionRequest;
import me.zhyd.oauth.request.AuthToutiaoRequest;
import me.zhyd.oauth.request.AuthTwitterRequest;
import me.zhyd.oauth.request.AuthWeChatEnterpriseQrcodeRequest;
import me.zhyd.oauth.request.AuthWeChatEnterpriseWebRequest;
import me.zhyd.oauth.request.AuthWeChatMpRequest;
import me.zhyd.oauth.request.AuthWeChatOpenRequest;
import me.zhyd.oauth.request.AuthWeiboRequest;
import me.zhyd.oauth.request.AuthXmlyRequest;
import org.springframework.util.CollectionUtils;

public class AuthRequestFactory {
    private final JustAuthProperties properties;
    private final AuthStateCache authStateCache;

    public AuthRequestFactory(JustAuthProperties properties, AuthStateCache authStateCache) {
        this.properties = properties;
        this.authStateCache = authStateCache;
    }

    public List<String> oauthList() {
        List<String> defaultList = new ArrayList(this.properties.getType().keySet());
        List<String> extendList = new ArrayList();
        ExtendProperties extend = this.properties.getExtend();
        if (null != extend) {
            Class enumClass = extend.getEnumClass();
            List<String> names = EnumUtil.getNames(enumClass);
            Stream var10000 = extend.getConfig().keySet().stream().map(String::toUpperCase);
            Objects.requireNonNull(names);
            extendList = var10000.filter(names::contains).toList();
        }

        return (List)CollUtil.addAll(defaultList, extendList);
    }

    public AuthRequest get(String source) {
        if (StrUtil.isBlank(source)) {
            throw new AuthException(AuthResponseStatus.NO_AUTH_SOURCE);
        } else {
            AuthRequest authRequest = this.getDefaultRequest(source);
            if (authRequest == null) {
                authRequest = this.getExtendRequest(this.properties.getExtend().getEnumClass(), source);
            }

            if (authRequest == null) {
                throw new AuthException(AuthResponseStatus.UNSUPPORTED);
            } else {
                return authRequest;
            }
        }
    }

    private AuthRequest getExtendRequest(Class clazz, String source) {
        String upperSource = source.toUpperCase();

        try {
            EnumUtil.fromString(clazz, upperSource);
        } catch (IllegalArgumentException var8) {
            return null;
        }

        Map<String, ExtendProperties.ExtendRequestConfig> extendConfig = this.properties.getExtend().getConfig();
        Map<String, ExtendProperties.ExtendRequestConfig> upperConfig = new HashMap(6);
        extendConfig.forEach((k, v) -> upperConfig.put(k.toUpperCase(), v));
        ExtendProperties.ExtendRequestConfig extendRequestConfig = (ExtendProperties.ExtendRequestConfig)upperConfig.get(upperSource);
        if (extendRequestConfig != null) {
            this.configureHttpConfig(upperSource, extendRequestConfig, this.properties.getHttpConfig());
            Class<? extends AuthRequest> requestClass = extendRequestConfig.getRequestClass();
            if (requestClass != null) {
                return (AuthRequest)ReflectUtil.newInstance(requestClass, new Object[]{extendRequestConfig, this.authStateCache});
            }
        }

        return null;
    }

    private AuthRequest getDefaultRequest(String source) {
        AuthDefaultSource authDefaultSource;
        try {
            authDefaultSource = (AuthDefaultSource)EnumUtil.fromString(AuthDefaultSource.class, source.toUpperCase());
        } catch (IllegalArgumentException var4) {
            return null;
        }

        AuthConfig config = (AuthConfig)this.properties.getType().get(authDefaultSource.name());
        if (config == null) {
            return null;
        } else {
            this.configureHttpConfig(authDefaultSource.name(), config, this.properties.getHttpConfig());
            switch (authDefaultSource) {
                case GITHUB -> {
                    return new AuthGithubRequest(config, this.authStateCache);
                }
                case WEIBO -> {
                    return new AuthWeiboRequest(config, this.authStateCache);
                }
                case GITEE -> {
                    return new AuthGiteeRequest(config, this.authStateCache);
                }
                case DINGTALK -> {
                    return new AuthDingTalkRequest(config, this.authStateCache);
                }
                case DINGTALK_ACCOUNT -> {
                    return new AuthDingTalkAccountRequest(config, this.authStateCache);
                }
                case BAIDU -> {
                    return new AuthBaiduRequest(config, this.authStateCache);
                }
                case CODING -> {
                    return new AuthCodingRequest(config, this.authStateCache);
                }
                case OSCHINA -> {
                    return new AuthOschinaRequest(config, this.authStateCache);
                }
                case QQ -> {
                    return new AuthQqRequest(config, this.authStateCache);
                }
                case WECHAT_OPEN -> {
                    return new AuthWeChatOpenRequest(config, this.authStateCache);
                }
                case WECHAT_MP -> {
                    return new AuthWeChatMpRequest(config, this.authStateCache);
                }
                case WECHAT_ENTERPRISE -> {
                    return new AuthWeChatEnterpriseQrcodeRequest(config, this.authStateCache);
                }
                case WECHAT_ENTERPRISE_WEB -> {
                    return new AuthWeChatEnterpriseWebRequest(config, this.authStateCache);
                }
                case TAOBAO -> {
                    return new AuthTaobaoRequest(config, this.authStateCache);
                }
                case GOOGLE -> {
                    return new AuthGoogleRequest(config, this.authStateCache);
                }
                case FACEBOOK -> {
                    return new AuthFacebookRequest(config, this.authStateCache);
                }
                case DOUYIN -> {
                    return new AuthDouyinRequest(config, this.authStateCache);
                }
                case LINKEDIN -> {
                    return new AuthLinkedinRequest(config, this.authStateCache);
                }
                case MICROSOFT -> {
                    return new AuthMicrosoftRequest(config, this.authStateCache);
                }
                case MI -> {
                    return new AuthMiRequest(config, this.authStateCache);
                }
                case TOUTIAO -> {
                    return new AuthToutiaoRequest(config, this.authStateCache);
                }
                case TEAMBITION -> {
                    return new AuthTeambitionRequest(config, this.authStateCache);
                }
                case RENREN -> {
                    return new AuthRenrenRequest(config, this.authStateCache);
                }
                case PINTEREST -> {
                    return new AuthPinterestRequest(config, this.authStateCache);
                }
                case STACK_OVERFLOW -> {
                    return new AuthStackOverflowRequest(config, this.authStateCache);
                }
                case HUAWEI -> {
                    return new AuthHuaweiRequest(config, this.authStateCache);
                }
                case GITLAB -> {
                    return new AuthGitlabRequest(config, this.authStateCache);
                }
                case KUJIALE -> {
                    return new AuthKujialeRequest(config, this.authStateCache);
                }
                case ELEME -> {
                    return new AuthElemeRequest(config, this.authStateCache);
                }
                case MEITUAN -> {
                    return new AuthMeituanRequest(config, this.authStateCache);
                }
                case TWITTER -> {
                    return new AuthTwitterRequest(config, this.authStateCache);
                }
                case FEISHU -> {
                    return new AuthFeishuRequest(config, this.authStateCache);
                }
                case JD -> {
                    return new AuthJdRequest(config, this.authStateCache);
                }
                case ALIYUN -> {
                    return new AuthAliyunRequest(config, this.authStateCache);
                }
                case XMLY -> {
                    return new AuthXmlyRequest(config, this.authStateCache);
                }
                case AMAZON -> {
                    return new AuthAmazonRequest(config, this.authStateCache);
                }
                case SLACK -> {
                    return new AuthSlackRequest(config, this.authStateCache);
                }
                case LINE -> {
                    return new AuthLineRequest(config, this.authStateCache);
                }
                case OKTA -> {
                    return new AuthOktaRequest(config, this.authStateCache);
                }
                default -> {
                    return null;
                }
            }
        }
    }

    private void configureHttpConfig(String authSource, AuthConfig authConfig, JustAuthProperties.JustAuthHttpConfig httpConfig) {
        if (null != httpConfig) {
            Map<String, JustAuthProperties.JustAuthProxyConfig> proxyConfigMap = httpConfig.getProxy();
            if (!CollectionUtils.isEmpty(proxyConfigMap)) {
                JustAuthProperties.JustAuthProxyConfig proxyConfig = (JustAuthProperties.JustAuthProxyConfig)proxyConfigMap.get(authSource);
                if (null != proxyConfig) {
                    authConfig.setHttpConfig(HttpConfig.builder().timeout(httpConfig.getTimeout()).proxy(new Proxy(Type.valueOf(proxyConfig.getType()), new InetSocketAddress(proxyConfig.getHostname(), proxyConfig.getPort()))).build());
                }
            }
        }
    }
}
