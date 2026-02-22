/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.security.justauth.justauth.JustAuthProperties
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.boot.context.properties.NestedConfigurationProperty
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(value="ums.oauth")
public class JustAuthProperties {
    @NestedConfigurationProperty
    private BaseAuth2Properties github;
    @NestedConfigurationProperty
    private BaseAuth2Properties weibo;
    @NestedConfigurationProperty
    private BaseAuth2Properties gitee;
    @NestedConfigurationProperty
    private BaseAuth2Properties dingtalk;
    @NestedConfigurationProperty
    private BaseAuth2Properties baidu;
    @NestedConfigurationProperty
    private BaseAuth2Properties coding;
    @NestedConfigurationProperty
    private BaseAuth2Properties oschina;
    @NestedConfigurationProperty
    private BaseAuth2Properties alipay;
    @NestedConfigurationProperty
    private BaseAuth2Properties qq;
    @NestedConfigurationProperty
    private BaseAuth2Properties wechatOpen;
    @NestedConfigurationProperty
    private BaseAuth2Properties wechatMp;
    @NestedConfigurationProperty
    private BaseAuth2Properties taobao;
    @NestedConfigurationProperty
    private BaseAuth2Properties google;
    @NestedConfigurationProperty
    private BaseAuth2Properties facebook;
    @NestedConfigurationProperty
    private BaseAuth2Properties douyin;
    @NestedConfigurationProperty
    private BaseAuth2Properties linkedin;
    @NestedConfigurationProperty
    private BaseAuth2Properties microsoft;
    @NestedConfigurationProperty
    private BaseAuth2Properties mi;
    @NestedConfigurationProperty
    private BaseAuth2Properties toutiao;
    @NestedConfigurationProperty
    private BaseAuth2Properties teambition;
    @NestedConfigurationProperty
    private BaseAuth2Properties renren;
    @NestedConfigurationProperty
    private BaseAuth2Properties pinterest;
    @NestedConfigurationProperty
    private BaseAuth2Properties stackOverflow;
    @NestedConfigurationProperty
    private BaseAuth2Properties huawei;
    @NestedConfigurationProperty
    private BaseAuth2Properties wechatEnterprise;
    @NestedConfigurationProperty
    private BaseAuth2Properties kujiale;
    @NestedConfigurationProperty
    private BaseAuth2Properties gitlab;
    @NestedConfigurationProperty
    private BaseAuth2Properties meituan;
    @NestedConfigurationProperty
    private BaseAuth2Properties eleme;
    @NestedConfigurationProperty
    private BaseAuth2Properties twitter;
    @NestedConfigurationProperty
    private BaseAuth2Properties jd;
    @NestedConfigurationProperty
    private BaseAuth2Properties aliyun;
    @NestedConfigurationProperty
    private BaseAuth2Properties feishu;
    @NestedConfigurationProperty
    private BaseAuth2Properties xmly;
    @NestedConfigurationProperty
    private BaseAuth2Properties wechatEnterpriseWeb;
    @NestedConfigurationProperty
    private BaseAuth2Properties customize;
    @NestedConfigurationProperty
    private BaseAuth2Properties gitlabPrivate;
    private Boolean suppressReflectWarning = false;
    private Boolean autoSignUp = true;
    private String signUpUrl = "/signUp.html";
    private String domain = "http://127.0.0.1";
    private String redirectUrlPrefix = "/auth2/login";
    private String authLoginUrlPrefix = "/auth2/authorization";
    private String defaultAuthorities = "ROLE_USER";
    private String temporaryUserPassword = "";
    private String temporaryUserAuthorities = "ROLE_TEMPORARY_USER";
    private Boolean enableUserConnectionAndAuthTokenTable = Boolean.TRUE;
    private Boolean enableAuthTokenTable = Boolean.TRUE;
    private String refreshTokenJobCron = "0 * 2 * * ?";
    private Boolean enableRefreshTokenJob = false;
    private Integer batchCount = 1000;
    private Integer remainingExpireIn = 24;
    @NestedConfigurationProperty
    private com.kuma.boot.security.justauth.justauth.JustAuthProperties justAuth = new com.kuma.boot.security.justauth.justauth.JustAuthProperties();
    @NestedConfigurationProperty
    private HttpConfigProperties proxy = new HttpConfigProperties();

    public BaseAuth2Properties getGithub() {
        return this.github;
    }

    public void setGithub(BaseAuth2Properties github) {
        this.github = github;
    }

    public BaseAuth2Properties getWeibo() {
        return this.weibo;
    }

    public void setWeibo(BaseAuth2Properties weibo) {
        this.weibo = weibo;
    }

    public BaseAuth2Properties getGitee() {
        return this.gitee;
    }

    public void setGitee(BaseAuth2Properties gitee) {
        this.gitee = gitee;
    }

    public BaseAuth2Properties getDingtalk() {
        return this.dingtalk;
    }

    public void setDingtalk(BaseAuth2Properties dingtalk) {
        this.dingtalk = dingtalk;
    }

    public BaseAuth2Properties getBaidu() {
        return this.baidu;
    }

    public void setBaidu(BaseAuth2Properties baidu) {
        this.baidu = baidu;
    }

    public BaseAuth2Properties getCoding() {
        return this.coding;
    }

    public void setCoding(BaseAuth2Properties coding) {
        this.coding = coding;
    }

    public BaseAuth2Properties getOschina() {
        return this.oschina;
    }

    public void setOschina(BaseAuth2Properties oschina) {
        this.oschina = oschina;
    }

    public BaseAuth2Properties getAlipay() {
        return this.alipay;
    }

    public void setAlipay(BaseAuth2Properties alipay) {
        this.alipay = alipay;
    }

    public BaseAuth2Properties getQq() {
        return this.qq;
    }

    public void setQq(BaseAuth2Properties qq) {
        this.qq = qq;
    }

    public BaseAuth2Properties getWechatOpen() {
        return this.wechatOpen;
    }

    public void setWechatOpen(BaseAuth2Properties wechatOpen) {
        this.wechatOpen = wechatOpen;
    }

    public BaseAuth2Properties getWechatMp() {
        return this.wechatMp;
    }

    public void setWechatMp(BaseAuth2Properties wechatMp) {
        this.wechatMp = wechatMp;
    }

    public BaseAuth2Properties getTaobao() {
        return this.taobao;
    }

    public void setTaobao(BaseAuth2Properties taobao) {
        this.taobao = taobao;
    }

    public BaseAuth2Properties getGoogle() {
        return this.google;
    }

    public void setGoogle(BaseAuth2Properties google) {
        this.google = google;
    }

    public BaseAuth2Properties getFacebook() {
        return this.facebook;
    }

    public void setFacebook(BaseAuth2Properties facebook) {
        this.facebook = facebook;
    }

    public BaseAuth2Properties getDouyin() {
        return this.douyin;
    }

    public void setDouyin(BaseAuth2Properties douyin) {
        this.douyin = douyin;
    }

    public BaseAuth2Properties getLinkedin() {
        return this.linkedin;
    }

    public void setLinkedin(BaseAuth2Properties linkedin) {
        this.linkedin = linkedin;
    }

    public BaseAuth2Properties getMicrosoft() {
        return this.microsoft;
    }

    public void setMicrosoft(BaseAuth2Properties microsoft) {
        this.microsoft = microsoft;
    }

    public BaseAuth2Properties getMi() {
        return this.mi;
    }

    public void setMi(BaseAuth2Properties mi) {
        this.mi = mi;
    }

    public BaseAuth2Properties getToutiao() {
        return this.toutiao;
    }

    public void setToutiao(BaseAuth2Properties toutiao) {
        this.toutiao = toutiao;
    }

    public BaseAuth2Properties getTeambition() {
        return this.teambition;
    }

    public void setTeambition(BaseAuth2Properties teambition) {
        this.teambition = teambition;
    }

    public BaseAuth2Properties getRenren() {
        return this.renren;
    }

    public void setRenren(BaseAuth2Properties renren) {
        this.renren = renren;
    }

    public BaseAuth2Properties getPinterest() {
        return this.pinterest;
    }

    public void setPinterest(BaseAuth2Properties pinterest) {
        this.pinterest = pinterest;
    }

    public BaseAuth2Properties getStackOverflow() {
        return this.stackOverflow;
    }

    public void setStackOverflow(BaseAuth2Properties stackOverflow) {
        this.stackOverflow = stackOverflow;
    }

    public BaseAuth2Properties getHuawei() {
        return this.huawei;
    }

    public void setHuawei(BaseAuth2Properties huawei) {
        this.huawei = huawei;
    }

    public BaseAuth2Properties getWechatEnterprise() {
        return this.wechatEnterprise;
    }

    public void setWechatEnterprise(BaseAuth2Properties wechatEnterprise) {
        this.wechatEnterprise = wechatEnterprise;
    }

    public BaseAuth2Properties getKujiale() {
        return this.kujiale;
    }

    public void setKujiale(BaseAuth2Properties kujiale) {
        this.kujiale = kujiale;
    }

    public BaseAuth2Properties getGitlab() {
        return this.gitlab;
    }

    public void setGitlab(BaseAuth2Properties gitlab) {
        this.gitlab = gitlab;
    }

    public BaseAuth2Properties getMeituan() {
        return this.meituan;
    }

    public void setMeituan(BaseAuth2Properties meituan) {
        this.meituan = meituan;
    }

    public BaseAuth2Properties getEleme() {
        return this.eleme;
    }

    public void setEleme(BaseAuth2Properties eleme) {
        this.eleme = eleme;
    }

    public BaseAuth2Properties getTwitter() {
        return this.twitter;
    }

    public void setTwitter(BaseAuth2Properties twitter) {
        this.twitter = twitter;
    }

    public BaseAuth2Properties getJd() {
        return this.jd;
    }

    public void setJd(BaseAuth2Properties jd) {
        this.jd = jd;
    }

    public BaseAuth2Properties getAliyun() {
        return this.aliyun;
    }

    public void setAliyun(BaseAuth2Properties aliyun) {
        this.aliyun = aliyun;
    }

    public BaseAuth2Properties getFeishu() {
        return this.feishu;
    }

    public void setFeishu(BaseAuth2Properties feishu) {
        this.feishu = feishu;
    }

    public BaseAuth2Properties getXmly() {
        return this.xmly;
    }

    public void setXmly(BaseAuth2Properties xmly) {
        this.xmly = xmly;
    }

    public BaseAuth2Properties getWechatEnterpriseWeb() {
        return this.wechatEnterpriseWeb;
    }

    public void setWechatEnterpriseWeb(BaseAuth2Properties wechatEnterpriseWeb) {
        this.wechatEnterpriseWeb = wechatEnterpriseWeb;
    }

    public BaseAuth2Properties getCustomize() {
        return this.customize;
    }

    public void setCustomize(BaseAuth2Properties customize) {
        this.customize = customize;
    }

    public BaseAuth2Properties getGitlabPrivate() {
        return this.gitlabPrivate;
    }

    public void setGitlabPrivate(BaseAuth2Properties gitlabPrivate) {
        this.gitlabPrivate = gitlabPrivate;
    }

    public Boolean getSuppressReflectWarning() {
        return this.suppressReflectWarning;
    }

    public void setSuppressReflectWarning(Boolean suppressReflectWarning) {
        this.suppressReflectWarning = suppressReflectWarning;
    }

    public Boolean getAutoSignUp() {
        return this.autoSignUp;
    }

    public void setAutoSignUp(Boolean autoSignUp) {
        this.autoSignUp = autoSignUp;
    }

    public String getSignUpUrl() {
        return this.signUpUrl;
    }

    public void setSignUpUrl(String signUpUrl) {
        this.signUpUrl = signUpUrl;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getRedirectUrlPrefix() {
        return this.redirectUrlPrefix;
    }

    public void setRedirectUrlPrefix(String redirectUrlPrefix) {
        this.redirectUrlPrefix = redirectUrlPrefix;
    }

    public String getAuthLoginUrlPrefix() {
        return this.authLoginUrlPrefix;
    }

    public void setAuthLoginUrlPrefix(String authLoginUrlPrefix) {
        this.authLoginUrlPrefix = authLoginUrlPrefix;
    }

    public String getDefaultAuthorities() {
        return this.defaultAuthorities;
    }

    public void setDefaultAuthorities(String defaultAuthorities) {
        this.defaultAuthorities = defaultAuthorities;
    }

    public String getTemporaryUserPassword() {
        return this.temporaryUserPassword;
    }

    public void setTemporaryUserPassword(String temporaryUserPassword) {
        this.temporaryUserPassword = temporaryUserPassword;
    }

    public String getTemporaryUserAuthorities() {
        return this.temporaryUserAuthorities;
    }

    public void setTemporaryUserAuthorities(String temporaryUserAuthorities) {
        this.temporaryUserAuthorities = temporaryUserAuthorities;
    }

    public Boolean getEnableUserConnectionAndAuthTokenTable() {
        return this.enableUserConnectionAndAuthTokenTable;
    }

    public void setEnableUserConnectionAndAuthTokenTable(Boolean enableUserConnectionAndAuthTokenTable) {
        this.enableUserConnectionAndAuthTokenTable = enableUserConnectionAndAuthTokenTable;
    }

    public Boolean getEnableAuthTokenTable() {
        return this.enableAuthTokenTable;
    }

    public void setEnableAuthTokenTable(Boolean enableAuthTokenTable) {
        this.enableAuthTokenTable = enableAuthTokenTable;
    }

    public String getRefreshTokenJobCron() {
        return this.refreshTokenJobCron;
    }

    public void setRefreshTokenJobCron(String refreshTokenJobCron) {
        this.refreshTokenJobCron = refreshTokenJobCron;
    }

    public Boolean getEnableRefreshTokenJob() {
        return this.enableRefreshTokenJob;
    }

    public void setEnableRefreshTokenJob(Boolean enableRefreshTokenJob) {
        this.enableRefreshTokenJob = enableRefreshTokenJob;
    }

    public Integer getBatchCount() {
        return this.batchCount;
    }

    public void setBatchCount(Integer batchCount) {
        this.batchCount = batchCount;
    }

    public Integer getRemainingExpireIn() {
        return this.remainingExpireIn;
    }

    public void setRemainingExpireIn(Integer remainingExpireIn) {
        this.remainingExpireIn = remainingExpireIn;
    }

    public com.kuma.boot.security.justauth.justauth.JustAuthProperties getJustAuth() {
        return this.justAuth;
    }

    public void setJustAuth(com.kuma.boot.security.justauth.justauth.JustAuthProperties justAuth) {
        this.justAuth = justAuth;
    }

    public HttpConfigProperties getProxy() {
        return this.proxy;
    }

    public void setProxy(HttpConfigProperties proxy) {
        this.proxy = proxy;
    }
}

