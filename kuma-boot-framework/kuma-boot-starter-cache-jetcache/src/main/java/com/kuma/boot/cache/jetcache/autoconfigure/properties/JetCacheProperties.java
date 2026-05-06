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

package com.kuma.boot.cache.jetcache.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * JetCache 閰嶇疆灞炴€с€?
 *
 * @author kuma
 * @since 2022-07-03
 */
@ConfigurationProperties(JetCacheProperties.PREFIX)
public class JetCacheProperties {

    public static final String PREFIX = "kuma.boot.cache.jetcache";

    /** 鏄惁鍚敤锛堥粯璁ゅ紑鍚級 */
    private boolean enabled = true;

    /** 鏁忔劅淇℃伅鑴辨晱锛堥粯璁ゅ紑鍚級 */
    private Boolean desensitization = true;

    /** 搴旂敤閫€鍑烘椂娓呯悊杩滅▼缂撳瓨锛堥粯璁ゅ叧闂級 */
    private Boolean clearRemoteOnExit = false;

    /** 鏄惁鍏佽缂撳瓨 null 鍊硷紙榛樿鍏佽锛?*/
    private Boolean allowNullValues = true;

    /** cache name 涓殑鍒嗛殧绗︼紝鐢ㄤ簬鍖归厤 expires 閰嶇疆 key锛堥粯璁?{@code -}锛?*/
    private String separator = "-";

    /** 鎸?cache name 閰嶇疆鐨勭嫭绔嬭繃鏈熸椂闂?*/
    private Map<String, Expire> expires = new HashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getDesensitization() {
        return desensitization;
    }

    public void setDesensitization(Boolean desensitization) {
        this.desensitization = desensitization;
    }

    public Boolean getClearRemoteOnExit() {
        return clearRemoteOnExit;
    }

    public void setClearRemoteOnExit(Boolean clearRemoteOnExit) {
        this.clearRemoteOnExit = clearRemoteOnExit;
    }

    public Boolean getAllowNullValues() {
        return allowNullValues;
    }

    public void setAllowNullValues(Boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public Map<String, Expire> getExpires() {
        return expires;
    }

    public void setExpires(Map<String, Expire> expires) {
        this.expires = expires;
    }

    @Override
    public String toString() {
        return "JetCacheProperties{"
                + "desensitization=" + desensitization
                + ", clearRemoteOnExit=" + clearRemoteOnExit
                + ", allowNullValues=" + allowNullValues
                + ", separator='" + separator + '\''
                + '}';
    }
}
