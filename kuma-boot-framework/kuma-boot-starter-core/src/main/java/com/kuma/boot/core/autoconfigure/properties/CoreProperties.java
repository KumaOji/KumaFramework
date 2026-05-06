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

package com.kuma.boot.core.autoconfigure.properties;

import com.kuma.boot.core.enums.KmcEnvEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * CoreProperties
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:43:31
 */
@ConfigurationProperties(prefix = CoreProperties.PREFIX)
public class CoreProperties {

    public static final String PREFIX = "kuma.boot.core";

    /**
     * 鏍稿績鐜鍙橀噺
     */
    private KmcEnvEnum env;
    /**
     * 鏄惁寮€鍚牳蹇冨睘鎬ч厤缃?
     */
    private boolean enabled = true;
    /**
     * 鏄惁寮€鍚嚜瀹氫箟鏀堕泦鍣?
     */
    private boolean collectHookEnabled = true;
    /**
     * 鏄惁寮€鍚厤缃埛鏂颁笂涓嬫枃鐩戝惉
     */
    private boolean contextRestartEnabled = false;
    /**
     * 鍒锋柊涓婁笅鏂囩洃鍚瓑寰呮椂闂?
     */
    private int contextRestartTimespan = 10;

    public KmcEnvEnum getEnv() {
        return env;
    }

    public void setEnv(KmcEnvEnum env) {
        this.env = env;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getCollectHookEnabled() {
        return collectHookEnabled;
    }

    public void setCollectHookEnabled(boolean collectHookEnabled) {
        this.collectHookEnabled = collectHookEnabled;
    }

    public boolean getContextRestartEnabled() {
        return contextRestartEnabled;
    }

    public void setContextRestartEnabled(boolean contextRestartEnabled) {
        this.contextRestartEnabled = contextRestartEnabled;
    }

    public int getContextRestartTimespan() {
        return contextRestartTimespan;
    }

    public void setContextRestartTimespan(int contextRestartTimespan) {
        this.contextRestartTimespan = contextRestartTimespan;
    }
}
