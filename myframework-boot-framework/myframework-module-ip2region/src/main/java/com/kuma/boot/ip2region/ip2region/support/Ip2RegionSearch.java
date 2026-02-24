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

package com.kuma.boot.ip2region.ip2region.support;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ip2region.ip2region.doamin.IpInfo;
import org.lionsoul.ip2region.xdb.Searcher;

/**
 * <p>
 * Ip2RegionSearch
 * </p>
 *
 *
 */
public class Ip2RegionSearch {

    private final Searcher searcher;

    public Ip2RegionSearch(Searcher searcher) {
        this.searcher = searcher;
    }

    /**
     * Search as string.
     *
     * @param ip the ip
     * @return the string
     */
    public String searchAsString(String ip) {
        try {
            return searcher.search(ip);
        } catch (Exception e) {
            LogUtils.error("Ip2Region Searcher fail! IP:{}", ip);
            return "";
        }
    }

    /**
     * Search as info ip info.
     *
     * @param ip the ip
     * @return the ip info
     */
    public IpInfo searchAsInfo(String ip) {
        return IpInfo.of(this.searchAsString(ip));
    }

    /**
     * Search as json string.
     *
     * @param ip the ip
     * @return the string
     */
    public String searchAsJson(String ip) {
        return JacksonUtils.toJSONString(this.searchAsInfo(ip));
    }
}
