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

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import tools.jackson.databind.json.JsonMapper;

/**
 * AuthUser Jackson 反序列化
 * @author YongWu zheng
 * @version V2.0  Created by 2020/10/28 17:19
 */
public class AuthUserJsonDeserializer extends StdDeserializer<AuthUser> {

    protected AuthUserJsonDeserializer() {
        super(AuthUser.class);
    }

    @Override
    public AuthUser deserialize(JsonParser p, DeserializationContext ctxt){

        JsonMapper mapper = null;
//        JsonMapper mapper = (JsonMapper) p.getCodec();

        final JsonNode jsonNode = mapper.readTree(p);

        final String uuid = jsonNode.get("uuid").asString();
        final String username = jsonNode.get("username").asString();
        final String nickname = jsonNode.get("nickname").asString(null);
        final String avatar = jsonNode.get("avatar").asString(null);
        final String blog = jsonNode.get("blog").asString(null);
        final String company = jsonNode.get("company").asString(null);
        final String location = jsonNode.get("location").asString(null);
        final String email = jsonNode.get("email").asString(null);
        final String remark = jsonNode.get("remark").asString(null);
        final AuthUserGender gender =
                mapper.convertValue(jsonNode.get("gender"), new TypeReference<AuthUserGender>() {});
        final String source = jsonNode.get("source").asString(null);

        final AuthToken token =
                mapper.convertValue(jsonNode.get("token"), new TypeReference<AuthToken>() {});

        final JsonNode rawUserInfoNode = jsonNode.get("rawUserInfo");
        final String rawUserInfoString = mapper.writeValueAsString(rawUserInfoNode);
        final JSONObject rawUserInfo = (JSONObject) JSONObject.parse(rawUserInfoString);
        rawUserInfo.remove("@class");

        return AuthUser.builder()
                .uuid(uuid)
                .username(username)
                .nickname(nickname)
                .avatar(avatar)
                .blog(blog)
                .company(company)
                .location(location)
                .email(email)
                .remark(remark)
                .gender(gender)
                .source(source)
                .token(token)
                // .rawUserInfo(rawUserInfo)
                .build();
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonDeserialize(using = AuthUserJsonDeserializer.class)
    public interface AuthUserMixin {}

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    public interface AuthTokenMixin {}
}
