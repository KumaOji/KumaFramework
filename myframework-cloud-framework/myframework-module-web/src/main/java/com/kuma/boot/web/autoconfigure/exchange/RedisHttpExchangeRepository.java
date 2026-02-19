/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.bean.BeanUtils
 *  org.springframework.boot.actuate.web.exchanges.HttpExchange
 *  org.springframework.boot.actuate.web.exchanges.HttpExchange$Principal
 *  org.springframework.boot.actuate.web.exchanges.HttpExchange$Request
 *  org.springframework.boot.actuate.web.exchanges.HttpExchange$Response
 *  org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository
 *  org.springframework.data.redis.core.StringRedisTemplate
 *  org.springframework.stereotype.Component
 *  tools.jackson.core.JacksonException
 *  tools.jackson.databind.json.JsonMapper
 */
package com.kuma.boot.web.autoconfigure.exchange;

import com.kuma.boot.common.utils.bean.BeanUtils;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import org.springframework.boot.actuate.web.exchanges.HttpExchange;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class RedisHttpExchangeRepository
implements HttpExchangeRepository {
    private final StringRedisTemplate stringRedisTemplate;
    private final JsonMapper jsonMapper;

    public RedisHttpExchangeRepository(StringRedisTemplate stringRedisTemplate, JsonMapper jsonMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jsonMapper = jsonMapper;
    }

    public List<HttpExchange> findAll() {
        List list = this.stringRedisTemplate.opsForList().range((Object)"http:request:list", 0L, -1L);
        return list.stream().map(str -> {
            try {
                HttpLog log = (HttpLog)this.jsonMapper.readValue(str, HttpLog.class);
                HttpLog.Request req = log.getRequest();
                HttpExchange.Request request = new HttpExchange.Request(req.getUri(), req.getRemoteAddress(), req.getMethod(), req.getHeaders());
                HttpLog.Response resp = log.getResponse();
                HttpExchange.Response response = new HttpExchange.Response(resp.getStatus(), resp.getHeaders());
                HttpExchange exchange = new HttpExchange(log.getTimestamp().atZone(ZoneId.systemDefault()).toInstant(), request, response, log.getPrincipal(), null, log.getTimeTaken());
                return exchange;
            }
            catch (JacksonException jacksonException) {
                return null;
            }
        }).filter(e -> e != null).toList();
    }

    public void add(HttpExchange httpExchange) {
        try {
            HttpLog log = new HttpLog();
            log.setPrincipal(httpExchange.getPrincipal());
            log.setTimestamp(httpExchange.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDateTime());
            log.setTimeTaken(httpExchange.getTimeTaken());
            HttpLog.Request request = new HttpLog.Request();
            BeanUtils.copyProperties((Object)httpExchange.getRequest(), (Object)request);
            HttpLog.Response response = new HttpLog.Response();
            BeanUtils.copyProperties((Object)httpExchange.getResponse(), (Object)response);
            log.setRequest(request);
            log.setResponse(response);
            this.stringRedisTemplate.opsForList().leftPush((Object)"http:request:list", (Object)this.jsonMapper.writeValueAsString((Object)log));
        }
        catch (JacksonException jacksonException) {
            // empty catch block
        }
    }

    public static class HttpLog {
        private LocalDateTime timestamp;
        private Request request;
        private Response response;
        private HttpExchange.Principal principal;
        private Duration timeTaken;

        public LocalDateTime getTimestamp() {
            return this.timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public Request getRequest() {
            return this.request;
        }

        public void setRequest(Request request) {
            this.request = request;
        }

        public Response getResponse() {
            return this.response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }

        public HttpExchange.Principal getPrincipal() {
            return this.principal;
        }

        public void setPrincipal(HttpExchange.Principal principal) {
            this.principal = principal;
        }

        public Duration getTimeTaken() {
            return this.timeTaken;
        }

        public void setTimeTaken(Duration timeTaken) {
            this.timeTaken = timeTaken;
        }

        public static class Request {
            private URI uri;
            private String remoteAddress;
            private String method;
            private Map<String, List<String>> headers;

            public URI getUri() {
                return this.uri;
            }

            public void setUri(URI uri) {
                this.uri = uri;
            }

            public String getRemoteAddress() {
                return this.remoteAddress;
            }

            public void setRemoteAddress(String remoteAddress) {
                this.remoteAddress = remoteAddress;
            }

            public String getMethod() {
                return this.method;
            }

            public void setMethod(String method) {
                this.method = method;
            }

            public Map<String, List<String>> getHeaders() {
                return this.headers;
            }

            public void setHeaders(Map<String, List<String>> headers) {
                this.headers = headers;
            }
        }

        public static final class Response {
            private int status;
            private Map<String, List<String>> headers;

            public int getStatus() {
                return this.status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public Map<String, List<String>> getHeaders() {
                return this.headers;
            }

            public void setHeaders(Map<String, List<String>> headers) {
                this.headers = headers;
            }
        }
    }
}

