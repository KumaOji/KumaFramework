/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 *  com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility
 *  com.fasterxml.jackson.annotation.JsonTypeInfo
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$Id
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  com.fasterxml.jackson.databind.deser.std.StdDeserializer
 *  com.fasterxml.jackson.databind.node.MissingNode
 *  org.springframework.security.core.authority.SimpleGrantedAuthority
 *  org.springframework.security.core.userdetails.User
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.deserializes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.MissingNode;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UserDeserializer
extends StdDeserializer<User> {
    public UserDeserializer() {
        super(User.class);
    }

    public User deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper)jp.getCodec();
        JsonNode jsonNode = (JsonNode)mapper.readTree(jp);
        Set authorities = (Set)mapper.convertValue((Object)jsonNode.get("authorities"), (TypeReference)new TypeReference<Set<SimpleGrantedAuthority>>(this){});
        JsonNode password = this.readJsonNode(jsonNode, "password");
        User result = new User(this.readJsonNode(jsonNode, "username").asText(), password.asText(""), this.readJsonNode(jsonNode, "enabled").asBoolean(), this.readJsonNode(jsonNode, "accountNonExpired").asBoolean(), this.readJsonNode(jsonNode, "credentialsNonExpired").asBoolean(), this.readJsonNode(jsonNode, "accountNonLocked").asBoolean(), (Collection)authorities);
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

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")
    @JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
    @JsonDeserialize(using=UserDeserializer.class)
    public static interface UserMixin {
    }
}

