/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.kuma.boot.security.spring.properties;

import com.google.common.base.MoreObjects;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="kuma.boot.security.oauth2.authentication")
public class OAuth2AuthenticationProperties {
    public static final String PREFIX = "kuma.boot.security.oauth2.authentication";
    private SignInFailureLimited signInFailureLimited = new SignInFailureLimited();
    private SignInEndpointLimited signInEndpointLimited = new SignInEndpointLimited();
    private SignInKickOutLimited signInKickOutLimited = new SignInKickOutLimited();
    private FormLogin formLogin = new FormLogin();
    private ExtensionLogin extensionLogin = new ExtensionLogin();

    public ExtensionLogin getExtensionLogin() {
        return this.extensionLogin;
    }

    public void setExtensionLogin(ExtensionLogin extensionLogin) {
        this.extensionLogin = extensionLogin;
    }

    public SignInEndpointLimited getSignInEndpointLimited() {
        return this.signInEndpointLimited;
    }

    public void setSignInEndpointLimited(SignInEndpointLimited signInEndpointLimited) {
        this.signInEndpointLimited = signInEndpointLimited;
    }

    public SignInFailureLimited getSignInFailureLimited() {
        return this.signInFailureLimited;
    }

    public void setSignInFailureLimited(SignInFailureLimited signInFailureLimited) {
        this.signInFailureLimited = signInFailureLimited;
    }

    public SignInKickOutLimited getSignInKickOutLimited() {
        return this.signInKickOutLimited;
    }

    public void setSignInKickOutLimited(SignInKickOutLimited signInKickOutLimited) {
        this.signInKickOutLimited = signInKickOutLimited;
    }

    public FormLogin getFormLogin() {
        return this.formLogin;
    }

    public void setFormLogin(FormLogin formLogin) {
        this.formLogin = formLogin;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("signInEndpointLimited", (Object)this.signInEndpointLimited).add("signInFailureLimited", (Object)this.signInFailureLimited).add("signInKickOutLimited", (Object)this.signInKickOutLimited).toString();
    }

    public static class SignInFailureLimited {
        private Boolean enabled = true;
        private Integer maxTimes = 5;
        private Boolean autoUnlock = true;
        private Duration expire = Duration.ofHours(2L);

        public Boolean getEnabled() {
            return this.enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Integer getMaxTimes() {
            return this.maxTimes;
        }

        public void setMaxTimes(Integer maxTimes) {
            this.maxTimes = maxTimes;
        }

        public Duration getExpire() {
            return this.expire;
        }

        public void setExpire(Duration expire) {
            this.expire = expire;
        }

        public Boolean getAutoUnlock() {
            return this.autoUnlock;
        }

        public void setAutoUnlock(Boolean autoUnlock) {
            this.autoUnlock = autoUnlock;
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("enabled", (Object)this.enabled).add("maxTimes", (Object)this.maxTimes).add("autoUnlock", (Object)this.autoUnlock).add("expire", (Object)this.expire).toString();
        }
    }

    public static class SignInEndpointLimited {
        private Boolean enabled = false;
        private Integer maximum = 1;

        public Boolean getEnabled() {
            return this.enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Integer getMaximum() {
            return this.maximum;
        }

        public void setMaximum(Integer maximum) {
            this.maximum = maximum;
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("enabled", (Object)this.enabled).add("maximum", (Object)this.maximum).toString();
        }
    }

    public static class SignInKickOutLimited {
        private Boolean enabled = true;

        public Boolean getEnabled() {
            return this.enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("enabled", (Object)this.enabled).toString();
        }
    }

    public static class FormLogin {
        private String usernameParameter = "username";
        private String passwordParameter = "password";
        private String rememberMeParameter = "remember-me";
        private String captchaParameter = "captcha";
        private String loginPageUrl = "/login";
        private String failureForwardUrl = this.loginPageUrl + "?error";
        private String successForwardUrl;
        private Boolean closeCaptcha = false;
        private String category = "HUTOOL_GIF";

        public String getUsernameParameter() {
            return this.usernameParameter;
        }

        public void setUsernameParameter(String usernameParameter) {
            this.usernameParameter = usernameParameter;
        }

        public String getPasswordParameter() {
            return this.passwordParameter;
        }

        public void setPasswordParameter(String passwordParameter) {
            this.passwordParameter = passwordParameter;
        }

        public String getRememberMeParameter() {
            return this.rememberMeParameter;
        }

        public void setRememberMeParameter(String rememberMeParameter) {
            this.rememberMeParameter = rememberMeParameter;
        }

        public String getCaptchaParameter() {
            return this.captchaParameter;
        }

        public void setCaptchaParameter(String captchaParameter) {
            this.captchaParameter = captchaParameter;
        }

        public String getLoginPageUrl() {
            return this.loginPageUrl;
        }

        public void setLoginPageUrl(String loginPageUrl) {
            this.loginPageUrl = loginPageUrl;
        }

        public String getFailureForwardUrl() {
            return this.failureForwardUrl;
        }

        public void setFailureForwardUrl(String failureForwardUrl) {
            this.failureForwardUrl = failureForwardUrl;
        }

        public String getSuccessForwardUrl() {
            return this.successForwardUrl;
        }

        public void setSuccessForwardUrl(String successForwardUrl) {
            this.successForwardUrl = successForwardUrl;
        }

        public Boolean getCloseCaptcha() {
            return this.closeCaptcha;
        }

        public void setCloseCaptcha(Boolean closeCaptcha) {
            this.closeCaptcha = closeCaptcha;
        }

        public String getCategory() {
            return this.category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("usernameParameter", (Object)this.usernameParameter).add("passwordParameter", (Object)this.passwordParameter).add("rememberMeParameter", (Object)this.rememberMeParameter).add("captchaParameter", (Object)this.captchaParameter).add("loginPageUrl", (Object)this.loginPageUrl).add("failureForwardUrl", (Object)this.failureForwardUrl).add("successForwardUrl", (Object)this.successForwardUrl).add("closeCaptcha", (Object)this.closeCaptcha).add("category", (Object)this.category).toString();
        }
    }

    public static class ExtensionLogin {
        private AccountLogin accountLogin = new AccountLogin();
        private CaptchaLogin captchaLogin = new CaptchaLogin();
        private EmailLogin emailLogin = new EmailLogin();
        private FaceLogin faceLogin = new FaceLogin();
        private FingerprintLogin fingerprintLogin = new FingerprintLogin();
        private GesturesLogin gesturesLogin = new GesturesLogin();
        private OneClickLogin oneClickLogin = new OneClickLogin();

        public AccountLogin getAccountLogin() {
            return this.accountLogin;
        }

        public void setAccountLogin(AccountLogin accountLogin) {
            this.accountLogin = accountLogin;
        }

        public CaptchaLogin getCaptchaLogin() {
            return this.captchaLogin;
        }

        public void setCaptchaLogin(CaptchaLogin captchaLogin) {
            this.captchaLogin = captchaLogin;
        }

        public EmailLogin getEmailLogin() {
            return this.emailLogin;
        }

        public void setEmailLogin(EmailLogin emailLogin) {
            this.emailLogin = emailLogin;
        }

        public FaceLogin getFaceLogin() {
            return this.faceLogin;
        }

        public void setFaceLogin(FaceLogin faceLogin) {
            this.faceLogin = faceLogin;
        }

        public FingerprintLogin getFingerprintLogin() {
            return this.fingerprintLogin;
        }

        public void setFingerprintLogin(FingerprintLogin fingerprintLogin) {
            this.fingerprintLogin = fingerprintLogin;
        }

        public GesturesLogin getGesturesLogin() {
            return this.gesturesLogin;
        }

        public void setGesturesLogin(GesturesLogin gesturesLogin) {
            this.gesturesLogin = gesturesLogin;
        }

        public OneClickLogin getOneClickLogin() {
            return this.oneClickLogin;
        }

        public void setOneClickLogin(OneClickLogin oneClickLogin) {
            this.oneClickLogin = oneClickLogin;
        }

        public static class AccountLogin {
            private String usernameParameter = "username";
            private String passwordParameter = "password";
            private String typeParameter = "type";
            private String loginUrl = "/login/account";

            public String getUsernameParameter() {
                return this.usernameParameter;
            }

            public void setUsernameParameter(String usernameParameter) {
                this.usernameParameter = usernameParameter;
            }

            public String getPasswordParameter() {
                return this.passwordParameter;
            }

            public void setPasswordParameter(String passwordParameter) {
                this.passwordParameter = passwordParameter;
            }

            public String getTypeParameter() {
                return this.typeParameter;
            }

            public void setTypeParameter(String typeParameter) {
                this.typeParameter = typeParameter;
            }

            public String getLoginUrl() {
                return this.loginUrl;
            }

            public void setLoginUrl(String loginUrl) {
                this.loginUrl = loginUrl;
            }
        }

        public static class CaptchaLogin {
            private String usernameParameter = "username";
            private String passwordParameter = "password";
            private String verificationCodeParameter = "verification_code";
            private String typeParameter = "type";
            private String loginUrl = "/login/captcha";

            public String getUsernameParameter() {
                return this.usernameParameter;
            }

            public void setUsernameParameter(String usernameParameter) {
                this.usernameParameter = usernameParameter;
            }

            public String getPasswordParameter() {
                return this.passwordParameter;
            }

            public void setPasswordParameter(String passwordParameter) {
                this.passwordParameter = passwordParameter;
            }

            public String getVerificationCodeParameter() {
                return this.verificationCodeParameter;
            }

            public void setVerificationCodeParameter(String verificationCodeParameter) {
                this.verificationCodeParameter = verificationCodeParameter;
            }

            public String getTypeParameter() {
                return this.typeParameter;
            }

            public void setTypeParameter(String typeParameter) {
                this.typeParameter = typeParameter;
            }

            public String getLoginUrl() {
                return this.loginUrl;
            }

            public void setLoginUrl(String loginUrl) {
                this.loginUrl = loginUrl;
            }
        }

        public static class EmailLogin {
            private String emailParameter = "email";
            private String emailCodeParameter = "emailCode";
            private String loginUrl = "/login/email";

            public String getEmailParameter() {
                return this.emailParameter;
            }

            public void setEmailParameter(String emailParameter) {
                this.emailParameter = emailParameter;
            }

            public String getEmailCodeParameter() {
                return this.emailCodeParameter;
            }

            public void setEmailCodeParameter(String emailCodeParameter) {
                this.emailCodeParameter = emailCodeParameter;
            }

            public String getLoginUrl() {
                return this.loginUrl;
            }

            public void setLoginUrl(String loginUrl) {
                this.loginUrl = loginUrl;
            }
        }

        public static class FaceLogin {
            private String imgBase64Parameter = "imgBase64";
            private String loginUrl = "/login/face";

            public String getImgBase64Parameter() {
                return this.imgBase64Parameter;
            }

            public void setImgBase64Parameter(String imgBase64Parameter) {
                this.imgBase64Parameter = imgBase64Parameter;
            }

            public String getLoginUrl() {
                return this.loginUrl;
            }

            public void setLoginUrl(String loginUrl) {
                this.loginUrl = loginUrl;
            }
        }

        public static class FingerprintLogin {
            private String usernameParameter = "name";
            private String fingerPrintParameter = "fingerPrint";
            private String loginUrl = "/login/fingerprint";

            public String getUsernameParameter() {
                return this.usernameParameter;
            }

            public void setUsernameParameter(String usernameParameter) {
                this.usernameParameter = usernameParameter;
            }

            public String getFingerPrintParameter() {
                return this.fingerPrintParameter;
            }

            public void setFingerPrintParameter(String fingerPrintParameter) {
                this.fingerPrintParameter = fingerPrintParameter;
            }

            public String getLoginUrl() {
                return this.loginUrl;
            }

            public void setLoginUrl(String loginUrl) {
                this.loginUrl = loginUrl;
            }
        }

        public static class GesturesLogin {
        }

        public static class OneClickLogin {
            private Boolean enable = false;
            private String loginUrl = "/login/oneClick";
            private String tokenParamName = "accessToken";
            private List<String> otherParamNames = new ArrayList<String>();

            public Boolean getEnable() {
                return this.enable;
            }

            public void setEnable(Boolean enable) {
                this.enable = enable;
            }

            public String getLoginUrl() {
                return this.loginUrl;
            }

            public void setLoginUrl(String loginUrl) {
                this.loginUrl = loginUrl;
            }

            public String getTokenParamName() {
                return this.tokenParamName;
            }

            public void setTokenParamName(String tokenParamName) {
                this.tokenParamName = tokenParamName;
            }

            public List<String> getOtherParamNames() {
                return this.otherParamNames;
            }

            public void setOtherParamNames(List<String> otherParamNames) {
                this.otherParamNames = otherParamNames;
            }
        }
    }
}

