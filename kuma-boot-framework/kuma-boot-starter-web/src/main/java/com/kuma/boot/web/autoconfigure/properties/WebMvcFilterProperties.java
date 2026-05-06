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

package com.kuma.boot.web.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * FilterProperties
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-03 08:04:30
 */
@ConfigurationProperties(prefix = WebMvcFilterProperties.PREFIX)
public class WebMvcFilterProperties {

    public static final String PREFIX = "kuma.boot.web.filter";

    /** 寮€鍚礋杞藉潎琛￠殧绂昏鍒?*/
    private Boolean version = true;

    /** 寮€鍚鎴疯繃婊ゅ櫒 */
    private Boolean tenant = true;

    /** 寮€鍚棩蹇楅摼璺拷韪繃婊ゅ櫒 */
    private Boolean trace = true;

    /** 寮€鍚棩蹇楅摼璺拷韪繃婊ゅ櫒 */
    private Boolean webContext = true;

    /** 寮€鍚洃鎺ф姤琛?*/
    private Boolean report = true;

    /** 寮€鍚痯ing */
    private Boolean ping = true;

    public WebMvcFilterProperties() {}

    public Boolean getVersion() {
        return version;
    }

    public void setVersion(Boolean version) {
        this.version = version;
    }

    public Boolean getTenant() {
        return tenant;
    }

    public void setTenant(Boolean tenant) {
        this.tenant = tenant;
    }

    public Boolean getTrace() {
        return trace;
    }

    public void setTrace(Boolean trace) {
        this.trace = trace;
    }

    public Boolean getWebContext() {
        return webContext;
    }

    public void setWebContext(Boolean webContext) {
        this.webContext = webContext;
    }

    public Boolean getReport() {
        return report;
    }

    public void setReport(Boolean report) {
        this.report = report;
    }

    public Boolean getPing() {
        return ping;
    }

    public void setPing(Boolean ping) {
        this.ping = ping;
    }
}
