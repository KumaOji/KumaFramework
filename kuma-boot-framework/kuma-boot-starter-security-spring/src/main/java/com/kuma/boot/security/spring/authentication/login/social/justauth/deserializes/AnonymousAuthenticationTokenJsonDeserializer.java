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
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import tools.jackson.databind.json.JsonMapper;

/**
 * AnonymousAuthenticationToken Jackson 反序列化
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/28 10:58
 */
public class AnonymousAuthenticationTokenJsonDeserializer
        extends StdDeserializer<AnonymousAuthenticationToken> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public AnonymousAuthenticationTokenJsonDeserializer() {
        super(AnonymousAuthenticationToken.class);
    }

    @Override
    public AnonymousAuthenticationToken deserialize(JsonParser p, DeserializationContext ctxt) {

//        JsonMapper mapper = (JsonMapper) p.getCodec();
        JsonMapper mapper = null;

        final JsonNode jsonNode = mapper.readTree(p);

        // 获取 authorities
        Collection<? extends GrantedAuthority> authorities =
                mapper.convertValue(
                        jsonNode.get("authorities"),
                        new TypeReference<Collection<SimpleGrantedAuthority>>() {});

        final JsonNode detailsNode = jsonNode.get("details");

        final Integer key = jsonNode.get("keyHash").asInt();
        final String principal = jsonNode.get("principal").asString("anonymousUser");

        // 创建 AnonymousAuthenticationToken 对象
        AnonymousAuthenticationToken token;
        try {
            final Constructor<AnonymousAuthenticationToken> declaredConstructor =
                    AnonymousAuthenticationToken.class.getDeclaredConstructor(
                            Integer.class, Object.class, Collection.class);
            declaredConstructor.setAccessible(true);
            token = declaredConstructor.newInstance(key, principal, authorities);
        } catch (NoSuchMethodException
                 | InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException e) {
            final String msg =
                    String.format(
                            "AnonymousAuthenticationToken Jackson 反序列化错误: principal 反序列化错误: %s",
                            e.getMessage());
            log.error(msg);
            throw new RuntimeException(msg);
        }

        // 创建 details 对象
        WebAuthenticationDetails details =
                mapper.convertValue(detailsNode, new TypeReference<WebAuthenticationDetails>() {});
        token.setDetails(details);

        return token;
    }

    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonDeserialize(using = AnonymousAuthenticationTokenJsonDeserializer.class)
    public interface AnonymousAuthenticationTokenMixin {}
}
