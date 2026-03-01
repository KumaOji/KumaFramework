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
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.MissingNode;
import java.io.IOException;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * Custom Deserializer for {@link User} class. This is already registered with
 * {@code org.springframework.security.jackson2.UserMixin}.
 * You can also use it directly with your mixin class.
 *
 * @author Jitendra Singh
 * @author YongWu zheng
 * @since 4.2
 */
public class UserDeserializer extends StdDeserializer<User> {

    public UserDeserializer() {
        super(User.class);
    }

    /**
     * This method will create {@link User} object. It will ensure successful object creation even if password key is null in
     * serialized json, because credentials may be removed from the {@link User} by invoking {@link User#eraseCredentials()}.
     * In that case there won't be any password key in serialized json.
     *
     * @param jp the JsonParser
     * @param ctxt the DeserializationContext
     * @return the user
     * @throws IOException if a exception during IO occurs
     * @throws JacksonException if an error during JSON processing occurs
     */
    @SuppressWarnings("DuplicatedCode")
    @Override
    public User deserialize(JsonParser jp, DeserializationContext ctxt)
            throws JacksonException {

//        JsonMapper mapper = (JsonMapper) jp.getCodec();
        JsonMapper mapper = null;
        JsonNode jsonNode = mapper.readTree(jp);

        Set<? extends GrantedAuthority> authorities =
                mapper.convertValue(
                        jsonNode.get("authorities"),
                        new TypeReference<Set<SimpleGrantedAuthority>>() {});
        JsonNode password = this.readJsonNode(jsonNode, "password");
        User result =
                new User(
                        this.readJsonNode(jsonNode, "username").asString(),
                        password.asText(""),
                        this.readJsonNode(jsonNode, "enabled").asBoolean(),
                        this.readJsonNode(jsonNode, "accountNonExpired").asBoolean(),
                        this.readJsonNode(jsonNode, "credentialsNonExpired").asBoolean(),
                        this.readJsonNode(jsonNode, "accountNonLocked").asBoolean(),
                        authorities);

        if (password.asText(null) == null) {
            result.eraseCredentials();
        }

        return result;
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        if (jsonNode.has(field)) {
            return jsonNode.get(field);
        }
        return MissingNode.getInstance();
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonDeserialize(using = UserDeserializer.class)
    public interface UserMixin {}
}
