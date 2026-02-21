/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.util.StreamUtils
 */
package com.kuma.boot.ip2region.impl;

import com.kuma.boot.ip2region.autoconfigure.properties.Ip2regionProperties;
import com.kuma.boot.ip2region.model.Ip2regionSearcher;
import com.kuma.boot.ip2region.model.IpInfo;
import com.kuma.boot.ip2region.model.IpV6Searcher;
import com.kuma.boot.ip2region.model.Searcher;
import com.kuma.boot.ip2region.utils.IpInfoUtil;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

public class Ip2regionSearcherImpl
implements InitializingBean,
DisposableBean,
Ip2regionSearcher {
    private final ResourceLoader resourceLoader;
    private final Ip2regionProperties properties;
    private Searcher searcher;
    private IpV6Searcher ipV6Searcher;

    public Ip2regionSearcherImpl(ResourceLoader resourceLoader, Ip2regionProperties properties) {
        this.resourceLoader = resourceLoader;
        this.properties = properties;
    }

    @Override
    public IpInfo memorySearch(long ip) {
        try {
            return IpInfoUtil.toIpInfo(this.searcher.search(ip));
        }
        catch (IOException e) {
            throw new RuntimeException("ip\u4f4d\u7f6e\u67e5\u8be2\u9519\u8bef");
        }
    }

    @Override
    public IpInfo memorySearch(String ip) {
        String[] ipV4Part = IpInfoUtil.getIpV4Part(ip);
        if (ipV4Part.length == 4) {
            return this.memorySearch(Searcher.getIpAdder(ipV4Part));
        }
        if (!ip.contains(":")) {
            throw new IllegalArgumentException("invalid ip address `" + ip + "`");
        }
        return this.ipV6Searcher.query(ip);
    }

    public void afterPropertiesSet() throws Exception {
        Resource resource = this.resourceLoader.getResource(this.properties.getDbFileLocation());
        try (InputStream inputStream = resource.getInputStream();){
            this.searcher = Searcher.newWithBuffer(StreamUtils.copyToByteArray((InputStream)inputStream));
        }
        Resource ipV6Resource = this.resourceLoader.getResource(this.properties.getIpv6dbFileLocation());
        try (InputStream inputStream = ipV6Resource.getInputStream();){
            this.ipV6Searcher = IpV6Searcher.newWithBuffer(StreamUtils.copyToByteArray((InputStream)inputStream));
        }
    }

    public void destroy() throws Exception {
        if (this.searcher != null) {
            this.searcher.close();
        }
    }
}

