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

package com.kuma.boot.ip2region.impl;

import com.kuma.boot.ip2region.model.Ip2regionSearcher;
import com.kuma.boot.ip2region.model.IpInfo;
import com.kuma.boot.ip2region.model.IpV6Searcher;
import com.kuma.boot.ip2region.model.Searcher;
import com.kuma.boot.ip2region.autoconfigure.properties.Ip2regionProperties;
import com.kuma.boot.ip2region.utils.IpInfoUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * ip2region 初始化
 *
 * @author dream.lu
 */
public class Ip2regionSearcherImpl implements InitializingBean, DisposableBean, Ip2regionSearcher {
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
            return IpInfoUtil.toIpInfo(searcher.search(ip));
        } catch (IOException e) {
            throw new RuntimeException("ip位置查询错误");
        }
    }

    @Override
    public IpInfo memorySearch(String ip) {
        // 1. ipv4
        String[] ipV4Part = IpInfoUtil.getIpV4Part(ip);
        if (ipV4Part.length == 4) {
            return memorySearch(Searcher.getIpAdder(ipV4Part));
        }
        // 2. 非 ipv6
        if (!ip.contains(":")) {
            throw new IllegalArgumentException("invalid ip address `" + ip + "`");
        }
        // 3. ipv6
        return ipV6Searcher.query(ip);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Resource resource = resourceLoader.getResource(properties.getDbFileLocation());
        try (InputStream inputStream = resource.getInputStream()) {
            this.searcher = Searcher.newWithBuffer(StreamUtils.copyToByteArray(inputStream));
        }
        Resource ipV6Resource = resourceLoader.getResource(properties.getIpv6dbFileLocation());
        try (InputStream inputStream = ipV6Resource.getInputStream()) {
            this.ipV6Searcher = IpV6Searcher.newWithBuffer(StreamUtils.copyToByteArray(inputStream));
        }
    }

    @Override
    public void destroy() throws Exception {
        if (this.searcher != null) {
            this.searcher.close();
        }
    }
}
