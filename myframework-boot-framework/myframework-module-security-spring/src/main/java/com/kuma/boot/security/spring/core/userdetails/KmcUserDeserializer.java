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

package com.kuma.boot.security.spring.core.userdetails;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.MissingNode;
import com.kuma.boot.security.spring.core.authority.KmcGrantedAuthority;

import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * <p>自定义 UserDetails 序列化
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:07:55
 */
public class KmcUserDeserializer extends ValueDeserializer<com.kuma.boot.security.spring.core.userdetails.KmcUser> {

    /**
     * 希罗多德授予权限设置
     */
    private static final TypeReference<Set<KmcGrantedAuthority>> KMC_GRANTED_AUTHORITY_SET =
            new TypeReference<>() {};

    /**
     * 希罗多德角色组
     */
    private static final TypeReference<Set<String>> KMC_ROLE_SET =
            new TypeReference<Set<String>>() {};


    /**
     * This method will create {@link User} object. It will ensure successful object creation even
     * if password key is null in serialized json, because credentials may be removed from the
     * {@link User} by invoking {@link User#eraseCredentials()}. In that case there won't be any
     * password key in serialized json.
     *
     * @param jp   the JsonParser
     * @param ctxt the DeserializationContext
     * @return {@link com.kuma.boot.security.spring.core.userdetails.KmcUser }
     * @since 2023-07-04 10:07:55
     */
    @Override
    public com.kuma.boot.security.spring.core.userdetails.KmcUser deserialize(JsonParser jp, DeserializationContext ctxt)
            throws  JacksonException {
//        JsonMapper mapper = (JsonMapper) jp.getCodec();
        JsonMapper mapper = null;
        JsonNode jsonNode = mapper.readTree(jp);
        Set<? extends GrantedAuthority> authorities =
                mapper.convertValue(jsonNode.get("authorities"), KMC_GRANTED_AUTHORITY_SET);
        Set<String> roles = mapper.convertValue(jsonNode.get("roles"), KMC_ROLE_SET);
        JsonNode passwordNode = readJsonNode(jsonNode, "password");
        Long userId = readJsonNode(jsonNode, "userId").asLong();
        String username = readJsonNode(jsonNode, "username").asString();
        String password = passwordNode.asString();
        boolean enabled = readJsonNode(jsonNode, "enabled").asBoolean();
        boolean accountNonExpired = readJsonNode(jsonNode, "accountNonExpired").asBoolean();
        boolean credentialsNonExpired = readJsonNode(jsonNode, "credentialsNonExpired").asBoolean();
        boolean accountNonLocked = readJsonNode(jsonNode, "accountNonLocked").asBoolean();
        com.kuma.boot.security.spring.core.userdetails.KmcUser result =
                new com.kuma.boot.security.spring.core.userdetails.KmcUser(
                        userId,
                        username,
                        password,
                        enabled,
                        accountNonExpired,
                        credentialsNonExpired,
                        accountNonLocked,
                        authorities);
        if (passwordNode.asString() == null) {
            result.eraseCredentials();
        }
        return result;
    }

    /**
     * 读取json节点
     *
     * @param jsonNode json节点
     * @param field    场
     * @return {@link JsonNode }
     * @since 2023-07-04 10:07:55
     */
    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}
