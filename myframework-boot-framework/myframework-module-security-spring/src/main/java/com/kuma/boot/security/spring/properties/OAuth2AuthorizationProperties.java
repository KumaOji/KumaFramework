/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.kuma.boot.security.spring.properties;

import com.google.common.base.MoreObjects;
import com.kuma.boot.security.spring.enums.Certificate;
import com.kuma.boot.security.spring.enums.Target;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="kuma.boot.security.oauth2.authorization")
public class OAuth2AuthorizationProperties {
    public static final String PREFIX = "kuma.boot.security.oauth2.authorization";
    private Target validate = Target.LOCAL;
    private Boolean strict = false;
    private Jwk jwk = new Jwk();
    private Matcher matcher = new Matcher();

    public Target getValidate() {
        return this.validate;
    }

    public void setValidate(Target validate) {
        this.validate = validate;
    }

    public Jwk getJwk() {
        return this.jwk;
    }

    public void setJwk(Jwk jwk) {
        this.jwk = jwk;
    }

    public Matcher getMatcher() {
        return this.matcher;
    }

    public void setMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    public Boolean getStrict() {
        return this.strict;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }

    public static class Jwk {
        private Certificate certificate = Certificate.STANDARD;
        private String jksKeyStore = "classpath*:certificate/ttc.jks";
        private String jksKeyPassword = "ttc";
        private String jksStorePassword = "ttc";
        private String jksKeyAlias = "ttc";

        public Certificate getCertificate() {
            return this.certificate;
        }

        public void setCertificate(Certificate certificate) {
            this.certificate = certificate;
        }

        public String getJksKeyStore() {
            return this.jksKeyStore;
        }

        public void setJksKeyStore(String jksKeyStore) {
            this.jksKeyStore = jksKeyStore;
        }

        public String getJksKeyPassword() {
            return this.jksKeyPassword;
        }

        public void setJksKeyPassword(String jksKeyPassword) {
            this.jksKeyPassword = jksKeyPassword;
        }

        public String getJksStorePassword() {
            return this.jksStorePassword;
        }

        public void setJksStorePassword(String jksStorePassword) {
            this.jksStorePassword = jksStorePassword;
        }

        public String getJksKeyAlias() {
            return this.jksKeyAlias;
        }

        public void setJksKeyAlias(String jksKeyAlias) {
            this.jksKeyAlias = jksKeyAlias;
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("certificate", (Object)this.certificate).add("jksKeyStore", (Object)this.jksKeyStore).add("jksKeyPassword", (Object)this.jksKeyPassword).add("jksStorePassword", (Object)this.jksStorePassword).add("jksKeyAlias", (Object)this.jksKeyAlias).toString();
        }
    }

    public static class Matcher {
        private List<String> staticResources;
        private List<String> permitAll;
        private List<String> hasAuthenticated;

        public List<String> getStaticResources() {
            return this.staticResources;
        }

        public void setStaticResources(List<String> staticResources) {
            this.staticResources = staticResources;
        }

        public List<String> getPermitAll() {
            return this.permitAll;
        }

        public void setPermitAll(List<String> permitAll) {
            this.permitAll = permitAll;
        }

        public List<String> getHasAuthenticated() {
            return this.hasAuthenticated;
        }

        public void setHasAuthenticated(List<String> hasAuthenticated) {
            this.hasAuthenticated = hasAuthenticated;
        }
    }
}

