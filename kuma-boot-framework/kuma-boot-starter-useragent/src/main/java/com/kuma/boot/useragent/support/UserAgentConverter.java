package com.kuma.boot.useragent.support;

import com.kuma.boot.useragent.domain.UserAgent;
import org.springframework.http.HttpHeaders;

public interface UserAgentConverter {
   UserAgent convert(HttpHeaders headers);
}
