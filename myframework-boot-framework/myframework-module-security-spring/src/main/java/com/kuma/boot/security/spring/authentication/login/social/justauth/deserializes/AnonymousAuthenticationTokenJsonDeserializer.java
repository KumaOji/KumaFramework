/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 *  com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  com.fasterxml.jackson.databind.deser.std.StdDeserializer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.security.authentication.AnonymousAuthenticationToken
 *  org.springframework.security.core.authority.SimpleGrantedAuthority
 *  org.springframework.security.web.authentication.WebAuthenticationDetails
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.deserializes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class AnonymousAuthenticationTokenJsonDeserializer
extends StdDeserializer<AnonymousAuthenticationToken> {
    private final Logger log = LoggerFactory.getLogger(((Object)((Object)this)).getClass());

    public AnonymousAuthenticationTokenJsonDeserializer() {
        super(AnonymousAuthenticationToken.class);
    }

    public AnonymousAuthenticationToken deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        AnonymousAuthenticationToken token;
        ObjectMapper mapper = (ObjectMapper)p.getCodec();
        JsonNode jsonNode = (JsonNode)mapper.readTree(p);
        Collection authorities = (Collection)mapper.convertValue((Object)jsonNode.get("authorities"), (TypeReference)new TypeReference<Collection<SimpleGrantedAuthority>>(this){});
        JsonNode detailsNode = jsonNode.get("details");
        Integer key = jsonNode.get("keyHash").asInt();
        String principal = jsonNode.get("principal").asText("anonymousUser");
        try {
            Constructor declaredConstructor = AnonymousAuthenticationToken.class.getDeclaredConstructor(Integer.class, Object.class, Collection.class);
            declaredConstructor.setAccessible(true);
            token = (AnonymousAuthenticationToken)declaredConstructor.newInstance(key, principal, authorities);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            String msg = String.format("AnonymousAuthenticationToken Jackson \u53cd\u5e8f\u5217\u5316\u9519\u8bef: principal \u53cd\u5e8f\u5217\u5316\u9519\u8bef: %s", e.getMessage());
            this.log.error(msg);
            throw new IOException(msg);
        }
        WebAuthenticationDetails details = (WebAuthenticationDetails)mapper.convertValue((Object)detailsNode, (TypeReference)new TypeReference<WebAuthenticationDetails>(this){});
        token.setDetails((Object)details);
        return token;
    }

    @JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
    @JsonDeserialize(using=AnonymousAuthenticationTokenJsonDeserializer.class)
    public static interface AnonymousAuthenticationTokenMixin {
    }
}

