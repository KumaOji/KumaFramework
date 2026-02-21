/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.lionsoul.ip2region.xdb.LongByteArray
 *  org.lionsoul.ip2region.xdb.Searcher
 *  org.lionsoul.ip2region.xdb.Version
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication$Type
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 *  org.springframework.web.servlet.config.annotation.WebMvcConfigurer
 */
package com.kuma.boot.ip2region.ip2region;

import com.kuma.boot.ip2region.ip2region.support.IPMethodArgumentResolver;
import com.kuma.boot.ip2region.ip2region.support.Ip2RegionSearch;
import com.kuma.boot.ip2region.ip2region.support.RequestIPMethodArgumentResolver;
import java.io.IOException;
import java.util.List;
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

@AutoConfiguration
@EnableConfigurationProperties(value={Ip2RegionProperties.class})
@ConditionalOnProperty(prefix="ip2region", name={"enabled"}, havingValue="true")
public class Ip2regionAutoConfiguration {
    @Bean
    public Ip2RegionSearch ip2RegionSearch(Ip2RegionProperties properties) throws IOException {
        LongByteArray longByteArray = Searcher.loadContentFromFile((String)properties.getFilePath());
        Searcher searcher = Searcher.newWithBuffer((Version)Version.IPv4, (LongByteArray)longByteArray);
        return new Ip2RegionSearch(searcher);
    }

    @AutoConfiguration
    @ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.SERVLET)
    public static class WebMvcIp2regionAutoConfiguration
    implements WebMvcConfigurer {
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new IPMethodArgumentResolver());
            resolvers.add(new RequestIPMethodArgumentResolver());
        }
    }
}

