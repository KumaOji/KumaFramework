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

package com.kuma.boot.security.spring.autoconfigure.properties;

import static com.kuma.boot.security.spring.authentication.login.extension.email.EmailAuthenticationFilter.KMC_SECURITY_EXTENSIONS_EMAIL_LOGIN_EMAIL_CODE_KEY;
import static com.kuma.boot.security.spring.authentication.login.extension.email.EmailAuthenticationFilter.KMC_SECURITY_EXTENSIONS_EMAIL_LOGIN_EMAIL_KEY;
import static com.kuma.boot.security.spring.authentication.login.extension.email.EmailAuthenticationFilter.KMC_SECURITY_EXTENSIONS_EMAIL_LOGIN_URL;
import static com.kuma.boot.security.spring.authentication.login.extension.face.FaceAuthenticationFilter.KMC_SECURITY_EXTENSIONS_FACE_IMG_BASE64_PARAMETER_KEY;
import static com.kuma.boot.security.spring.authentication.login.extension.face.FaceAuthenticationFilter.KMC_SECURITY_EXTENSIONS_FACE_LOGIN_URL;
import static com.kuma.boot.security.spring.authentication.login.extension.fingerprint.FingerprintAuthenticationFilter.KMC_SECURITY_EXTENSIONS_FINGERPRINT_LOGIN_FINGER_PRINT_URL;
import static com.kuma.boot.security.spring.authentication.login.extension.fingerprint.FingerprintAuthenticationFilter.KMC_SECURITY_EXTENSIONS_FINGERPRINT_LOGIN_NAME_URL;
import static com.kuma.boot.security.spring.authentication.login.extension.fingerprint.FingerprintAuthenticationFilter.KMC_SECURITY_EXTENSIONS_FINGERPRINT_LOGIN_URL;
import static com.kuma.boot.security.spring.constants.SymbolConstants.QUESTION;

import com.google.common.base.MoreObjects;
import com.kuma.boot.security.spring.authentication.login.extension.account.AccountAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.captcha.CaptchaAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.oneClick.OneClickLoginAuthenticationFilter;
import com.kuma.boot.security.spring.authentication.login.extension.oneClick.service.OneClickLoginService;
import com.kuma.boot.security.spring.authentication.login.social.justauth.userdetails.JustAuthUserDetailsRegisterService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;

/**
 * <p>OAuth2 合规性配置参数 </p>
 *
 * @since : 2022/7/7 0:16
 */
@ConfigurationProperties(prefix = OAuth2AuthenticationProperties.PREFIX)
public class OAuth2AuthenticationProperties {

    public static final String PREFIX = "kuma.boot.security.oauth2.authentication";

    /**
     * 开启登录失败限制
     */
    private SignInFailureLimited signInFailureLimited = new SignInFailureLimited();

    /**
     * 同一终端登录限制
     */
    private SignInEndpointLimited signInEndpointLimited = new SignInEndpointLimited();

    /**
     * 账户踢出限制
     */
    private SignInKickOutLimited signInKickOutLimited = new SignInKickOutLimited();

    private FormLogin formLogin = new FormLogin();

    private ExtensionLogin extensionLogin = new ExtensionLogin();

    public ExtensionLogin getExtensionLogin() {
        return extensionLogin;
    }

    public void setExtensionLogin(ExtensionLogin extensionLogin) {
        this.extensionLogin = extensionLogin;
    }

    public SignInEndpointLimited getSignInEndpointLimited() {
        return signInEndpointLimited;
    }

    public void setSignInEndpointLimited(SignInEndpointLimited signInEndpointLimited) {
        this.signInEndpointLimited = signInEndpointLimited;
    }

    public SignInFailureLimited getSignInFailureLimited() {
        return signInFailureLimited;
    }

    public void setSignInFailureLimited(SignInFailureLimited signInFailureLimited) {
        this.signInFailureLimited = signInFailureLimited;
    }

    public SignInKickOutLimited getSignInKickOutLimited() {
        return signInKickOutLimited;
    }

    public void setSignInKickOutLimited(SignInKickOutLimited signInKickOutLimited) {
        this.signInKickOutLimited = signInKickOutLimited;
    }

    public FormLogin getFormLogin() {
        return formLogin;
    }

    public void setFormLogin(FormLogin formLogin) {
        this.formLogin = formLogin;
    }

    public static class SignInFailureLimited {

        /**
         * 是否开启登录失败检测，默认开启
         */
        private Boolean enabled = true;

        /**
         * 允许允许最大失败次数
         */
        private Integer maxTimes = 5;

        /**
         * 是否自动解锁被锁定用户，默认开启
         */
        private Boolean autoUnlock = true;

        /**
         * 记录失败次数的缓存过期时间，默认：2小时。
         */
        private Duration expire = Duration.ofHours(2);

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Integer getMaxTimes() {
            return maxTimes;
        }

        public void setMaxTimes(Integer maxTimes) {
            this.maxTimes = maxTimes;
        }

        public Duration getExpire() {
            return expire;
        }

        public void setExpire(Duration expire) {
            this.expire = expire;
        }

        public Boolean getAutoUnlock() {
            return autoUnlock;
        }

        public void setAutoUnlock(Boolean autoUnlock) {
            this.autoUnlock = autoUnlock;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("enabled", enabled)
                    .add("maxTimes", maxTimes)
                    .add("autoUnlock", autoUnlock)
                    .add("expire", expire)
                    .toString();
        }
    }

    public static class SignInEndpointLimited {

        /**
         * 同一终端登录限制是否开启，默认开启。
         */
        private Boolean enabled = false;

        /**
         * 统一终端，允许同时登录的最大数量
         */
        private Integer maximum = 1;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Integer getMaximum() {
            return maximum;
        }

        public void setMaximum(Integer maximum) {
            this.maximum = maximum;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("enabled", enabled)
                    .add("maximum", maximum)
                    .toString();
        }
    }

    public static class SignInKickOutLimited {

        /**
         * 是否开启 Session 踢出功能，默认开启
         */
        private Boolean enabled = true;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("enabled", enabled).toString();
        }
    }

    public static class FormLogin {

        /**
         * UI 界面用户名标输入框 name 属性值
         */
        private String usernameParameter =
                UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;

        /**
         * UI 界面密码标输入框 name 属性值
         */
        private String passwordParameter =
                UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY;

        /**
         * UI 界面Remember Me name 属性值
         */
        private String rememberMeParameter =
                AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY;

        /**
         * UI 界面验证码 name 属性值
         */
        private String captchaParameter = "captcha";

        /**
         * 登录页面地址
         */
        private String loginPageUrl = DefaultLoginPageGeneratingFilter.DEFAULT_LOGIN_PAGE_URL;

        /**
         * 登录失败重定向地址
         */
        private String failureForwardUrl =
                loginPageUrl + QUESTION + DefaultLoginPageGeneratingFilter.ERROR_PARAMETER_NAME;

        /**
         * 登录成功重定向地址
         */
        private String successForwardUrl;

        /**
         * 关闭验证码显示，默认 false，显示
         */
        private Boolean closeCaptcha = false;

        /**
         * 验证码类别，默认为 Hutool Gif 类型
         */
        private String category = "HUTOOL_GIF";

        public String getUsernameParameter() {
            return usernameParameter;
        }

        public void setUsernameParameter(String usernameParameter) {
            this.usernameParameter = usernameParameter;
        }

        public String getPasswordParameter() {
            return passwordParameter;
        }

        public void setPasswordParameter(String passwordParameter) {
            this.passwordParameter = passwordParameter;
        }

        public String getRememberMeParameter() {
            return rememberMeParameter;
        }

        public void setRememberMeParameter(String rememberMeParameter) {
            this.rememberMeParameter = rememberMeParameter;
        }

        public String getCaptchaParameter() {
            return captchaParameter;
        }

        public void setCaptchaParameter(String captchaParameter) {
            this.captchaParameter = captchaParameter;
        }

        public String getLoginPageUrl() {
            return loginPageUrl;
        }

        public void setLoginPageUrl(String loginPageUrl) {
            this.loginPageUrl = loginPageUrl;
        }

        public String getFailureForwardUrl() {
            return failureForwardUrl;
        }

        public void setFailureForwardUrl(String failureForwardUrl) {
            this.failureForwardUrl = failureForwardUrl;
        }

        public String getSuccessForwardUrl() {
            return successForwardUrl;
        }

        public void setSuccessForwardUrl(String successForwardUrl) {
            this.successForwardUrl = successForwardUrl;
        }

        public Boolean getCloseCaptcha() {
            return closeCaptcha;
        }

        public void setCloseCaptcha(Boolean closeCaptcha) {
            this.closeCaptcha = closeCaptcha;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("usernameParameter", usernameParameter)
                    .add("passwordParameter", passwordParameter)
                    .add("rememberMeParameter", rememberMeParameter)
                    .add("captchaParameter", captchaParameter)
                    .add("loginPageUrl", loginPageUrl)
                    .add("failureForwardUrl", failureForwardUrl)
                    .add("successForwardUrl", successForwardUrl)
                    .add("closeCaptcha", closeCaptcha)
                    .add("category", category)
                    .toString();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("signInEndpointLimited", signInEndpointLimited)
                .add("signInFailureLimited", signInFailureLimited)
                .add("signInKickOutLimited", signInKickOutLimited)
                .toString();
    }

    public static class ExtensionLogin {

        private AccountLogin accountLogin = new AccountLogin();
        private CaptchaLogin captchaLogin = new CaptchaLogin();
        private EmailLogin emailLogin = new EmailLogin();
        private FaceLogin faceLogin = new FaceLogin();
        private FingerprintLogin fingerprintLogin = new FingerprintLogin();
        private GesturesLogin gesturesLogin = new GesturesLogin();
        private OneClickLogin oneClickLogin = new OneClickLogin();

        public static class OneClickLogin {
            /**
             * 一键登录是否开启, 默认 false
             */
            private Boolean enable = false;

            /**
             * 一键登录请求处理 url, 默认 /authentication/one-click
             */
            private String loginUrl =
                    OneClickLoginAuthenticationFilter.KMC_SECURITY_EXTENSIONS_ONE_CLICK_LOGIN_URL;

            /**
             * token 参数名称, 默认: accessToken
             */
            private String tokenParamName =
                    OneClickLoginAuthenticationFilter
                            .KMC_SECURITY_EXTENSIONS_ONE_CLICK_LOGIN_TOKEN_KEY;

            /**
             * 其他请求参数名称列表(包括请求头名称), 此参数会传递到 {@link OneClickLoginService#callback(String, Map)} 与
             * {@link JustAuthUserDetailsRegisterService#registerUser(String, Map)}; 默认为: 空
             */
            private List<String> otherParamNames = new ArrayList<>();

            public Boolean getEnable() {
                return enable;
            }

            public void setEnable(Boolean enable) {
                this.enable = enable;
            }

            public String getLoginUrl() {
                return loginUrl;
            }

            public void setLoginUrl(String loginUrl) {
                this.loginUrl = loginUrl;
            }

            public String getTokenParamName() {
                return tokenParamName;
            }

            public void setTokenParamName(String tokenParamName) {
                this.tokenParamName = tokenParamName;
            }

            public List<String> getOtherParamNames() {
                return otherParamNames;
            }

            public void setOtherParamNames(List<String> otherParamNames) {
                this.otherParamNames = otherParamNames;
            }
        }

        public static class AccountLogin {

            /**
             * UI 界面用户名标输入框 name 属性值
             */
            private String usernameParameter =
                    AccountAuthenticationFilter.KMC_SECURITY_EXTENSIONS_ACCOUNT_LOGIN_USERNAME_KEY;

            /**
             * UI 界面密码标输入框 name 属性值
             */
            private String passwordParameter =
                    AccountAuthenticationFilter.KMC_SECURITY_EXTENSIONS_ACCOUNT_LOGIN_PASSWORD_KEY;

            private String typeParameter =
                    AccountAuthenticationFilter.KMC_SECURITY_EXTENSIONS_ACCOUNT_LOGIN_TYPE_KEY;
            private String loginUrl =
                    AccountAuthenticationFilter.KMC_SECURITY_EXTENSIONS_ACCOUNT_LOGIN_URL;

            public String getUsernameParameter() {
                return usernameParameter;
            }

            public void setUsernameParameter(String usernameParameter) {
                this.usernameParameter = usernameParameter;
            }

            public String getPasswordParameter() {
                return passwordParameter;
            }

            public void setPasswordParameter(String passwordParameter) {
                this.passwordParameter = passwordParameter;
            }

            public String getTypeParameter() {
                return typeParameter;
            }

            public void setTypeParameter(String typeParameter) {
                this.typeParameter = typeParameter;
            }

            public String getLoginUrl() {
                return loginUrl;
            }

            public void setLoginUrl(String loginUrl) {
                this.loginUrl = loginUrl;
            }
        }

        public static class CaptchaLogin {

            private String usernameParameter =
                    CaptchaAuthenticationFilter.KMC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_USERNAME_KEY;
            private String passwordParameter =
                    CaptchaAuthenticationFilter.KMC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_PASSWORD_KEY;
            private String verificationCodeParameter =
                    CaptchaAuthenticationFilter
                            .KMC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_VERIFICATION_CODE_KEY;
            private String typeParameter =
                    CaptchaAuthenticationFilter.KMC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_TYPE_KEY;
            private String loginUrl =
                    CaptchaAuthenticationFilter.KMC_SECURITY_EXTENSIONS_CAPTCHA_LOGIN_URL;

            public String getUsernameParameter() {
                return usernameParameter;
            }

            public void setUsernameParameter(String usernameParameter) {
                this.usernameParameter = usernameParameter;
            }

            public String getPasswordParameter() {
                return passwordParameter;
            }

            public void setPasswordParameter(String passwordParameter) {
                this.passwordParameter = passwordParameter;
            }

            public String getVerificationCodeParameter() {
                return verificationCodeParameter;
            }

            public void setVerificationCodeParameter(String verificationCodeParameter) {
                this.verificationCodeParameter = verificationCodeParameter;
            }

            public String getTypeParameter() {
                return typeParameter;
            }

            public void setTypeParameter(String typeParameter) {
                this.typeParameter = typeParameter;
            }

            public String getLoginUrl() {
                return loginUrl;
            }

            public void setLoginUrl(String loginUrl) {
                this.loginUrl = loginUrl;
            }
        }

        public static class EmailLogin {
            private String emailParameter = KMC_SECURITY_EXTENSIONS_EMAIL_LOGIN_EMAIL_KEY;
            private String emailCodeParameter = KMC_SECURITY_EXTENSIONS_EMAIL_LOGIN_EMAIL_CODE_KEY;
            private String loginUrl = KMC_SECURITY_EXTENSIONS_EMAIL_LOGIN_URL;

            public String getEmailParameter() {
                return emailParameter;
            }

            public void setEmailParameter(String emailParameter) {
                this.emailParameter = emailParameter;
            }

            public String getEmailCodeParameter() {
                return emailCodeParameter;
            }

            public void setEmailCodeParameter(String emailCodeParameter) {
                this.emailCodeParameter = emailCodeParameter;
            }

            public String getLoginUrl() {
                return loginUrl;
            }

            public void setLoginUrl(String loginUrl) {
                this.loginUrl = loginUrl;
            }
        }

        public static class FaceLogin {
            private String imgBase64Parameter =
                    KMC_SECURITY_EXTENSIONS_FACE_IMG_BASE64_PARAMETER_KEY;
            private String loginUrl = KMC_SECURITY_EXTENSIONS_FACE_LOGIN_URL;

            public String getImgBase64Parameter() {
                return imgBase64Parameter;
            }

            public void setImgBase64Parameter(String imgBase64Parameter) {
                this.imgBase64Parameter = imgBase64Parameter;
            }

            public String getLoginUrl() {
                return loginUrl;
            }

            public void setLoginUrl(String loginUrl) {
                this.loginUrl = loginUrl;
            }
        }

        public static class GesturesLogin {}

        public static class FingerprintLogin {
            private String usernameParameter = KMC_SECURITY_EXTENSIONS_FINGERPRINT_LOGIN_NAME_URL;
            private String fingerPrintParameter =
                    KMC_SECURITY_EXTENSIONS_FINGERPRINT_LOGIN_FINGER_PRINT_URL;
            private String loginUrl = KMC_SECURITY_EXTENSIONS_FINGERPRINT_LOGIN_URL;

            public String getUsernameParameter() {
                return usernameParameter;
            }

            public void setUsernameParameter(String usernameParameter) {
                this.usernameParameter = usernameParameter;
            }

            public String getFingerPrintParameter() {
                return fingerPrintParameter;
            }

            public void setFingerPrintParameter(String fingerPrintParameter) {
                this.fingerPrintParameter = fingerPrintParameter;
            }

            public String getLoginUrl() {
                return loginUrl;
            }

            public void setLoginUrl(String loginUrl) {
                this.loginUrl = loginUrl;
            }
        }

        public AccountLogin getAccountLogin() {
            return accountLogin;
        }

        public void setAccountLogin(AccountLogin accountLogin) {
            this.accountLogin = accountLogin;
        }

        public CaptchaLogin getCaptchaLogin() {
            return captchaLogin;
        }

        public void setCaptchaLogin(CaptchaLogin captchaLogin) {
            this.captchaLogin = captchaLogin;
        }

        public EmailLogin getEmailLogin() {
            return emailLogin;
        }

        public void setEmailLogin(EmailLogin emailLogin) {
            this.emailLogin = emailLogin;
        }

        public FaceLogin getFaceLogin() {
            return faceLogin;
        }

        public void setFaceLogin(FaceLogin faceLogin) {
            this.faceLogin = faceLogin;
        }

        public FingerprintLogin getFingerprintLogin() {
            return fingerprintLogin;
        }

        public void setFingerprintLogin(FingerprintLogin fingerprintLogin) {
            this.fingerprintLogin = fingerprintLogin;
        }

        public GesturesLogin getGesturesLogin() {
            return gesturesLogin;
        }

        public void setGesturesLogin(GesturesLogin gesturesLogin) {
            this.gesturesLogin = gesturesLogin;
        }

        public OneClickLogin getOneClickLogin() {
            return oneClickLogin;
        }

        public void setOneClickLogin(OneClickLogin oneClickLogin) {
            this.oneClickLogin = oneClickLogin;
        }
    }
}
