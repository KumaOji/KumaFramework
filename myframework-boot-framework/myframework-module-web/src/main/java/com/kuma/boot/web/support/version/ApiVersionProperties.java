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

package com.kuma.boot.web.support.version;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * RESTful API接口版本定义自动配置属性
 *
 * @author kuma
 * @version 2022.06
 * @since 2022-07-27 20:20:50
 */
@ConfigurationProperties("kuma.boot.web.api-version")
public class ApiVersionProperties {

    /**
     * 是否启用 <code style="color:red">RESTful API接口版本控制</code>
     *
     * <p>默认：true
     */
    private boolean enabled = true;

    /** 最小版本号，小于该版本号返回版本过时。 */
    private double minimumVersion;

    /**
     * {@link RequestMapping} 版本占位符，如下所示：
     *
     * <p>/{version}/user
     *
     * <p>/user/{version}
     */
    private String versionPlaceholder = "{version}";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getMinimumVersion() {
        return minimumVersion;
    }

    public void setMinimumVersion(double minimumVersion) {
        this.minimumVersion = minimumVersion;
    }

    public String getVersionPlaceholder() {
        return versionPlaceholder;
    }

    public void setVersionPlaceholder(String versionPlaceholder) {
        this.versionPlaceholder = versionPlaceholder;
    }
}
