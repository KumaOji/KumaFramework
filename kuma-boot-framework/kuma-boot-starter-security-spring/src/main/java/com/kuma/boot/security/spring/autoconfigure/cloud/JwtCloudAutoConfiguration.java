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

package com.kuma.boot.security.spring.autoconfigure.cloud;

// import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
// import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
// import com.alibaba.cloud.nacos.NacosServiceManager;
// import com.alibaba.nacos.api.naming.listener.NamingEvent;
// import com.alibaba.nacos.api.naming.pojo.Instance;

import com.kuma.boot.common.constant.ServiceNameConstants;
import com.kuma.boot.security.spring.autoconfigure.cloud.JwtUriFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DiscoveryClient.class)
@ConditionalOnBean(DiscoveryClient.class)
public class JwtCloudAutoConfiguration {

    @Bean
    public JwtUriFactory jwtUriFactory(DiscoveryClient discoveryClient) {
        return new JwtUriFactory() {
            @Override
            public String jwkSetUri() {
                return discoveryClient.getServices().stream()
                        .filter(s -> s.contains(ServiceNameConstants.KUMA_CLOUD_AUTH))
                        .flatMap(s -> discoveryClient.getInstances(s).stream())
                        .map(
                                instance ->
                                        String.format(
                                                "http://%s:%s" + "/oauth2/jwks",
                                                instance.getHost(), instance.getPort()))
                        .findFirst()
                        .orElse(null);
            }
        };
    }

    // todo  此类主要是auth服务器ip变化了 通知客户端更新jwtDecoder
    // @Configuration
    // @ConditionalOnMissingBean(value = JwtDecoder.class)
    // @ConditionalOnClass(name = "com.alibaba.cloud.nacos.NacosServiceManager")
    // @ConditionalOnNacosDiscoveryEnabled
    // public static class NimbusJwtDecoderNacosServiceListener implements InitializingBean {
    //
    //	private final NacosServiceManager nacosServiceManager;
    //	private final NacosDiscoveryProperties properties;
    //
    //	private DiscoveryClient discoveryClient;
    //
    //	public NimbusJwtDecoderNacosServiceListener(NacosServiceManager nacosServiceManager,
    //												NacosDiscoveryProperties properties) {
    //		this.nacosServiceManager = nacosServiceManager;
    //		this.properties = properties;
    //	}
    //
    //	@Override
    //	public void afterPropertiesSet() throws Exception {
    //
    //		nacosServiceManager
    //			.getNamingService()
    //			.subscribe(
    //				ServiceName.KUMA_CLOUD_AUTH,
    //				this.properties.getGroup(),
    //				List.of(this.properties.getClusterName()),
    //				event -> {
    //					if (event instanceof NamingEvent) {
    //						List<Instance> instances = ((NamingEvent) event).getInstances();
    //						if (instances.isEmpty()) {
    //							return;
    //						}
    //						Instance instance = instances.get(0);
    //						String jwkSetUri = String.format(
    //							"http://%s:%s" + "/oauth2/jwks", instance.getIp(),
    //							instance.getPort());
    //
    //						NimbusJwtDecoder nimbusJwtDecoder =
    //							NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
    //								.jwsAlgorithm(SignatureAlgorithm.RS256)
    //								.build();
    //						nimbusJwtDecoder.setJwtValidator(JwtValidators.createDefault());
    //						ContextUtils.destroySingletonBean("jwtDecoder");
    //						ContextUtils.registerSingletonBean("jwtDecoder", nimbusJwtDecoder);
    //					}
    //				});
    //	}
    // }
}
