/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 *  com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  com.fasterxml.jackson.annotation.JsonTypeInfo
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$Id
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.deserializes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE, getterVisibility=JsonAutoDetect.Visibility.PUBLIC_ONLY, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class SimpleGrantedAuthorityMixin {
    @JsonCreator
    public SimpleGrantedAuthorityMixin(@JsonProperty(value="authority") String role) {
    }
}

