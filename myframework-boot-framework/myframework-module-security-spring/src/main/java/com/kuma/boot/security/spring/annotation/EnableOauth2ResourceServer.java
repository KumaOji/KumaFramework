//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.security.spring.annotation;

import com.kuma.boot.security.spring.autoconfigure.JwtDecoderAutoConfiguration;
import com.kuma.boot.security.spring.autoconfigure.Oauth2ResourceAutoConfiguration;
import com.kuma.boot.security.spring.autoconfigure.ReactiveJwtDecoderAutoConfiguration;
import com.kuma.boot.security.spring.autoconfigure.SecurityAccessAutoConfiguration;
import com.kuma.boot.security.spring.autoconfigure.SecurityAuthenticationAutoConfiguration;
import com.kuma.boot.security.spring.autoconfigure.SecurityAuthorizationAutoConfiguration;
import com.kuma.boot.security.spring.autoconfigure.cloud.JwtCloudAutoConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({JwtCloudAutoConfiguration.class, JwtDecoderAutoConfiguration.class, ReactiveJwtDecoderAutoConfiguration.class, SecurityAccessAutoConfiguration.class, SecurityAuthorizationAutoConfiguration.class, Oauth2ResourceAutoConfiguration.class, SecurityAuthenticationAutoConfiguration.class})
public @interface EnableOauth2ResourceServer {
}
