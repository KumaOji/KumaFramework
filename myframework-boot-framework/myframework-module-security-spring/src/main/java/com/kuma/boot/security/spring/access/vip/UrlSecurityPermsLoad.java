/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.io.UrlResource
 *  org.springframework.util.AntPathMatcher
 */
package com.kuma.boot.security.spring.access.vip;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.UrlResource;
import org.springframework.util.AntPathMatcher;

public class UrlSecurityPermsLoad
implements InitializingBean {
    private final Map<String, String> urlPerms = new LinkedHashMap<String, String>();
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final RedisRepository redisRepository;

    public UrlSecurityPermsLoad(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    public void afterPropertiesSet() throws Exception {
        this.loadPerm();
    }

    public String findMatchRoles(String url) {
        for (String next : this.urlPerms.keySet()) {
            if (!this.antPathMatcher.match(next, url)) continue;
            return this.urlPerms.get(next);
        }
        return "";
    }

    public void addUrlPerm(String pattern, String expression) {
        this.urlPerms.put(pattern, expression);
    }

    public List<String> findAnonUrls() {
        ArrayList<String> antMatchPatterns = new ArrayList<String>();
        for (Map.Entry<String, String> urlPermEntry : this.urlPerms.entrySet()) {
            String value = urlPermEntry.getValue();
            if (!StringUtils.isNotBlank((CharSequence)value) || !value.contains("anon")) continue;
            antMatchPatterns.add(urlPermEntry.getKey());
        }
        return antMatchPatterns;
    }

    public void loadPerm() {
        try {
            ClassLoader classLoader = UrlSecurityPermsLoad.class.getClassLoader();
            Enumeration<URL> resources = classLoader.getResources("authority.conf");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                UrlResource urlResource = new UrlResource(url);
                InputStream inputStream = urlResource.getInputStream();
                try {
                    List lines = IOUtils.readLines((InputStream)inputStream, (Charset)StandardCharsets.UTF_8);
                    for (String line : lines) {
                        if (StringUtils.isBlank((CharSequence)line) || line.startsWith("#")) continue;
                        String[] splitLine = StringUtils.splitPreserveAllTokens((String)(line = StringUtils.trim((String)line)), (String)"=", (int)2);
                        if (splitLine.length != 2) {
                            LogUtils.warn((String)"\u9519\u8bef\u7684\u6743\u9650\u914d\u7f6e:{}", (Object[])new Object[]{line});
                            continue;
                        }
                        this.urlPerms.put(splitLine[0], splitLine[1]);
                    }
                }
                finally {
                    if (inputStream == null) continue;
                    inputStream.close();
                }
            }
        }
        catch (Exception e) {
            LogUtils.error((Throwable)e, (String)"authority.conf\u4e0d\u5b58\u5728", (Object[])new Object[0]);
            Object object = this.redisRepository.get("lsxxx");
        }
    }

    public Map<String, String> getUrlPerms() {
        return this.urlPerms;
    }
}

