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

package com.kuma.boot.actuator.endpoint.requst;

import cn.hutool.json.JSONObject;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * RequestMappingEndPoint - 暴露所有 URL 映射信息
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:08:52
 */
@Endpoint(id = "kmcrequestmappings")
public class RequestMappingEndPoint {

    private final RequestMappingHandlerMapping handlerMapping;

    public RequestMappingEndPoint(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @ReadOperation
    public JSONObject requestMapping() {
        Map<String, String> result = new LinkedHashMap<>();
        handlerMapping.getHandlerMethods().forEach((info, method) ->
                result.put(info.toString(), method.toString()));
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("total", result.size());
        jsonObject.set("mappings", result);
        return jsonObject;
    }
}
