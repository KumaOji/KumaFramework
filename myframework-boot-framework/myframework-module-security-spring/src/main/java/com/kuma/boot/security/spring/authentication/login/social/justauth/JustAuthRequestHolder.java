/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.security.justauth.justauth.JustAuthProperties
 *  com.kuma.boot.security.justauth.justauth.cache.AuthStateRedisCache
 *  com.kuma.boot.security.justauth.justauth.cache.AuthStateSessionCache
 *  com.kuma.boot.security.justauth.justauth.enums.StateCacheType
 *  com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest
 *  com.kuma.boot.security.justauth.justauth.source.AuthCustomizeSource
 *  com.kuma.boot.security.justauth.justauth.source.AuthGitlabPrivateSource
 *  me.zhyd.oauth.cache.AuthDefaultStateCache
 *  me.zhyd.oauth.cache.AuthStateCache
 *  me.zhyd.oauth.config.AuthConfig
 *  me.zhyd.oauth.config.AuthConfig$AuthConfigBuilder
 *  me.zhyd.oauth.config.AuthDefaultSource
 *  me.zhyd.oauth.config.AuthSource
 *  me.zhyd.oauth.enums.scope.AuthBaiduScope
 *  me.zhyd.oauth.enums.scope.AuthCodingScope
 *  me.zhyd.oauth.enums.scope.AuthFacebookScope
 *  me.zhyd.oauth.enums.scope.AuthGiteeScope
 *  me.zhyd.oauth.enums.scope.AuthGithubScope
 *  me.zhyd.oauth.enums.scope.AuthGitlabScope
 *  me.zhyd.oauth.enums.scope.AuthGoogleScope
 *  me.zhyd.oauth.enums.scope.AuthHuaweiScope
 *  me.zhyd.oauth.enums.scope.AuthJdScope
 *  me.zhyd.oauth.enums.scope.AuthKujialeScope
 *  me.zhyd.oauth.enums.scope.AuthLinkedinScope
 *  me.zhyd.oauth.enums.scope.AuthMiScope
 *  me.zhyd.oauth.enums.scope.AuthMicrosoftScope
 *  me.zhyd.oauth.enums.scope.AuthPinterestScope
 *  me.zhyd.oauth.enums.scope.AuthQqScope
 *  me.zhyd.oauth.enums.scope.AuthRenrenScope
 *  me.zhyd.oauth.enums.scope.AuthScope
 *  me.zhyd.oauth.enums.scope.AuthStackoverflowScope
 *  me.zhyd.oauth.enums.scope.AuthWeChatEnterpriseWebScope
 *  me.zhyd.oauth.enums.scope.AuthWechatMpScope
 *  me.zhyd.oauth.enums.scope.AuthWeiboScope
 *  me.zhyd.oauth.request.AuthDefaultRequest
 *  me.zhyd.oauth.utils.AuthScopeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext
 *  org.springframework.cglib.proxy.Callback
 *  org.springframework.cglib.proxy.Enhancer
 *  org.springframework.cglib.proxy.MethodInterceptor
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.web.context.WebApplicationContext
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.justauth.justauth.cache.AuthStateRedisCache;
import com.kuma.boot.security.justauth.justauth.cache.AuthStateSessionCache;
import com.kuma.boot.security.justauth.justauth.enums.StateCacheType;
import com.kuma.boot.security.justauth.justauth.request.Auth2DefaultRequest;
import com.kuma.boot.security.justauth.justauth.source.AuthCustomizeSource;
import com.kuma.boot.security.justauth.justauth.source.AuthGitlabPrivateSource;
import com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties;
import com.kuma.boot.security.spring.authentication.login.social.justauth.properties.HttpConfigProperties;
import com.kuma.boot.security.spring.authentication.login.social.justauth.properties.JustAuthProperties;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import me.zhyd.oauth.cache.AuthDefaultStateCache;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.enums.scope.AuthBaiduScope;
import me.zhyd.oauth.enums.scope.AuthCodingScope;
import me.zhyd.oauth.enums.scope.AuthFacebookScope;
import me.zhyd.oauth.enums.scope.AuthGiteeScope;
import me.zhyd.oauth.enums.scope.AuthGithubScope;
import me.zhyd.oauth.enums.scope.AuthGitlabScope;
import me.zhyd.oauth.enums.scope.AuthGoogleScope;
import me.zhyd.oauth.enums.scope.AuthHuaweiScope;
import me.zhyd.oauth.enums.scope.AuthJdScope;
import me.zhyd.oauth.enums.scope.AuthKujialeScope;
import me.zhyd.oauth.enums.scope.AuthLinkedinScope;
import me.zhyd.oauth.enums.scope.AuthMiScope;
import me.zhyd.oauth.enums.scope.AuthMicrosoftScope;
import me.zhyd.oauth.enums.scope.AuthPinterestScope;
import me.zhyd.oauth.enums.scope.AuthQqScope;
import me.zhyd.oauth.enums.scope.AuthRenrenScope;
import me.zhyd.oauth.enums.scope.AuthScope;
import me.zhyd.oauth.enums.scope.AuthStackoverflowScope;
import me.zhyd.oauth.enums.scope.AuthWeChatEnterpriseWebScope;
import me.zhyd.oauth.enums.scope.AuthWechatMpScope;
import me.zhyd.oauth.enums.scope.AuthWeiboScope;
import me.zhyd.oauth.request.AuthDefaultRequest;
import me.zhyd.oauth.utils.AuthScopeUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

public final class JustAuthRequestHolder
implements InitializingBean,
ApplicationContextAware {
    private static volatile JustAuthRequestHolder INSTANCE;
    private static final String FIELD_SEPARATOR = "_";
    private static final String CLIENT_ID_FIELD_NAME = "clientId";
    private static final String CLIENT_SECRET_FIELD_NAME = "clientSecret";
    private static final Map<String, Auth2DefaultRequest> PROVIDER_ID_AUTH_REQUEST_MAP;
    private static final Map<AuthSource, String> SOURCE_PROVIDER_ID_MAP;
    private ApplicationContext applicationContext;
    private static volatile AuthCustomizeSource authCustomizeSource;
    private static volatile AuthGitlabPrivateSource authGitlabPrivateSource;
    public static final String AUTH_REQUEST_PACKAGE = "me.zhyd.oauth.request.";
    public static final String AUTH_REQUEST_PREFIX = "Auth";
    public static final String AUTH_REQUEST_SUFFIX = "Request";

    private JustAuthRequestHolder() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static JustAuthRequestHolder getInstance() {
        if (Objects.nonNull(INSTANCE)) {
            return INSTANCE;
        }
        Class<JustAuthRequestHolder> clazz = JustAuthRequestHolder.class;
        synchronized (JustAuthRequestHolder.class) {
            if (Objects.isNull(INSTANCE)) {
                JustAuthRequestHolder justAuthRequestHolder;
                INSTANCE = justAuthRequestHolder = new JustAuthRequestHolder();
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return INSTANCE;
        }
    }

    public static synchronized void setAuthCustomizeSource(AuthCustomizeSource authCustomizeSource) {
        if (JustAuthRequestHolder.authCustomizeSource == null) {
            JustAuthRequestHolder.authCustomizeSource = authCustomizeSource;
        }
    }

    public static synchronized void setAuthGitlabPrivateSource(AuthGitlabPrivateSource authGitlabPrivateSource) {
        if (JustAuthRequestHolder.authGitlabPrivateSource == null) {
            JustAuthRequestHolder.authGitlabPrivateSource = authGitlabPrivateSource;
        }
    }

    @Nullable
    public static Auth2DefaultRequest getAuth2DefaultRequest(String providerId) {
        if (PROVIDER_ID_AUTH_REQUEST_MAP.isEmpty() || providerId == null) {
            return null;
        }
        return PROVIDER_ID_AUTH_REQUEST_MAP.get(providerId);
    }

    public static String getProviderId(AuthSource source) {
        if (SOURCE_PROVIDER_ID_MAP.isEmpty() || null == source) {
            return null;
        }
        return SOURCE_PROVIDER_ID_MAP.get(source);
    }

    public static Collection<String> getValidProviderIds() {
        return Collections.unmodifiableCollection(PROVIDER_ID_AUTH_REQUEST_MAP.keySet());
    }

    public static Collection<String> getAllProviderIds() {
        return Collections.unmodifiableCollection(SOURCE_PROVIDER_ID_MAP.values());
    }

    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void afterPropertiesSet() throws Exception {
        Field[] declaredFields;
        AuthStateCache authStateCache;
        block12: {
            block11: {
                try {
                    JustAuthRequestHolder.setAuthCustomizeSource((AuthCustomizeSource)this.applicationContext.getBean(AuthCustomizeSource.class));
                }
                catch (Exception e) {
                    if (!LogUtils.isDebugEnabled()) break block11;
                    LogUtils.debug((String)"\u6ca1\u6709\u81ea\u5b9a\u4e49\u5b9e\u73b0 {}", (Object[])new Object[]{AuthCustomizeSource.class.getName()});
                }
            }
            try {
                JustAuthRequestHolder.setAuthGitlabPrivateSource((AuthGitlabPrivateSource)this.applicationContext.getBean(AuthGitlabPrivateSource.class));
            }
            catch (Exception e) {
                if (!LogUtils.isDebugEnabled()) break block12;
                LogUtils.debug((String)"\u6ca1\u6709\u81ea\u5b9a\u4e49\u5b9e\u73b0 {}", (Object[])new Object[]{AuthGitlabPrivateSource.class.getName()});
            }
        }
        JustAuthProperties auth2Properties = (JustAuthProperties)this.applicationContext.getBean(JustAuthProperties.class);
        com.kuma.boot.security.justauth.justauth.JustAuthProperties justAuthProperties = auth2Properties.getJustAuth();
        StateCacheType stateCacheType = justAuthProperties.getCacheType();
        if (stateCacheType.equals((Object)StateCacheType.REDIS)) {
            Class<?> stringRedisTemplateClass = Class.forName("org.springframework.data.redis.core.StringRedisTemplate");
            Object stringRedisTemplate = this.applicationContext.getBean(stringRedisTemplateClass);
            authStateCache = this.getAuthStateCache(stateCacheType, auth2Properties, stringRedisTemplate);
        } else {
            authStateCache = this.getAuthStateCache(stateCacheType, auth2Properties, null);
        }
        Class<JustAuthProperties> aClass = JustAuthProperties.class;
        for (Field field : declaredFields = aClass.getDeclaredFields()) {
            AuthDefaultSource source;
            String providerId;
            Object baseProperties;
            block13: {
                field.setAccessible(true);
                baseProperties = field.get(auth2Properties);
                if (!(baseProperties instanceof BaseAuth2Properties)) continue;
                providerId = field.getName();
                CharSequence[] splits = org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase((String)providerId);
                source = null;
                try {
                    source = AuthDefaultSource.valueOf((String)String.join((CharSequence)FIELD_SEPARATOR, splits).toUpperCase());
                    SOURCE_PROVIDER_ID_MAP.put((AuthSource)source, providerId);
                }
                catch (Exception e) {
                    if (authCustomizeSource != null && JustAuthRequestHolder.getProviderIdBySource((AuthSource)authCustomizeSource).equals(providerId)) {
                        source = authCustomizeSource;
                        providerId = ((BaseAuth2Properties)baseProperties).getCustomizeProviderId();
                        Class<?> authCustomizeSourceClass = authCustomizeSource.getClass();
                        Field nameField = authCustomizeSourceClass.getSuperclass().getDeclaredField("name");
                        nameField.setAccessible(true);
                        nameField.set(authCustomizeSource, String.join((CharSequence)FIELD_SEPARATOR, org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase((String)providerId)).toUpperCase());
                        SOURCE_PROVIDER_ID_MAP.put((AuthSource)authCustomizeSource, providerId);
                    }
                    if (authGitlabPrivateSource == null || !JustAuthRequestHolder.getProviderIdBySource((AuthSource)authGitlabPrivateSource).equals(providerId)) break block13;
                    source = authGitlabPrivateSource;
                    SOURCE_PROVIDER_ID_MAP.put((AuthSource)authCustomizeSource, providerId);
                }
            }
            BaseAuth2Properties baseAuth2Properties = (BaseAuth2Properties)baseProperties;
            if (baseAuth2Properties.getClientId() == null || baseAuth2Properties.getClientSecret() == null) continue;
            if (source == null) {
                throw new RuntimeException(String.format("\u83b7\u53d6\u4e0d\u5230 %s \u76f8\u5bf9\u5e94\u7684 me.zhyd.oauth.config.AuthSource", providerId));
            }
            Auth2DefaultRequest auth2DefaultRequest = this.getAuth2DefaultRequest((AuthSource)source, auth2Properties, authStateCache);
            if (null == auth2DefaultRequest) continue;
            PROVIDER_ID_AUTH_REQUEST_MAP.put(providerId, auth2DefaultRequest);
        }
    }

    @Nullable
    private Auth2DefaultRequest getAuth2DefaultRequest(@NonNull AuthSource source, @NonNull JustAuthProperties justAuthProperties, @NonNull AuthStateCache authStateCache) throws IllegalAccessException, ClassNotFoundException {
        com.kuma.boot.security.justauth.justauth.JustAuthProperties justAuth = justAuthProperties.getJustAuth();
        AuthConfig config = this.getAuthConfig(justAuthProperties, source);
        List<String> scopes = this.getScopesBySource(justAuthProperties, source);
        config.setScopes(scopes);
        HttpConfigProperties proxy = justAuthProperties.getProxy();
        config.setHttpConfig(proxy.getHttpConfig());
        config.setIgnoreCheckState(justAuth.getIgnoreCheckState().booleanValue());
        if (source instanceof AuthCustomizeSource || source instanceof AuthGitlabPrivateSource) {
            if (justAuthProperties.getCustomize().getCustomizeIsForeign().booleanValue()) {
                config.getHttpConfig().setTimeout((int)proxy.getForeignTimeout().toMillis());
            }
            return this.getAuthDefaultRequestAdapter(config, source, authStateCache, null, null);
        }
        if (!(source instanceof AuthDefaultSource)) {
            return null;
        }
        boolean isNotSupport = false;
        switch ((AuthDefaultSource)source) {
            case CODING: {
                break;
            }
            case ALIPAY: {
                BaseAuth2Properties alipay = justAuthProperties.getAlipay();
                return this.getAuthDefaultRequestAdapter(config, source, authStateCache, alipay.getProxyHost(), alipay.getProxyPort());
            }
            case QQ: {
                config.setUnionId(justAuthProperties.getQq().getUnionId().booleanValue());
                break;
            }
            case WECHAT_ENTERPRISE: {
                config.setAgentId(justAuthProperties.getWechatEnterprise().getAgentId());
                break;
            }
            case XMLY: {
                BaseAuth2Properties xmly = justAuthProperties.getXmly();
                config.setDeviceId(xmly.getDeviceId());
                config.setClientOsType(xmly.getClientOsType());
                config.setPackId(xmly.getPackId());
                break;
            }
            case STACK_OVERFLOW: {
                config.setStackOverflowKey(justAuthProperties.getStackOverflow().getStackOverflowKey());
                config.getHttpConfig().setTimeout((int)proxy.getForeignTimeout().toMillis());
                break;
            }
            case GITHUB:
            case GOOGLE:
            case FACEBOOK:
            case MICROSOFT:
            case PINTEREST:
            case GITLAB:
            case TWITTER: {
                config.getHttpConfig().setTimeout((int)proxy.getForeignTimeout().toMillis());
                break;
            }
            case CSDN: {
                isNotSupport = true;
                break;
            }
        }
        if (isNotSupport) {
            return null;
        }
        return this.getAuthDefaultRequestAdapter(config, source, authStateCache, null, null);
    }

    @Nullable
    private List<String> getScopesBySource(@NonNull JustAuthProperties justAuthProperties, @NonNull AuthSource source) throws IllegalAccessException {
        List<String> customAuthScopes = this.getCustomAuthScopes(justAuthProperties, source);
        if (CollectionUtils.isEmpty(customAuthScopes)) {
            List defaultScopes = AuthScopeUtils.getDefaultScopes((AuthScope[])this.getDefaultScopeBySource(source));
            if (CollectionUtils.isEmpty((Collection)defaultScopes)) {
                return null;
            }
            return defaultScopes;
        }
        return customAuthScopes;
    }

    @Nullable
    private AuthScope[] getDefaultScopeBySource(@NonNull AuthSource source) {
        if (source instanceof AuthCustomizeSource) {
            return ((AuthCustomizeSource)source).getDefaultScopes();
        }
        switch ((AuthDefaultSource)source) {
            case STACK_OVERFLOW: {
                return AuthStackoverflowScope.values();
            }
            case WECHAT_ENTERPRISE_WEB: {
                return AuthWeChatEnterpriseWebScope.values();
            }
            case BAIDU: {
                return AuthBaiduScope.values();
            }
            case CODING: {
                return AuthCodingScope.values();
            }
            case PINTEREST: {
                return AuthPinterestScope.values();
            }
            case GITHUB: {
                return AuthGithubScope.values();
            }
            case MI: {
                return AuthMiScope.values();
            }
            case RENREN: {
                return AuthRenrenScope.values();
            }
            case HUAWEI: {
                return AuthHuaweiScope.values();
            }
            case QQ: {
                return AuthQqScope.values();
            }
            case FACEBOOK: {
                return AuthFacebookScope.values();
            }
            case WECHAT_MP: {
                return AuthWechatMpScope.values();
            }
            case JD: {
                return AuthJdScope.values();
            }
            case GITEE: {
                return AuthGiteeScope.values();
            }
            case WEIBO: {
                return AuthWeiboScope.values();
            }
            case MICROSOFT: {
                return AuthMicrosoftScope.values();
            }
            case GITLAB: {
                return AuthGitlabScope.values();
            }
            case GOOGLE: {
                return AuthGoogleScope.values();
            }
            case KUJIALE: {
                return AuthKujialeScope.values();
            }
            case LINKEDIN: {
                return AuthLinkedinScope.values();
            }
        }
        return null;
    }

    @Nullable
    private List<String> getCustomAuthScopes(@NonNull JustAuthProperties justAuthProperties, @NonNull AuthSource source) throws IllegalAccessException {
        String providerId = JustAuthRequestHolder.getProviderId(source);
        BaseAuth2Properties providerProperties = this.getBaseAuth2PropertiesByProviderId(justAuthProperties, providerId);
        if (Objects.isNull(providerProperties)) {
            return null;
        }
        return providerProperties.getScopes();
    }

    public static String getProviderIdBySource(@NonNull AuthSource source) {
        String[] splits = source.getName().split(FIELD_SEPARATOR);
        return JustAuthRequestHolder.toProviderId(splits);
    }

    @NonNull
    private JustAuthDefaultRequestAdapter getAuthDefaultRequestAdapter(@NonNull AuthConfig config, @NonNull AuthSource source, @NonNull AuthStateCache authStateCache, @Nullable String alipayProxyHost, @Nullable Integer alipayProxyPort) throws ClassNotFoundException {
        JustAuthDefaultRequestAdapter adapter = new JustAuthDefaultRequestAdapter(config, source, authStateCache);
        Class[] argumentTypes = new Class[]{AuthConfig.class, AuthStateCache.class};
        Object[] arguments = new Object[]{config, authStateCache};
        if (AuthDefaultSource.ALIPAY.equals((Object)source) && StringUtils.hasText((String)alipayProxyHost) && Objects.nonNull(alipayProxyPort)) {
            argumentTypes = new Class[]{AuthConfig.class, AuthStateCache.class, String.class, Integer.class};
            arguments = new Object[]{config, authStateCache, alipayProxyHost, alipayProxyPort};
        }
        AuthDefaultRequest proxyObject = this.createProxy(JustAuthRequestHolder.getAuthRequestClassBySource(source), argumentTypes, arguments, adapter);
        adapter.setAuthDefaultRequest(proxyObject);
        return adapter;
    }

    @NonNull
    private AuthDefaultRequest createProxy(Class<?> targetClass, Class<?>[] argumentTypes, Object[] arguments, JustAuthDefaultRequestAdapter adapter) throws ClassNotFoundException {
        if (!AuthDefaultRequest.class.isAssignableFrom(targetClass)) {
            throw new ClassNotFoundException(targetClass.getName() + " \u5fc5\u987b\u662f me.zhyd.oauth.request.AuthDefaultRequest \u7684\u5b50\u7c7b");
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback((Callback)((MethodInterceptor)(target, method, args, methodProxy) -> {
            if (target instanceof AuthDefaultRequest && !(target instanceof JustAuthDefaultRequestAdapter) && "getRealState".equals(method.getName())) {
                return adapter.getRealState((String)args[0]);
            }
            return methodProxy.invokeSuper(target, args);
        }));
        return (AuthDefaultRequest)enhancer.create((Class[])argumentTypes, arguments);
    }

    private AuthStateCache getAuthStateCache(StateCacheType type, JustAuthProperties justAuthProperties, Object stringRedisTemplate) {
        switch (type) {
            case DEFAULT: {
                return AuthDefaultStateCache.INSTANCE;
            }
            case SESSION: {
                return new AuthStateSessionCache(justAuthProperties.getJustAuth());
            }
            case REDIS: {
                if (stringRedisTemplate == null) {
                    throw new RuntimeException(String.format("applicationContext \u4e2d\u83b7\u53d6\u4e0d\u5230 %s, %s \u7c7b\u578b\u7684\u7f13\u5b58\u65e0\u6cd5\u521b\u5efa!", "org.springframework.data.redis.core.StringRedisTemplate", type.name()));
                }
                return new AuthStateRedisCache(justAuthProperties.getJustAuth(), stringRedisTemplate);
            }
        }
        LogUtils.error((String)"{} \u7c7b\u578b\u4e0d\u5339\u914d, \u4f7f\u7528 {} \u7c7b\u578b\u7f13\u5b58\u66ff\u4ee3", (Object[])new Object[]{StateCacheType.class.getName(), StateCacheType.DEFAULT.name()});
        return AuthDefaultStateCache.INSTANCE;
    }

    private AuthConfig getAuthConfig(@NonNull JustAuthProperties justAuthProperties, @NonNull AuthSource source) throws IllegalAccessException, NullPointerException {
        Field[] declaredFields;
        AuthConfig.AuthConfigBuilder builder = AuthConfig.builder();
        String providerId = JustAuthRequestHolder.getProviderId(source);
        BaseAuth2Properties providerProperties = this.getBaseAuth2PropertiesByProviderId(justAuthProperties, providerId);
        Objects.requireNonNull(providerProperties, String.format("\u83b7\u53d6\u4e0d\u5230 %s \u7c7b\u578b\u6240\u5bf9\u5e94\u7684 BaseAuth2Properties \u7684\u5b50\u7c7b", source.getName()));
        Class<BaseAuth2Properties> baseClass = BaseAuth2Properties.class;
        for (Field field : declaredFields = baseClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (CLIENT_ID_FIELD_NAME.equals(field.getName())) {
                String clientId = (String)field.get(providerProperties);
                Objects.requireNonNull(clientId, String.format("\u83b7\u53d6\u4e0d\u5230 %s \u7c7b\u578b\u6240\u5bf9\u5e94\u7684 %s \u7684\u503c", source.getName(), CLIENT_ID_FIELD_NAME));
                builder.clientId(clientId);
            }
            if (!CLIENT_SECRET_FIELD_NAME.equals(field.getName())) continue;
            String clientSecret = (String)field.get(providerProperties);
            Objects.requireNonNull(clientSecret, String.format("\u83b7\u53d6\u4e0d\u5230 %s \u7c7b\u578b\u6240\u5bf9\u5e94\u7684 %s \u7684\u503c", source.getName(), CLIENT_SECRET_FIELD_NAME));
            builder.clientSecret(clientSecret);
        }
        String redirectUri = justAuthProperties.getDomain() + this.getServletContextPath() + justAuthProperties.getRedirectUrlPrefix() + "/" + providerId;
        return builder.redirectUri(redirectUri).build();
    }

    @Nullable
    private BaseAuth2Properties getBaseAuth2PropertiesByProviderId(JustAuthProperties justAuthProperties, String providerId) throws IllegalAccessException {
        Class<?> aClass = justAuthProperties.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        Object providerProperties = null;
        for (Field field : declaredFields) {
            BaseAuth2Properties baseAuth2Properties;
            String customizeProviderId;
            field.setAccessible(true);
            if (field.getName().equals(providerId)) {
                providerProperties = field.get(justAuthProperties);
                break;
            }
            if (!"customize".equals(field.getName()) || !StringUtils.hasText((String)(customizeProviderId = (baseAuth2Properties = (BaseAuth2Properties)field.get(justAuthProperties)).getCustomizeProviderId())) || !customizeProviderId.equals(providerId)) continue;
            providerProperties = baseAuth2Properties;
            break;
        }
        return (BaseAuth2Properties)providerProperties;
    }

    @NonNull
    public static Class<?> getAuthRequestClassBySource(@NonNull AuthSource source) throws ClassNotFoundException {
        if (source instanceof AuthCustomizeSource) {
            if (authCustomizeSource == null) {
                throw new RuntimeException("\u5fc5\u987b\u5b9e\u73b0 top.dcenter.ums.security.core.oauth.justauth.source.AuthCustomizeSource \u4e14\u6ce8\u5165 IOC \u5bb9\u5668");
            }
            return authCustomizeSource.getCustomizeRequestClass();
        }
        if (source instanceof AuthGitlabPrivateSource) {
            if (authGitlabPrivateSource == null) {
                throw new RuntimeException("\u5fc5\u987b\u5b9e\u73b0 top.dcenter.ums.security.core.oauth.justauth.source.AuthCustomizeSource \u4e14\u6ce8\u5165 IOC \u5bb9\u5668");
            }
            return authGitlabPrivateSource.getCustomizeRequestClass();
        }
        if (!(source instanceof AuthDefaultSource)) {
            throw new RuntimeException("AuthSource \u5fc5\u987b\u662f me.zhyd.oauth.config.AuthDefaultSource \u6216 top.dcenter.ums.security.core.oauth.justauth.source.AuthCustomizeSource \u5b50\u7c7b");
        }
        String[] splits = ((AuthDefaultSource)source).name().split(FIELD_SEPARATOR);
        String authRequestClassName = AUTH_REQUEST_PACKAGE + JustAuthRequestHolder.toAuthRequestClassName(splits);
        return Class.forName(authRequestClassName);
    }

    private String getServletContextPath() {
        String contextPath;
        try {
            contextPath = Objects.requireNonNull(((AnnotationConfigServletWebServerApplicationContext)this.applicationContext).getServletContext()).getContextPath();
        }
        catch (Exception e) {
            contextPath = Objects.requireNonNull(((WebApplicationContext)this.applicationContext).getServletContext()).getContextPath();
        }
        return contextPath;
    }

    @NonNull
    private static String toAuthRequestClassName(String[] splits) {
        StringBuilder sb = new StringBuilder();
        sb.append(AUTH_REQUEST_PREFIX);
        for (String split : splits) {
            split = split.toLowerCase();
            if (AuthDefaultSource.DINGTALK.name().equalsIgnoreCase(split)) {
                sb.append("DingTalk");
                continue;
            }
            if ("wechat".equalsIgnoreCase(split)) {
                sb.append("WeChat");
                continue;
            }
            if ("enterprise".equalsIgnoreCase(split) && splits.length == 2 && "wechat".equalsIgnoreCase(splits[0])) {
                sb.append("EnterpriseQrcode");
                continue;
            }
            if (split.length() > 1) {
                sb.append(split.substring(0, 1).toUpperCase()).append(split.substring(1));
                continue;
            }
            sb.append(split.toUpperCase());
        }
        sb.append(AUTH_REQUEST_SUFFIX);
        return sb.toString();
    }

    @NonNull
    private static String toProviderId(String[] splits) {
        if (splits.length == 1) {
            return splits[0].trim().toLowerCase();
        }
        StringBuilder sb = new StringBuilder();
        for (String split : splits) {
            if ((split = split.toLowerCase()).length() > 1) {
                sb.append(split.substring(0, 1).toUpperCase()).append(split.substring(1));
                continue;
            }
            sb.append(split.toUpperCase());
        }
        String firstChar = String.valueOf(sb.charAt(0)).toLowerCase();
        sb.replace(0, 1, firstChar);
        return sb.toString();
    }

    static {
        PROVIDER_ID_AUTH_REQUEST_MAP = new ConcurrentHashMap<String, Auth2DefaultRequest>();
        SOURCE_PROVIDER_ID_MAP = new ConcurrentHashMap<AuthSource, String>();
        authCustomizeSource = null;
        authGitlabPrivateSource = null;
    }
}

