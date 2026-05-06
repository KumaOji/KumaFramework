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

package com.kuma.boot.ip2region.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ip2region 閰嶇疆绫?
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:01:42
 */
@ConfigurationProperties(Ip2regionProperties.PREFIX)
public class Ip2regionProperties {

    public static final String PREFIX = "kuma.boot.ip2region";

    private boolean enabled = true;

    /** ip2region.db 鏂囦欢璺緞 */
    private String dbFileLocation = "classpath:ip2region/ip2region.xdb";

    /**
     * ipv6wry.db 鏂囦欢璺緞
     */
    private String ipv6dbFileLocation = "classpath:ip2region/ipv6wry.db";

    public boolean isEnabled() {
        return enabled;
    }

    public String getIpv6dbFileLocation() {
        return ipv6dbFileLocation;
    }

    public void setIpv6dbFileLocation(String ipv6dbFileLocation) {
        this.ipv6dbFileLocation = ipv6dbFileLocation;
    }

    public String getDbFileLocation() {
        return dbFileLocation;
    }

    public void setDbFileLocation(String dbFileLocation) {
        this.dbFileLocation = dbFileLocation;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
