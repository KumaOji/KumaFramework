package com.kuma.boot.useragent.support;

import com.kuma.boot.useragent.domain.UserAgent;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;

public abstract class AbstractUserAgentConverter<T> implements UserAgentConverter {
   public AbstractUserAgentConverter() {
   }

   public UserAgent convert(@NonNull HttpHeaders headers) {
      String useragent = headers.getFirst("User-Agent");
      UserAgent.UserAgentBuilder builder = UserAgent.builder(useragent);
      T t = (T)this.create(headers);
      return builder.browser(this.browser(t)).browserType(this.browserType(t)).browserVersion(this.browserVersion(t)).os(this.os(t)).osVersion(this.osVersion(t)).deviceType(this.deviceType(t)).deviceName(this.deviceName(t)).deviceBrand(this.deviceBrand(t)).build();
   }

   protected abstract T create(HttpHeaders headers);

   protected abstract String browser(T t);

   protected abstract String browserType(T t);

   protected abstract String browserVersion(T t);

   protected abstract String os(T t);

   protected abstract String osVersion(T t);

   protected abstract String deviceType(T t);

   protected abstract String deviceName(T t);

   protected abstract String deviceBrand(T t);
}
