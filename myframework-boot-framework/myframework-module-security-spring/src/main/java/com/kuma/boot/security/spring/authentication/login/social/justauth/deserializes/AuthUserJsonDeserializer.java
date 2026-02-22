/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSONObject
 *  com.alibaba.fastjson2.JSONReader$Feature
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 *  com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility
 *  com.fasterxml.jackson.annotation.JsonTypeInfo
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$Id
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  com.fasterxml.jackson.databind.deser.std.StdDeserializer
 *  me.zhyd.oauth.enums.AuthUserGender
 *  me.zhyd.oauth.model.AuthToken
 *  me.zhyd.oauth.model.AuthUser
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.deserializes;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;

public class AuthUserJsonDeserializer
extends StdDeserializer<AuthUser> {
    protected AuthUserJsonDeserializer() {
        super(AuthUser.class);
    }

    public AuthUser deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper)p.getCodec();
        JsonNode jsonNode = (JsonNode)mapper.readTree(p);
        String uuid = jsonNode.get("uuid").asText();
        String username = jsonNode.get("username").asText();
        String nickname = jsonNode.get("nickname").asText(null);
        String avatar = jsonNode.get("avatar").asText(null);
        String blog = jsonNode.get("blog").asText(null);
        String company = jsonNode.get("company").asText(null);
        String location = jsonNode.get("location").asText(null);
        String email = jsonNode.get("email").asText(null);
        String remark = jsonNode.get("remark").asText(null);
        AuthUserGender gender = (AuthUserGender)mapper.convertValue((Object)jsonNode.get("gender"), (TypeReference)new TypeReference<AuthUserGender>(this){});
        String source = jsonNode.get("source").asText(null);
        AuthToken token = (AuthToken)mapper.convertValue((Object)jsonNode.get("token"), (TypeReference)new TypeReference<AuthToken>(this){});
        JsonNode rawUserInfoNode = jsonNode.get("rawUserInfo");
        String rawUserInfoString = mapper.writeValueAsString((Object)rawUserInfoNode);
        JSONObject rawUserInfo = JSONObject.parse((String)rawUserInfoString, (JSONReader.Feature[])new JSONReader.Feature[0]);
        rawUserInfo.remove((Object)"@class");
        return AuthUser.builder().uuid(uuid).username(username).nickname(nickname).avatar(avatar).blog(blog).company(company).location(location).email(email).remark(remark).gender(gender).source(source).token(token).build();
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")
    @JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
    public static interface AuthTokenMixin {
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")
    @JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
    @JsonDeserialize(using=AuthUserJsonDeserializer.class)
    public static interface AuthUserMixin {
    }
}

