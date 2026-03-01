/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.boot.totp.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.totp.TotpInfo;
import com.kuma.boot.totp.autoconfigure.properties.TotpProperties;
import com.kuma.boot.totp.code.CodeGenerator;
import com.kuma.boot.totp.code.CodeVerifier;
import com.kuma.boot.totp.code.DefaultCodeGenerator;
import com.kuma.boot.totp.code.DefaultCodeVerifier;
import com.kuma.boot.totp.code.HashingAlgorithm;
import com.kuma.boot.totp.qr.QrDataFactory;
import com.kuma.boot.totp.qr.QrGenerator;
import com.kuma.boot.totp.qr.ZxingPngQrGenerator;
import com.kuma.boot.totp.recovery.RecoveryCodeGenerator;
import com.kuma.boot.totp.secret.DefaultSecretGenerator;
import com.kuma.boot.totp.secret.SecretGenerator;
import com.kuma.boot.totp.time.SystemTimeProvider;
import com.kuma.boot.totp.time.TimeProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(value={TotpInfo.class})
@EnableConfigurationProperties(value={TotpProperties.class})
public class TotpAutoConfiguration
implements InitializingBean {
    private TotpProperties props;

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(TotpAutoConfiguration.class, (String)"kuma-boot-starter-totp", (String[])new String[0]);
    }

    @Autowired
    public TotpAutoConfiguration(TotpProperties props) {
        this.props = props;
    }

    @Bean
    @ConditionalOnMissingBean
    public SecretGenerator secretGenerator() {
        int length = this.props.getSecret().getLength();
        return new DefaultSecretGenerator(length);
    }

    @Bean
    @ConditionalOnMissingBean
    public TimeProvider timeProvider() {
        return new SystemTimeProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public HashingAlgorithm hashingAlgorithm() {
        return HashingAlgorithm.SHA1;
    }

    @Bean
    @ConditionalOnMissingBean
    public QrDataFactory qrDataFactory(HashingAlgorithm hashingAlgorithm) {
        return new QrDataFactory(hashingAlgorithm, this.getCodeLength(), this.getTimePeriod());
    }

    @Bean
    @ConditionalOnMissingBean
    public QrGenerator qrGenerator() {
        return new ZxingPngQrGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public CodeGenerator codeGenerator(HashingAlgorithm algorithm) {
        return new DefaultCodeGenerator(algorithm, this.getCodeLength());
    }

    @Bean
    @ConditionalOnMissingBean
    public CodeVerifier codeVerifier(CodeGenerator codeGenerator, TimeProvider timeProvider) {
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        verifier.setTimePeriod(this.getTimePeriod());
        verifier.setAllowedTimePeriodDiscrepancy(this.props.getTime().getDiscrepancy());
        return verifier;
    }

    @Bean
    @ConditionalOnMissingBean
    public RecoveryCodeGenerator recoveryCodeGenerator() {
        return new RecoveryCodeGenerator();
    }

    private int getCodeLength() {
        return this.props.getCode().getLength();
    }

    private int getTimePeriod() {
        return this.props.getTime().getPeriod();
    }
}

