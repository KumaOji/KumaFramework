/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.config.Customizer
 *  org.springframework.security.config.annotation.SecurityConfigurerAdapter
 *  org.springframework.security.config.annotation.web.HttpSecurityBuilder
 *  org.springframework.security.web.DefaultSecurityFilterChain
 */
package com.kuma.boot.security.spring.authentication.login.extension;

import com.kuma.boot.security.spring.authentication.login.extension.account.AccountExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.captcha.CaptchaExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.email.EmailExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.face.FaceExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.fingerprint.FingerprintExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.gestures.GesturesExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.oneClick.OneClickExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.qrcocde.QrcodeExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.sms.SmsExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.wechatminiapp.WechatMiniAppExtensionLoginFilterConfigurer;
import com.kuma.boot.security.spring.authentication.login.extension.wechatmp.WechatMpExtensionLoginFilterConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.DefaultSecurityFilterChain;

public class ExtensionLoginFilterSecurityConfigurer<H extends HttpSecurityBuilder<H>>
extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, H> {
    private AccountExtensionLoginFilterConfigurer<H> accountLoginFilterConfigurer;
    private CaptchaExtensionLoginFilterConfigurer<H> captchaLoginFilterConfigurer;
    private EmailExtensionLoginFilterConfigurer<H> emailLoginFilterConfigurer;
    private FaceExtensionLoginFilterConfigurer<H> faceLoginFilterConfigurer;
    private FingerprintExtensionLoginFilterConfigurer<H> fingerprintLoginFilterConfigurer;
    private GesturesExtensionLoginFilterConfigurer<H> gesturesLoginFilterConfigurer;
    private OneClickExtensionLoginFilterConfigurer<H> oneClickLoginFilterConfigurer;
    private QrcodeExtensionLoginFilterConfigurer<H> qrcodeLoginFilterConfigurer;
    private SmsExtensionLoginFilterConfigurer<H> smsLoginFilterConfigurer;
    private WechatMpExtensionLoginFilterConfigurer<H> wechatMpLoginFilterConfigurer;
    private WechatMiniAppExtensionLoginFilterConfigurer<H> wechatMiniAppLoginFilterConfigurer;

    public EmailExtensionLoginFilterConfigurer<H> emailLogin() {
        return this.lazyInitEmailLoginFilterConfigurer();
    }

    public ExtensionLoginFilterSecurityConfigurer<H> emailLogin(Customizer<EmailExtensionLoginFilterConfigurer<H>> emailLoginConfigurerCustomizer) {
        emailLoginConfigurerCustomizer.customize(this.lazyInitEmailLoginFilterConfigurer());
        return this;
    }

    public QrcodeExtensionLoginFilterConfigurer<H> qrcodeLogin() {
        return this.lazyInitQrcodeLoginFilterConfigurer();
    }

    public ExtensionLoginFilterSecurityConfigurer<H> qrcodeLogin(Customizer<QrcodeExtensionLoginFilterConfigurer<H>> qrcodeLoginConfigurerCustomizer) {
        qrcodeLoginConfigurerCustomizer.customize(this.lazyInitQrcodeLoginFilterConfigurer());
        return this;
    }

    public OneClickExtensionLoginFilterConfigurer<H> oneClickLogin() {
        return this.lazyInitOneClickLoginFilterConfigurer();
    }

    public ExtensionLoginFilterSecurityConfigurer<H> oneClickLogin(Customizer<OneClickExtensionLoginFilterConfigurer<H>> oneClickLoginConfigurerCustomizer) {
        oneClickLoginConfigurerCustomizer.customize(this.lazyInitOneClickLoginFilterConfigurer());
        return this;
    }

    public WechatMpExtensionLoginFilterConfigurer<H> wechatMpLogin() {
        return this.lazyInitMpLoginFilterConfigurer();
    }

    public ExtensionLoginFilterSecurityConfigurer<H> wechatMpLogin(Customizer<WechatMpExtensionLoginFilterConfigurer<H>> wechcatMpLoginConfigurerCustomizer) {
        wechcatMpLoginConfigurerCustomizer.customize(this.lazyInitMpLoginFilterConfigurer());
        return this;
    }

    public GesturesExtensionLoginFilterConfigurer<H> gesturesLogin() {
        return this.lazyInitGesturesLoginFilterConfigurer();
    }

    public ExtensionLoginFilterSecurityConfigurer<H> gesturesLogin(Customizer<GesturesExtensionLoginFilterConfigurer<H>> gesturesLoginConfigurerCustomizer) {
        gesturesLoginConfigurerCustomizer.customize(this.lazyInitGesturesLoginFilterConfigurer());
        return this;
    }

    public FingerprintExtensionLoginFilterConfigurer<H> fingerprintLogin() {
        return this.lazyInitFingerprintLoginFilterConfigurer();
    }

    public ExtensionLoginFilterSecurityConfigurer<H> fingerprintLogin(Customizer<FingerprintExtensionLoginFilterConfigurer<H>> fingerprintLoginConfigurerCustomizer) {
        fingerprintLoginConfigurerCustomizer.customize(this.lazyInitFingerprintLoginFilterConfigurer());
        return this;
    }

    public FaceExtensionLoginFilterConfigurer<H> faceLogin() {
        return this.lazyInitFaceLoginFilterConfigurer();
    }

    public ExtensionLoginFilterSecurityConfigurer<H> faceLogin(Customizer<FaceExtensionLoginFilterConfigurer<H>> faceLoginConfigurerCustomizer) {
        faceLoginConfigurerCustomizer.customize(this.lazyInitFaceLoginFilterConfigurer());
        return this;
    }

    public AccountExtensionLoginFilterConfigurer<H> accountLogin() {
        return this.lazyInitAccountLoginFilterConfigurer();
    }

    public ExtensionLoginFilterSecurityConfigurer<H> accountLogin(Customizer<AccountExtensionLoginFilterConfigurer<H>> accountLoginConfigurerCustomizer) {
        accountLoginConfigurerCustomizer.customize(this.lazyInitAccountLoginFilterConfigurer());
        return this;
    }

    public CaptchaExtensionLoginFilterConfigurer<H> captchaLogin() {
        return this.lazyInitCaptchaLoginFilterConfigurer();
    }

    public ExtensionLoginFilterSecurityConfigurer<H> captchaLogin(Customizer<CaptchaExtensionLoginFilterConfigurer<H>> captchaLoginConfigurerCustomizer) {
        captchaLoginConfigurerCustomizer.customize(this.lazyInitCaptchaLoginFilterConfigurer());
        return this;
    }

    public SmsExtensionLoginFilterConfigurer<H> smsLogin() {
        return this.lazyInitSmsLoginFilterConfigurer();
    }

    public ExtensionLoginFilterSecurityConfigurer<H> smsLogin(Customizer<SmsExtensionLoginFilterConfigurer<H>> smsLoginConfigurerCustomizer) {
        smsLoginConfigurerCustomizer.customize(this.lazyInitSmsLoginFilterConfigurer());
        return this;
    }

    public WechatMiniAppExtensionLoginFilterConfigurer<H> wechatMiniAppLogin() {
        return this.lazyInitMiniAppLoginFilterConfigurer();
    }

    public ExtensionLoginFilterSecurityConfigurer<H> wechatMiniAppLogin(Customizer<WechatMiniAppExtensionLoginFilterConfigurer<H>> wechatMiniAppLoginConfigurerCustomizer) {
        wechatMiniAppLoginConfigurerCustomizer.customize(this.lazyInitMiniAppLoginFilterConfigurer());
        return this;
    }

    public void init(H builder) throws Exception {
        this.init(this.emailLoginFilterConfigurer, builder);
        this.init(this.accountLoginFilterConfigurer, builder);
        this.init(this.captchaLoginFilterConfigurer, builder);
        this.init(this.faceLoginFilterConfigurer, builder);
        this.init(this.fingerprintLoginFilterConfigurer, builder);
        this.init(this.gesturesLoginFilterConfigurer, builder);
        this.init(this.wechatMpLoginFilterConfigurer, builder);
        this.init(this.oneClickLoginFilterConfigurer, builder);
        this.init(this.qrcodeLoginFilterConfigurer, builder);
        this.init(this.smsLoginFilterConfigurer, builder);
        this.init(this.wechatMiniAppLoginFilterConfigurer, builder);
    }

    public void configure(H builder) throws Exception {
        this.configure(this.emailLoginFilterConfigurer, builder);
        this.configure(this.accountLoginFilterConfigurer, builder);
        this.configure(this.captchaLoginFilterConfigurer, builder);
        this.configure(this.faceLoginFilterConfigurer, builder);
        this.configure(this.fingerprintLoginFilterConfigurer, builder);
        this.configure(this.gesturesLoginFilterConfigurer, builder);
        this.configure(this.wechatMpLoginFilterConfigurer, builder);
        this.configure(this.oneClickLoginFilterConfigurer, builder);
        this.configure(this.qrcodeLoginFilterConfigurer, builder);
        this.configure(this.smsLoginFilterConfigurer, builder);
        this.configure(this.wechatMiniAppLoginFilterConfigurer, builder);
    }

    private <E extends AbstractExtensionLoginFilterConfigurer<H, ?, ?, ?>> void init(E e, H builder) {
        if (e != null) {
            e.init(builder);
        }
    }

    private <E extends AbstractExtensionLoginFilterConfigurer<H, ?, ?, ?>> void configure(E e, H builder) throws Exception {
        if (e != null) {
            e.configure(builder);
        }
    }

    private EmailExtensionLoginFilterConfigurer<H> lazyInitEmailLoginFilterConfigurer() {
        if (this.emailLoginFilterConfigurer == null) {
            this.emailLoginFilterConfigurer = new EmailExtensionLoginFilterConfigurer(this);
        }
        return this.emailLoginFilterConfigurer;
    }

    private SmsExtensionLoginFilterConfigurer<H> lazyInitSmsLoginFilterConfigurer() {
        if (this.smsLoginFilterConfigurer == null) {
            this.smsLoginFilterConfigurer = new SmsExtensionLoginFilterConfigurer(this);
        }
        return this.smsLoginFilterConfigurer;
    }

    private WechatMiniAppExtensionLoginFilterConfigurer<H> lazyInitMiniAppLoginFilterConfigurer() {
        if (this.wechatMiniAppLoginFilterConfigurer == null) {
            this.wechatMiniAppLoginFilterConfigurer = new WechatMiniAppExtensionLoginFilterConfigurer(this);
        }
        return this.wechatMiniAppLoginFilterConfigurer;
    }

    private CaptchaExtensionLoginFilterConfigurer<H> lazyInitCaptchaLoginFilterConfigurer() {
        if (this.captchaLoginFilterConfigurer == null) {
            this.captchaLoginFilterConfigurer = new CaptchaExtensionLoginFilterConfigurer(this);
        }
        return this.captchaLoginFilterConfigurer;
    }

    private AccountExtensionLoginFilterConfigurer<H> lazyInitAccountLoginFilterConfigurer() {
        if (this.accountLoginFilterConfigurer == null) {
            this.accountLoginFilterConfigurer = new AccountExtensionLoginFilterConfigurer(this);
        }
        return this.accountLoginFilterConfigurer;
    }

    private FaceExtensionLoginFilterConfigurer<H> lazyInitFaceLoginFilterConfigurer() {
        if (this.faceLoginFilterConfigurer == null) {
            this.faceLoginFilterConfigurer = new FaceExtensionLoginFilterConfigurer(this);
        }
        return this.faceLoginFilterConfigurer;
    }

    private FingerprintExtensionLoginFilterConfigurer<H> lazyInitFingerprintLoginFilterConfigurer() {
        if (this.fingerprintLoginFilterConfigurer == null) {
            this.fingerprintLoginFilterConfigurer = new FingerprintExtensionLoginFilterConfigurer(this);
        }
        return this.fingerprintLoginFilterConfigurer;
    }

    private GesturesExtensionLoginFilterConfigurer<H> lazyInitGesturesLoginFilterConfigurer() {
        if (this.gesturesLoginFilterConfigurer == null) {
            this.gesturesLoginFilterConfigurer = new GesturesExtensionLoginFilterConfigurer(this);
        }
        return this.gesturesLoginFilterConfigurer;
    }

    private WechatMpExtensionLoginFilterConfigurer<H> lazyInitMpLoginFilterConfigurer() {
        if (this.wechatMpLoginFilterConfigurer == null) {
            this.wechatMpLoginFilterConfigurer = new WechatMpExtensionLoginFilterConfigurer(this);
        }
        return this.wechatMpLoginFilterConfigurer;
    }

    private OneClickExtensionLoginFilterConfigurer<H> lazyInitOneClickLoginFilterConfigurer() {
        if (this.oneClickLoginFilterConfigurer == null) {
            this.oneClickLoginFilterConfigurer = new OneClickExtensionLoginFilterConfigurer(this);
        }
        return this.oneClickLoginFilterConfigurer;
    }

    private QrcodeExtensionLoginFilterConfigurer<H> lazyInitQrcodeLoginFilterConfigurer() {
        if (this.qrcodeLoginFilterConfigurer == null) {
            this.qrcodeLoginFilterConfigurer = new QrcodeExtensionLoginFilterConfigurer(this);
        }
        return this.qrcodeLoginFilterConfigurer;
    }
}

