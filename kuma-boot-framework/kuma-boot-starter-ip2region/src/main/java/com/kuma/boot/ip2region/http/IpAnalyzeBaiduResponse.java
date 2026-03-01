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

package com.kuma.boot.ip2region.http;

import java.io.Serializable;

/**
 * @description 地址解析的结果
 */
public class IpAnalyzeBaiduResponse implements Serializable {

    /**
     * 返回状态码
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 地址
     */
    private String address;

    /**
     * 详细内容
     */
    private IpAnalyzeContent content;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public IpAnalyzeContent getContent() {
        return content;
    }

    public void setContent(IpAnalyzeContent content) {
        this.content = content;
    }
}
