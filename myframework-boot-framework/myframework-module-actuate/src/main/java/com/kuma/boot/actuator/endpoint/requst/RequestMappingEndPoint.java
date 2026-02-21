/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.json.JSONObject
 *  org.springframework.boot.actuate.endpoint.annotation.Endpoint
 *  org.springframework.boot.actuate.endpoint.annotation.ReadOperation
 */
package com.kuma.boot.actuator.endpoint.requst;

import cn.hutool.json.JSONObject;
import java.util.Map;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

@Endpoint(id="kmcrequestmappings")
public class RequestMappingEndPoint {
    public static Map<String, Object> requestMappingHandlerMapping;

    @ReadOperation
    public JSONObject requestMapping() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("data", requestMappingHandlerMapping);
        return jsonObject;
    }
}

