/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.boot.http.client.HttpRedirects
 */
package com.kuma.boot.web.httpexchange;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.http.client.HttpRedirects;

@ConfigurationProperties(value="http-exchange")
public class HttpExchangeProperties
implements InitializingBean {
    public static final String PREFIX = "http-exchange";
    private boolean enabled = true;
    private Set<String> basePackages = new LinkedHashSet<String>();
    private Set<Class<?>> clients = new LinkedHashSet();
    private @Nullable String baseUrl;
    private List<Header> headers = new ArrayList<Header>();
    private List<Channel> channels = new ArrayList<Channel>();
    private boolean beanToQueryEnabled = false;
    private Refresh refresh = new Refresh();
    private @Nullable ClientType clientType;
    private boolean requestMappingSupportEnabled = false;
    private boolean warnUnusedConfigEnabled = true;
    private boolean loadbalancerEnabled = true;
    private boolean httpClientReuseEnabled = true;

    public void afterPropertiesSet() {
        this.merge();
    }

    void merge() {
        for (Channel chan : this.channels) {
            if (this.baseUrl != null && chan.getBaseUrl() == null) {
                chan.setBaseUrl(this.baseUrl);
            }
            if (this.clientType != null && chan.getClientType() == null) {
                chan.setClientType(this.clientType);
            }
            if (chan.getLoadbalancerEnabled() == null) {
                chan.setLoadbalancerEnabled(this.loadbalancerEnabled);
            }
            if (chan.getHttpClientReuseEnabled() == null) {
                chan.setHttpClientReuseEnabled(this.httpClientReuseEnabled);
            }
            LinkedHashMap total = this.headers.stream().collect(Collectors.toMap(Header::key, Header::values, (oldV, newV) -> oldV, LinkedHashMap::new));
            for (Header header : chan.getHeaders()) {
                total.put(header.key(), header.values());
            }
            List<Header> mergedHeaders = total.entrySet().stream().map(e -> new Header((String)e.getKey(), (List)e.getValue())).toList();
            chan.setHeaders(mergedHeaders);
        }
    }

    Channel defaultChannel() {
        return new Channel(null, this.baseUrl, this.headers, this.clientType, null, null, null, this.loadbalancerEnabled, this.httpClientReuseEnabled, null, List.of(), List.of());
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getBasePackages() {
        return this.basePackages;
    }

    public void setBasePackages(Set<String> basePackages) {
        this.basePackages = basePackages;
    }

    public Set<Class<?>> getClients() {
        return this.clients;
    }

    public void setClients(Set<Class<?>> clients) {
        this.clients = clients;
    }

    public @Nullable String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(@Nullable String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<Header> getHeaders() {
        return this.headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public List<Channel> getChannels() {
        return this.channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public boolean isBeanToQueryEnabled() {
        return this.beanToQueryEnabled;
    }

    public void setBeanToQueryEnabled(boolean beanToQueryEnabled) {
        this.beanToQueryEnabled = beanToQueryEnabled;
    }

    public Refresh getRefresh() {
        return this.refresh;
    }

    public void setRefresh(Refresh refresh) {
        this.refresh = refresh;
    }

    public @Nullable ClientType getClientType() {
        return this.clientType;
    }

    public void setClientType(@Nullable ClientType clientType) {
        this.clientType = clientType;
    }

    public boolean isRequestMappingSupportEnabled() {
        return this.requestMappingSupportEnabled;
    }

    public void setRequestMappingSupportEnabled(boolean requestMappingSupportEnabled) {
        this.requestMappingSupportEnabled = requestMappingSupportEnabled;
    }

    public boolean isWarnUnusedConfigEnabled() {
        return this.warnUnusedConfigEnabled;
    }

    public void setWarnUnusedConfigEnabled(boolean warnUnusedConfigEnabled) {
        this.warnUnusedConfigEnabled = warnUnusedConfigEnabled;
    }

    public boolean isLoadbalancerEnabled() {
        return this.loadbalancerEnabled;
    }

    public void setLoadbalancerEnabled(boolean loadbalancerEnabled) {
        this.loadbalancerEnabled = loadbalancerEnabled;
    }

    public boolean isHttpClientReuseEnabled() {
        return this.httpClientReuseEnabled;
    }

    public void setHttpClientReuseEnabled(boolean httpClientReuseEnabled) {
        this.httpClientReuseEnabled = httpClientReuseEnabled;
    }

    public static class Refresh {
        public static final String PREFIX = "http-exchange.refresh";
        private boolean enabled = false;

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class Channel {
        private @Nullable String name;
        private @Nullable String baseUrl;
        private List<Header> headers = new ArrayList<Header>();
        private @Nullable ClientType clientType;
        private @Nullable HttpRedirects redirects;
        private @Nullable Integer connectTimeout;
        private @Nullable Integer readTimeout;
        private @Nullable Boolean loadbalancerEnabled;
        private @Nullable Boolean httpClientReuseEnabled;
        private @Nullable Ssl ssl;
        private List<String> clients = new ArrayList<String>();
        private List<Class<?>> classes = new ArrayList();

        public Channel(@Nullable String name, @Nullable String baseUrl, List<Header> headers, @Nullable ClientType clientType, @Nullable HttpRedirects redirects, @Nullable Integer connectTimeout, @Nullable Integer readTimeout, @Nullable Boolean loadbalancerEnabled, @Nullable Boolean httpClientReuseEnabled, @Nullable Ssl ssl, List<String> clients, List<Class<?>> classes) {
            this.name = name;
            this.baseUrl = baseUrl;
            this.headers = headers;
            this.clientType = clientType;
            this.redirects = redirects;
            this.connectTimeout = connectTimeout;
            this.readTimeout = readTimeout;
            this.loadbalancerEnabled = loadbalancerEnabled;
            this.httpClientReuseEnabled = httpClientReuseEnabled;
            this.ssl = ssl;
            this.clients = clients;
            this.classes = classes;
        }

        public @Nullable String getName() {
            return this.name;
        }

        public void setName(@Nullable String name) {
            this.name = name;
        }

        public @Nullable String getBaseUrl() {
            return this.baseUrl;
        }

        public void setBaseUrl(@Nullable String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public List<Header> getHeaders() {
            return this.headers;
        }

        public void setHeaders(List<Header> headers) {
            this.headers = headers;
        }

        public @Nullable ClientType getClientType() {
            return this.clientType;
        }

        public void setClientType(@Nullable ClientType clientType) {
            this.clientType = clientType;
        }

        public @Nullable HttpRedirects getRedirects() {
            return this.redirects;
        }

        public void setRedirects(@Nullable HttpRedirects redirects) {
            this.redirects = redirects;
        }

        public @Nullable Integer getConnectTimeout() {
            return this.connectTimeout;
        }

        public void setConnectTimeout(@Nullable Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public @Nullable Integer getReadTimeout() {
            return this.readTimeout;
        }

        public void setReadTimeout(@Nullable Integer readTimeout) {
            this.readTimeout = readTimeout;
        }

        public @Nullable Boolean getLoadbalancerEnabled() {
            return this.loadbalancerEnabled;
        }

        public void setLoadbalancerEnabled(@Nullable Boolean loadbalancerEnabled) {
            this.loadbalancerEnabled = loadbalancerEnabled;
        }

        public @Nullable Boolean getHttpClientReuseEnabled() {
            return this.httpClientReuseEnabled;
        }

        public void setHttpClientReuseEnabled(@Nullable Boolean httpClientReuseEnabled) {
            this.httpClientReuseEnabled = httpClientReuseEnabled;
        }

        public @Nullable Ssl getSsl() {
            return this.ssl;
        }

        public void setSsl(@Nullable Ssl ssl) {
            this.ssl = ssl;
        }

        public List<String> getClients() {
            return this.clients;
        }

        public void setClients(List<String> clients) {
            this.clients = clients;
        }

        public List<Class<?>> getClasses() {
            return this.classes;
        }

        public void setClasses(List<Class<?>> classes) {
            this.classes = classes;
        }
    }

    public static enum ClientType {
        REST_CLIENT,
        WEB_CLIENT;

    }

    public record Header(String key, List<String> values) {
        public Header {
            values = List.copyOf(values);
        }
    }

    public record Ssl(String bundle) {
    }
}

