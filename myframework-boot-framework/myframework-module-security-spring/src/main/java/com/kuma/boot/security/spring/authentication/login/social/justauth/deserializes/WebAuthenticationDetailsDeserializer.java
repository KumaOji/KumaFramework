/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 *  com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility
 *  com.fasterxml.jackson.annotation.JsonTypeInfo
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$Id
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  com.fasterxml.jackson.databind.deser.std.StdDeserializer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.security.web.authentication.WebAuthenticationDetails
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.deserializes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class WebAuthenticationDetailsDeserializer
extends StdDeserializer<WebAuthenticationDetails> {
    private final Logger log = LoggerFactory.getLogger(((Object)((Object)this)).getClass());

    public WebAuthenticationDetailsDeserializer() {
        super(WebAuthenticationDetails.class);
    }

    public WebAuthenticationDetails deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper)p.getCodec();
        JsonNode jsonNode = (JsonNode)mapper.readTree(p);
        Class<WebAuthenticationDetails> detailsClass = WebAuthenticationDetails.class;
        try {
            Class<String> stringClass = String.class;
            Constructor privateConstructor = detailsClass.getDeclaredConstructor(stringClass, stringClass);
            privateConstructor.setAccessible(true);
            String remoteAddress = jsonNode.get("remoteAddress").asText(null);
            String sessionId = jsonNode.get("sessionId").asText(null);
            return (WebAuthenticationDetails)privateConstructor.newInstance(remoteAddress, sessionId);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            String msg = String.format("WebAuthenticationDetails Jackson \u53cd\u5e8f\u5217\u5316\u9519\u8bef: %s", e.getMessage());
            this.log.error(msg);
            throw new IOException(msg, e);
        }
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")
    @JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
    @JsonDeserialize(using=WebAuthenticationDetailsDeserializer.class)
    public static interface WebAuthenticationDetailsMixin {
    }
}

