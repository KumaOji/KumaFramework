/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.node.MissingNode
 */
package com.kuma.boot.security.spring.core.userdetails;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.kuma.boot.security.spring.core.authority.TtcGrantedAuthority;

import java.io.IOException;
import java.util.Set;

public class TtcUserDeserializer
extends JsonDeserializer<TtcUser> {
    private static final TypeReference<Set<TtcGrantedAuthority>> TTC_GRANTED_AUTHORITY_SET = new TypeReference<Set<TtcGrantedAuthority>>(){};
    private static final TypeReference<Set<String>> TTC_ROLE_SET = new TypeReference<Set<String>>(){};

    public TtcUser deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper)jp.getCodec();
        JsonNode jsonNode = (JsonNode)mapper.readTree(jp);
        Set authorities = (Set)mapper.convertValue((Object)jsonNode.get("authorities"), TTC_GRANTED_AUTHORITY_SET);
        Set roles = (Set)mapper.convertValue((Object)jsonNode.get("roles"), TTC_ROLE_SET);
        JsonNode passwordNode = this.readJsonNode(jsonNode, "password");
        Long userId = this.readJsonNode(jsonNode, "userId").asLong();
        String username = this.readJsonNode(jsonNode, "username").asText();
        String password = passwordNode.asText();
        boolean enabled = this.readJsonNode(jsonNode, "enabled").asBoolean();
        boolean accountNonExpired = this.readJsonNode(jsonNode, "accountNonExpired").asBoolean();
        boolean credentialsNonExpired = this.readJsonNode(jsonNode, "credentialsNonExpired").asBoolean();
        boolean accountNonLocked = this.readJsonNode(jsonNode, "accountNonLocked").asBoolean();
        TtcUser result = new TtcUser(userId, username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        if (passwordNode.asText() == null) {
            result.eraseCredentials();
        }
        return result;
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}

