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

package com.kuma.boot.web.support.reactive;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * HttpHeaders 透传
 *
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class HttpHeadersFilterFunction implements ExchangeFilterFunction {
    // @Autowired
    // private MicaHeadersProperties properties;
    // @Nullable
    // @Autowired(required = false)
    // private MicaAccountGetter accountGetter;

    /**
     * 全局透传请求头：X-Real-IP x-forwarded-for 请求和转发的ip
     */
    private static final String[] ALLOW_HEADS = new String[] {"X-Real-IP", "x-forwarded-for"};

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        // return ReactiveRequestContextHolder.getRequest().flatMap(req -> {
        // 	ClientRequest clientRequest = ClientRequest.from(request)
        // 		// 透传 header
        // 		.headers(headers -> filterHeaders(headers, req.getHeaders()))
        // 		.build();
        // 	return next.exchange(clientRequest);
        // });
        return null;
    }

    /**
     * 透传 header
     *
     * @param newHeaders 传递到下层的 header
     * @param oldHeaders 网关上层的 header
     */
    // private void filterHeaders(HttpHeaders newHeaders, HttpHeaders oldHeaders) {
    // 	String accountHeaderName = properties.getAccountHeaderName();
    // 	// 如果配置有 account 读取器
    // 	if (accountGetter != null) {
    // 		String xAccountHeader = accountGetter.get();
    // 		if (StringUtil.isNotBlank(xAccountHeader)) {
    // 			newHeaders.add(accountHeaderName, xAccountHeader);
    // 		}
    // 	}
    //
    // 	List<String> allowHeadsList = new ArrayList<>(Arrays.asList(ALLOW_HEADS));
    // 	// 如果有传递 account header 继续往下层传递
    // 	allowHeadsList.add(accountHeaderName);
    // 	// 配置的下传头
    // 	allowHeadsList.addAll(properties.getAllowed());
    //
    // 	for (String headerName : allowHeadsList) {
    // 		String headerValue = oldHeaders.getFirst(headerName);
    // 		if (StringUtil.isNotBlank(headerValue)) {
    // 			newHeaders.add(headerName, headerValue);
    // 		}
    // 	}
    // }
}
