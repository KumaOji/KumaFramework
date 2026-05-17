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

package com.kuma.boot.web.autoconfigure.exchange;

import com.kuma.boot.core.utils.bean.BeanUtils;
import org.springframework.boot.actuate.web.exchanges.HttpExchange;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Component
public class RedisHttpExchangeRepository implements HttpExchangeRepository {
    private final StringRedisTemplate stringRedisTemplate;
    private final JsonMapper jsonMapper;

    public RedisHttpExchangeRepository(
            StringRedisTemplate stringRedisTemplate, JsonMapper jsonMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public List<HttpExchange> findAll() {
        List<String> list = this.stringRedisTemplate.opsForList().range("http:request:list", 0, -1);
        return list.stream()
                .map(
                        str -> {
                            try {
                                HttpLog log = this.jsonMapper.readValue(str, HttpLog.class);
                                HttpLog.Request req = log.getRequest();
                                HttpExchange.Request request =
                                        new HttpExchange.Request(
                                                req.getUri(),
                                                req.getRemoteAddress(),
                                                req.getMethod(),
                                                req.getHeaders());
                                HttpLog.Response resp = log.getResponse();
                                HttpExchange.Response response =
                                        new HttpExchange.Response(
                                                resp.getStatus(), resp.getHeaders());
                                HttpExchange exchange =
                                        new HttpExchange(
                                                log.getTimestamp()
                                                        .atZone(ZoneId.systemDefault())
                                                        .toInstant(),
                                                request,
                                                response,
                                                log.getPrincipal(),
                                                null,
                                                log.getTimeTaken());
                                return exchange;
                            } catch (JacksonException e) {
                            }
                            return null;
                        })
                .filter(e -> e != null)
                .toList();
    }

    @Override
    public void add(HttpExchange httpExchange) {
        try {
            HttpLog log = new HttpLog();
            log.setPrincipal(httpExchange.getPrincipal());
            log.setTimestamp(
                    httpExchange.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDateTime());
            log.setTimeTaken(httpExchange.getTimeTaken());
            HttpLog.Request request = new HttpLog.Request();
            BeanUtils.copyProperties(httpExchange.getRequest(), request);
            HttpLog.Response response = new HttpLog.Response();
            BeanUtils.copyProperties(httpExchange.getResponse(), response);
            log.setRequest(request);
            log.setResponse(response);
            this.stringRedisTemplate
                    .opsForList()
                    .leftPush("http:request:list", this.jsonMapper.writeValueAsString(log));
        } catch (JacksonException e) {
        }
    }

    public static class HttpLog {
        private LocalDateTime timestamp;
        private Request request;
        private Response response;
        private HttpExchange.Principal principal;
        private Duration timeTaken;

        public static class Request {
            private URI uri;
            private String remoteAddress;
            private String method;
            private Map<String, List<String>> headers;

            public URI getUri() {
                return uri;
            }

            public void setUri(URI uri) {
                this.uri = uri;
            }

            public String getRemoteAddress() {
                return remoteAddress;
            }

            public void setRemoteAddress(String remoteAddress) {
                this.remoteAddress = remoteAddress;
            }

            public String getMethod() {
                return method;
            }

            public void setMethod(String method) {
                this.method = method;
            }

            public Map<String, List<String>> getHeaders() {
                return headers;
            }

            public void setHeaders(Map<String, List<String>> headers) {
                this.headers = headers;
            }
        }

        public static final class Response {
            private int status;
            private Map<String, List<String>> headers;

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public Map<String, List<String>> getHeaders() {
                return headers;
            }

            public void setHeaders(Map<String, List<String>> headers) {
                this.headers = headers;
            }
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public Request getRequest() {
            return request;
        }

        public void setRequest(Request request) {
            this.request = request;
        }

        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }

        public HttpExchange.Principal getPrincipal() {
            return principal;
        }

        public void setPrincipal(HttpExchange.Principal principal) {
            this.principal = principal;
        }

        public Duration getTimeTaken() {
            return timeTaken;
        }

        public void setTimeTaken(Duration timeTaken) {
            this.timeTaken = timeTaken;
        }
    }
}
