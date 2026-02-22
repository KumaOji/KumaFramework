/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 *  com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility
 *  com.fasterxml.jackson.annotation.JsonTypeInfo
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$Id
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JavaType
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  com.fasterxml.jackson.databind.deser.std.StdDeserializer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.security.authentication.RememberMeAuthenticationToken
 *  org.springframework.security.core.authority.SimpleGrantedAuthority
 *  org.springframework.security.web.authentication.WebAuthenticationDetails
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.deserializes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
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
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class RememberMeAuthenticationTokenJsonDeserializer
extends StdDeserializer<RememberMeAuthenticationToken> {
    private final Logger log = LoggerFactory.getLogger(((Object)((Object)this)).getClass());

    public RememberMeAuthenticationTokenJsonDeserializer() {
        super(RememberMeAuthenticationToken.class);
    }

    public RememberMeAuthenticationToken deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        RememberMeAuthenticationToken token;
        Object principal;
        ObjectMapper mapper = (ObjectMapper)p.getCodec();
        JsonNode jsonNode = (JsonNode)mapper.readTree(p);
        Collection tokenAuthorities = (Collection)mapper.convertValue((Object)jsonNode.get("authorities"), (TypeReference)new TypeReference<Collection<SimpleGrantedAuthority>>(this){});
        boolean authenticated = jsonNode.get("authenticated").asBoolean();
        Integer keyHash = jsonNode.get("keyHash").asInt();
        JsonNode detailsNode = jsonNode.get("details");
        JsonNode principalNode = jsonNode.get("principal");
        String principalString = principalNode.toString();
        String principalClassName = principalString.substring(1);
        principalClassName = principalClassName.startsWith("\"@class\":\"") ? principalClassName.substring(principalClassName.indexOf("\"@class\":\"") + 10) : principalClassName.substring(1);
        principalClassName = principalClassName.substring(0, principalClassName.indexOf("\""));
        try {
            Class<?> principalClass = Class.forName(principalClassName);
            JavaType javaType = mapper.getTypeFactory().constructType(principalClass);
            principal = mapper.convertValue((Object)principalNode, javaType);
        }
        catch (Exception e) {
            String msg = String.format("RememberMeAuthenticationToken Jackson \u53cd\u5e8f\u5217\u5316\u9519\u8bef: principal \u53cd\u5e8f\u5217\u5316\u9519\u8bef: %s", principalNode.toString());
            this.log.error(msg);
            throw new IOException(msg);
        }
        try {
            Constructor declaredConstructor = RememberMeAuthenticationToken.class.getDeclaredConstructor(Integer.class, Object.class, Collection.class);
            declaredConstructor.setAccessible(true);
            token = (RememberMeAuthenticationToken)declaredConstructor.newInstance(keyHash, principal, tokenAuthorities);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            String msg = String.format("RememberMeAuthenticationToken Jackson \u53cd\u5e8f\u5217\u5316\u9519\u8bef: principal \u53cd\u5e8f\u5217\u5316\u9519\u8bef: %s", e.getMessage());
            this.log.error(msg);
            throw new IOException(msg);
        }
        token.setAuthenticated(authenticated);
        token.eraseCredentials();
        if (!detailsNode.isNull() && !detailsNode.isMissingNode()) {
            WebAuthenticationDetails details = (WebAuthenticationDetails)mapper.convertValue((Object)detailsNode, (TypeReference)new TypeReference<WebAuthenticationDetails>(this){});
            token.setDetails((Object)details);
        }
        return token;
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")
    @JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
    @JsonDeserialize(using=RememberMeAuthenticationTokenJsonDeserializer.class)
    public static interface RememberMeAuthenticationTokenMixin {
    }
}

