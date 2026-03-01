/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.authentication.login.social.justauth.properties;

import com.kuma.boot.security.spring.authentication.login.social.justauth.filter.login.Auth2LoginAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.social.justauth.service.ConnectionService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails.TemporaryUser;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * 第三方授权登录属性
 *
 * @author YongWu zheng
 * @version V1.0  Created by 2020/10/6 21:01
 */
@SuppressWarnings({"jol"})
@ConfigurationProperties("ums.oauth")
public class JustAuthProperties {

    // =================== 第三方 属性 ===================
    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties github;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties weibo;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties gitee;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties dingtalk;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties baidu;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties coding;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties oschina;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties alipay;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties qq;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties wechatOpen;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties wechatMp;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties taobao;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties google;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties facebook;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties douyin;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties linkedin;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties microsoft;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties mi;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties toutiao;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties teambition;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties renren;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties pinterest;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties stackOverflow;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties huawei;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties wechatEnterprise;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties kujiale;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties gitlab;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties meituan;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties eleme;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties twitter;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties jd;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties aliyun;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties feishu;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties xmly;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties wechatEnterpriseWeb;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties customize;

    /**
     * 字段名称与其所代表的第三方的 providerId 相同.
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties gitlabPrivate;

    // =================== OAuth2 属性 ===================
    /**
     * 抑制反射警告, 支持 JDK11, 默认: false , 在确认 WARNING: An illegal reflective access operation has
     * occurred 安全后, 可以打开此设置, 可以抑制反射警告.
     */
    private Boolean suppressReflectWarning = false;

    /**
     * 第三方授权登录后如未注册用户是否支持自动注册功能, 默认: true<br>
     * {@code https://gitee.com/pcore/just-auth-spring-security-starter/issues/I22KP3}
     */
    private Boolean autoSignUp = true;

    /**
     * 第三方授权登录后如未注册用户不支持自动注册功能, 则跳转到此 url 进行注册逻辑, 此 url 必须开发者自己实现; 默认: /signUp.html; <br> 注意: 当
     * autoSignUp = false 时, 此属性才生效.<br> 例如:<br> 1. 设置值 "/signUp", 则跳转指定到 "/signUp" 进行注册. <br> 2.
     * 想返回自定义 json 数据到前端, 这里要设置 为空 , 在 {@link Auth2LoginAuthenticationFilter} 设置的
     * {@link AuthenticationSuccessHandler} 上处理返回 json; 判断是否为临时用户的条件是:
     * {@link Authentication#getPrincipal()} 是否为 {@link TemporaryUser} 类型.<br>
     */
    private String signUpUrl = "/signUp.html";

    /**
     * 第三方登录回调的域名, 例如：https://localhost 默认为 "http://127.0.0.1"， redirectUrl 直接由
     * {domain}/{servletContextPath}/{redirectUrlPrefix}/{providerId}(ums.oauth.[qq/gitee/weibo])组成
     */
    private String domain = "http://127.0.0.1";

    /**
     * 第三方登录回调处理 url 前缀 ，也就是 RedirectUrl 的前缀, 不包含 ServletContextPath，默认为 /auth2/login.<br><br>
     */
    private String redirectUrlPrefix = "/auth2/login";

    /**
     * 第三方登录授权登录 url 前缀, 不包含 ServletContextPath，默认为 /auth2/authorization.<br><br>
     */
    private String authLoginUrlPrefix = "/auth2/authorization";

    /**
     * 第三方授权登录成功后的默认权限, 多个权限用逗号分开, 默认为: "ROLE_USER"
     */
    private String defaultAuthorities = "ROLE_USER";

    /**
     * 用于第三方授权登录时, 未开启自动注册且用户是第一次授权登录的临时用户密码, 默认为: "".<br> 注意: 生产环境更换密码
     */
    private String temporaryUserPassword = "";

    /**
     * 用于第三方授权登录时, 未开启自动注册且用户是第一次授权登录的临时用户的默认权限, 多个权限用逗号分开, 默认为: "ROLE_TEMPORARY_USER"
     */
    private String temporaryUserAuthorities = "ROLE_TEMPORARY_USER";

    /**
     * 是否支持内置的第三方登录用户表(user_connection) 和 第三方登录 token 表(auth_token). 默认: true. 注意: 如果为 false,
     * 则必须重新实现 {@link ConnectionService} 接口.
     */
    private Boolean enableUserConnectionAndAuthTokenTable = Boolean.TRUE;

    /**
     * 是否支持内置的第三方登录 token 表(auth_token). 默认: true.
     */
    private Boolean enableAuthTokenTable = Boolean.TRUE;

    // =================== refreshToken 定时任务 属性 ===================
    /**
     * A cron-like expression.
     * <pre>
     * 0 * 2 * * ? 分别对应: second/minute/hour/day of month/month/day of week
     * </pre>
     * 默认为: "0 * 2 * * ?", 凌晨 2 点启动定时任务, 支持分布式(分布式 IOC 容器中必须有 {@link RedisConnectionFactory}, 也就是说,
     * 是否分布式执行依据 IOC 容器中是否有 {@link RedisConnectionFactory})
     *
     * @see org.springframework.scheduling.support.CronSequenceGenerator
     */
    private String refreshTokenJobCron = "0 * 2 * * ?";

    /**
     * 是否支持定时刷新 AccessToken 定时任务, 考虑到很多应用都有自己的定时任务应用, 默认: false. {@link RefreshTokenJob} 接口的实现已注入
     * IOC 容器, 方便自定义定时任务接口时调用. <br> 支持分布式(分布式 IOC 容器中必须有 {@link RedisConnectionFactory}, 也就是说,
     * 是否分布式执行依据 IOC 容器中是否有 {@link RedisConnectionFactory})
     */
    private Boolean enableRefreshTokenJob = false;

    /**
     * 定时刷新 accessToken 任务时, 批处理数据库的记录数.<br> 注意: 分布式应用时, 此配置不同服务器配置必须是一样的. batchCount
     * 大小需要根据实际生产环境进行优化
     */
    private Integer batchCount = 1000;

    /**
     * accessToken 的剩余有效期内进行刷新 accessToken, 默认: 24, 单位: 小时.<br> 注意: 需要根据实际生产环境进行优化
     */
    private Integer remainingExpireIn = 24;

    // =================== justAuth 属性 ===================

    @NestedConfigurationProperty
    private com.kuma.boot.security.justauth.justauth.JustAuthProperties justAuth =
            new com.kuma.boot.security.justauth.justauth.JustAuthProperties();

    /**
     * 针对国外服务可以单独设置代理 HttpConfig config = new HttpConfig(); config.setProxy(new
     * Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10080)));
     * config.setTimeout(15000);
     *
     * @since 1.15.5
     */
    @NestedConfigurationProperty private com.kuma.boot.security.spring.authentication.login.social.justauth.properties.HttpConfigProperties proxy = new com.kuma.boot.security.spring.authentication.login.social.justauth.properties.HttpConfigProperties();

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getGithub() {
        return github;
    }

    public void setGithub(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties github) {
        this.github = github;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getWeibo() {
        return weibo;
    }

    public void setWeibo(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties weibo) {
        this.weibo = weibo;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getGitee() {
        return gitee;
    }

    public void setGitee(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties gitee) {
        this.gitee = gitee;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getDingtalk() {
        return dingtalk;
    }

    public void setDingtalk(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties dingtalk) {
        this.dingtalk = dingtalk;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getBaidu() {
        return baidu;
    }

    public void setBaidu(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties baidu) {
        this.baidu = baidu;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getCoding() {
        return coding;
    }

    public void setCoding(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties coding) {
        this.coding = coding;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getOschina() {
        return oschina;
    }

    public void setOschina(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties oschina) {
        this.oschina = oschina;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getAlipay() {
        return alipay;
    }

    public void setAlipay(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties alipay) {
        this.alipay = alipay;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getQq() {
        return qq;
    }

    public void setQq(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties qq) {
        this.qq = qq;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getWechatOpen() {
        return wechatOpen;
    }

    public void setWechatOpen(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties wechatOpen) {
        this.wechatOpen = wechatOpen;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getWechatMp() {
        return wechatMp;
    }

    public void setWechatMp(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties wechatMp) {
        this.wechatMp = wechatMp;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getTaobao() {
        return taobao;
    }

    public void setTaobao(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties taobao) {
        this.taobao = taobao;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getGoogle() {
        return google;
    }

    public void setGoogle(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties google) {
        this.google = google;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getFacebook() {
        return facebook;
    }

    public void setFacebook(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties facebook) {
        this.facebook = facebook;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getDouyin() {
        return douyin;
    }

    public void setDouyin(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties douyin) {
        this.douyin = douyin;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties linkedin) {
        this.linkedin = linkedin;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getMicrosoft() {
        return microsoft;
    }

    public void setMicrosoft(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties microsoft) {
        this.microsoft = microsoft;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getMi() {
        return mi;
    }

    public void setMi(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties mi) {
        this.mi = mi;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getToutiao() {
        return toutiao;
    }

    public void setToutiao(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties toutiao) {
        this.toutiao = toutiao;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getTeambition() {
        return teambition;
    }

    public void setTeambition(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties teambition) {
        this.teambition = teambition;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getRenren() {
        return renren;
    }

    public void setRenren(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties renren) {
        this.renren = renren;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getPinterest() {
        return pinterest;
    }

    public void setPinterest(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties pinterest) {
        this.pinterest = pinterest;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getStackOverflow() {
        return stackOverflow;
    }

    public void setStackOverflow(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties stackOverflow) {
        this.stackOverflow = stackOverflow;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getHuawei() {
        return huawei;
    }

    public void setHuawei(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties huawei) {
        this.huawei = huawei;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getWechatEnterprise() {
        return wechatEnterprise;
    }

    public void setWechatEnterprise(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties wechatEnterprise) {
        this.wechatEnterprise = wechatEnterprise;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getKujiale() {
        return kujiale;
    }

    public void setKujiale(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties kujiale) {
        this.kujiale = kujiale;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getGitlab() {
        return gitlab;
    }

    public void setGitlab(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties gitlab) {
        this.gitlab = gitlab;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getMeituan() {
        return meituan;
    }

    public void setMeituan(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties meituan) {
        this.meituan = meituan;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getEleme() {
        return eleme;
    }

    public void setEleme(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties eleme) {
        this.eleme = eleme;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getTwitter() {
        return twitter;
    }

    public void setTwitter(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties twitter) {
        this.twitter = twitter;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getJd() {
        return jd;
    }

    public void setJd(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties jd) {
        this.jd = jd;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getAliyun() {
        return aliyun;
    }

    public void setAliyun(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties aliyun) {
        this.aliyun = aliyun;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getFeishu() {
        return feishu;
    }

    public void setFeishu(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties feishu) {
        this.feishu = feishu;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getXmly() {
        return xmly;
    }

    public void setXmly(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties xmly) {
        this.xmly = xmly;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getWechatEnterpriseWeb() {
        return wechatEnterpriseWeb;
    }

    public void setWechatEnterpriseWeb(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties wechatEnterpriseWeb) {
        this.wechatEnterpriseWeb = wechatEnterpriseWeb;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getCustomize() {
        return customize;
    }

    public void setCustomize(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties customize) {
        this.customize = customize;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties getGitlabPrivate() {
        return gitlabPrivate;
    }

    public void setGitlabPrivate(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.BaseAuth2Properties gitlabPrivate) {
        this.gitlabPrivate = gitlabPrivate;
    }

    public Boolean getSuppressReflectWarning() {
        return suppressReflectWarning;
    }

    public void setSuppressReflectWarning(Boolean suppressReflectWarning) {
        this.suppressReflectWarning = suppressReflectWarning;
    }

    public Boolean getAutoSignUp() {
        return autoSignUp;
    }

    public void setAutoSignUp(Boolean autoSignUp) {
        this.autoSignUp = autoSignUp;
    }

    public String getSignUpUrl() {
        return signUpUrl;
    }

    public void setSignUpUrl(String signUpUrl) {
        this.signUpUrl = signUpUrl;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getRedirectUrlPrefix() {
        return redirectUrlPrefix;
    }

    public void setRedirectUrlPrefix(String redirectUrlPrefix) {
        this.redirectUrlPrefix = redirectUrlPrefix;
    }

    public String getAuthLoginUrlPrefix() {
        return authLoginUrlPrefix;
    }

    public void setAuthLoginUrlPrefix(String authLoginUrlPrefix) {
        this.authLoginUrlPrefix = authLoginUrlPrefix;
    }

    public String getDefaultAuthorities() {
        return defaultAuthorities;
    }

    public void setDefaultAuthorities(String defaultAuthorities) {
        this.defaultAuthorities = defaultAuthorities;
    }

    public String getTemporaryUserPassword() {
        return temporaryUserPassword;
    }

    public void setTemporaryUserPassword(String temporaryUserPassword) {
        this.temporaryUserPassword = temporaryUserPassword;
    }

    public String getTemporaryUserAuthorities() {
        return temporaryUserAuthorities;
    }

    public void setTemporaryUserAuthorities(String temporaryUserAuthorities) {
        this.temporaryUserAuthorities = temporaryUserAuthorities;
    }

    public Boolean getEnableUserConnectionAndAuthTokenTable() {
        return enableUserConnectionAndAuthTokenTable;
    }

    public void setEnableUserConnectionAndAuthTokenTable(
            Boolean enableUserConnectionAndAuthTokenTable) {
        this.enableUserConnectionAndAuthTokenTable = enableUserConnectionAndAuthTokenTable;
    }

    public Boolean getEnableAuthTokenTable() {
        return enableAuthTokenTable;
    }

    public void setEnableAuthTokenTable(Boolean enableAuthTokenTable) {
        this.enableAuthTokenTable = enableAuthTokenTable;
    }

    public String getRefreshTokenJobCron() {
        return refreshTokenJobCron;
    }

    public void setRefreshTokenJobCron(String refreshTokenJobCron) {
        this.refreshTokenJobCron = refreshTokenJobCron;
    }

    public Boolean getEnableRefreshTokenJob() {
        return enableRefreshTokenJob;
    }

    public void setEnableRefreshTokenJob(Boolean enableRefreshTokenJob) {
        this.enableRefreshTokenJob = enableRefreshTokenJob;
    }

    public Integer getBatchCount() {
        return batchCount;
    }

    public void setBatchCount(Integer batchCount) {
        this.batchCount = batchCount;
    }

    public Integer getRemainingExpireIn() {
        return remainingExpireIn;
    }

    public void setRemainingExpireIn(Integer remainingExpireIn) {
        this.remainingExpireIn = remainingExpireIn;
    }

    public com.kuma.boot.security.justauth.justauth.JustAuthProperties getJustAuth() {
        return justAuth;
    }

    public void setJustAuth(
            com.kuma.boot.security.justauth.justauth.JustAuthProperties justAuth) {
        this.justAuth = justAuth;
    }

    public com.kuma.boot.security.spring.authentication.login.social.justauth.properties.HttpConfigProperties getProxy() {
        return proxy;
    }

    public void setProxy(com.kuma.boot.security.spring.authentication.login.social.justauth.properties.HttpConfigProperties proxy) {
        this.proxy = proxy;
    }
}
