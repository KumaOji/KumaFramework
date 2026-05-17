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

package com.kuma.boot.ip2region.ip2region;

import com.kuma.boot.ip2region.ip2region.support.IPMethodArgumentResolver;
import com.kuma.boot.ip2region.ip2region.support.Ip2RegionSearch;
import com.kuma.boot.ip2region.ip2region.support.RequestIPMethodArgumentResolver;
import org.lionsoul.ip2region.xdb.LongByteArray;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.List;

/**
 * The type Ip 2 region auto configuration.
 */
@AutoConfiguration
@EnableConfigurationProperties(Ip2RegionProperties.class)
@ConditionalOnProperty(prefix = Ip2RegionProperties.PREFIX, name = "enabled", havingValue = "true")
public class Ip2regionAutoConfiguration {

    /**
     * Ip 2 region search ip 2 region search.
     *
     * @param properties the properties
     * @return the ip 2 region search
     */
    @Bean
    public Ip2RegionSearch ip2RegionSearch(Ip2RegionProperties properties) throws IOException {
//        Resource resource = properties.getFileResource();
//        byte[] bytes = FileUtils.copyToByteArray(resource.getInputStream());
        LongByteArray longByteArray = Searcher.loadContentFromFile(properties.getFilePath());
        Searcher searcher = Searcher.newWithBuffer(Version.IPv4,longByteArray);
        return new Ip2RegionSearch(searcher);
    }

    /**
     * The type Web mvc ip 2 region auto configuration.
     */
    @AutoConfiguration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public static class WebMvcIp2regionAutoConfiguration implements WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new IPMethodArgumentResolver());
            resolvers.add(new RequestIPMethodArgumentResolver());
        }
    }
}
