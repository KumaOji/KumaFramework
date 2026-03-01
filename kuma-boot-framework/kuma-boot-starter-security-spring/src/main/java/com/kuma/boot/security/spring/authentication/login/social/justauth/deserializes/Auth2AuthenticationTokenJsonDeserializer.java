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

package com.kuma.boot.security.spring.authentication.login.social.justauth.deserializes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import com.kuma.boot.security.spring.authentication.login.social.justauth.JustAuthAuthenticationToken;
import java.io.IOException;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import tools.jackson.databind.json.JsonMapper;

/**
 * Auth2AuthenticationToken Jackson 反序列化
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/28 10:58
 */
public class Auth2AuthenticationTokenJsonDeserializer
        extends StdDeserializer<JustAuthAuthenticationToken> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Auth2AuthenticationTokenJsonDeserializer() {
        super(JustAuthAuthenticationToken.class);
    }

    @Override
    public JustAuthAuthenticationToken deserialize(JsonParser p, DeserializationContext ctxt) {

//        JsonMapper mapper = (JsonMapper) p.getCodec();
        JsonMapper mapper = null;

        final JsonNode jsonNode = mapper.readTree(p);

        // 获取 authorities
        Collection<? extends GrantedAuthority> tokenAuthorities =
                mapper.convertValue(
                        jsonNode.get("authorities"),
                        new TypeReference<Collection<SimpleGrantedAuthority>>() {});

        final String providerId = jsonNode.get("providerId").asText(null);
        final JsonNode detailsNode = jsonNode.get("details");
        final JsonNode principalNode = jsonNode.get("principal");

        // 创建 principal 对象
        Object principal;
        // 获取 principal 实际的全类名
        final String principalString = principalNode.toString();
        String principalClassName = principalString.substring(1);
        if (principalClassName.startsWith("\"@class\":\"")) {
            principalClassName =
                    principalClassName.substring(principalClassName.indexOf("\"@class\":\"") + 10);
        } else {
            principalClassName = principalClassName.substring(1);
        }
        principalClassName = principalClassName.substring(0, principalClassName.indexOf("\""));

        try {
            final Class<?> principalClass = Class.forName(principalClassName);
            final JavaType javaType = mapper.getTypeFactory().constructType(principalClass);
            principal = mapper.convertValue(principalNode, javaType);
        } catch (Exception e) {
            final String msg =
                    String.format(
                            "Auth2AuthenticationToken Jackson 反序列化错误: principal 反序列化错误: %s",
                            principalNode.toString());
            log.error(msg);
            throw new RuntimeException(msg);
        }

        final JustAuthAuthenticationToken justAuthAuthenticationToken =
                new JustAuthAuthenticationToken(principal, tokenAuthorities, providerId);

        // 创建 details 对象
        if (!(detailsNode.isNull() || detailsNode.isMissingNode())) {
            WebAuthenticationDetails details =
                    mapper.convertValue(
                            detailsNode, new TypeReference<WebAuthenticationDetails>() {});
            justAuthAuthenticationToken.setDetails(details);
        }

        return justAuthAuthenticationToken;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonDeserialize(using = Auth2AuthenticationTokenJsonDeserializer.class)
    public interface Auth2AuthenticationTokenMixin {}
}
