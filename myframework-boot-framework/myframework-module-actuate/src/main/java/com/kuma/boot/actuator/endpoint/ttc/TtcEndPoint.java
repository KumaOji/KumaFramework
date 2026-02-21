/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.json.JSONObject
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  jakarta.annotation.Nullable
 *  org.springframework.boot.actuate.endpoint.annotation.Endpoint
 *  org.springframework.boot.actuate.endpoint.annotation.ReadOperation
 *  org.springframework.boot.actuate.endpoint.annotation.Selector
 *  org.springframework.boot.actuate.endpoint.annotation.WriteOperation
 */
package com.kuma.boot.actuator.endpoint.kmc;

import cn.hutool.json.JSONObject;
import com.kuma.boot.common.utils.lang.StringUtils;
import jakarta.annotation.Nullable;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

@Endpoint(id="kmc")
public class KmcEndPoint {
    private String STATUS = "up";
    private String DETAIL = "\u4e00\u5207\u6b63\u5e38";

    @ReadOperation
    public JSONObject test() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("status", (Object)this.STATUS);
        jsonObject.set("detail", (Object)this.DETAIL);
        return jsonObject;
    }

    @ReadOperation
    public JSONObject testSelector(@Selector String name) {
        JSONObject jsonObject = new JSONObject();
        if ("status".equals(name)) {
            jsonObject.set("status", (Object)this.STATUS);
        } else if ("detail".equals(name)) {
            jsonObject.set("detail", (Object)this.DETAIL);
        }
        return jsonObject;
    }

    @WriteOperation
    public void test4(@Selector String name, @Nullable String value) {
        if (!StringUtils.isEmpty((String)value)) {
            if ("status".equals(name)) {
                this.STATUS = value;
            } else if ("detail".equals(name)) {
                this.DETAIL = value;
            }
        }
    }
}

