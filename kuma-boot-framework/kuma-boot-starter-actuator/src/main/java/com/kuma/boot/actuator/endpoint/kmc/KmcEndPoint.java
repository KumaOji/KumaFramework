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

package com.kuma.boot.actuator.endpoint.kmc;

import cn.hutool.json.JSONObject;
import com.kuma.boot.common.utils.lang.StringUtils;
import jakarta.annotation.Nullable;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

/**
 * KmcEndPoint
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:08:52
 */
@Endpoint(id = "kmc")
public class KmcEndPoint {

    private volatile String status = "up";
    private volatile String detail = "一切正常";

    @ReadOperation
    public JSONObject info() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("status", status);
        jsonObject.set("detail", detail);
        return jsonObject;
    }

    @ReadOperation
    public JSONObject infoByKey(@Selector String key) {
        JSONObject jsonObject = new JSONObject();
        if ("status".equals(key)) {
            jsonObject.set("status", status);
        } else if ("detail".equals(key)) {
            jsonObject.set("detail", detail);
        }
        return jsonObject;
    }

    /** 动态修改指标，e.g. POST /actuator/kmc/{key}  body: { "value": "..." } */
    @WriteOperation
    public void update(@Selector String key, @Nullable String value) {
        if (!StringUtils.isEmpty(value)) {
            if ("status".equals(key)) {
                status = value;
            } else if ("detail".equals(key)) {
                detail = value;
            }
        }
    }
}
