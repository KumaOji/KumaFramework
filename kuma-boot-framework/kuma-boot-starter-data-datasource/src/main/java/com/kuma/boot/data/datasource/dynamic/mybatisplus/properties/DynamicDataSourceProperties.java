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

package com.kuma.boot.data.datasource.dynamic.mybatisplus.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MybatisPlusAutoFillProperties
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:44:25
 */
@ConfigurationProperties(prefix = DynamicDataSourceProperties.PREFIX)
public class DynamicDataSourceProperties {

    public static final String PREFIX = "kuma.boot.data.datasource.dynamic";

    /** 鏄惁寮€鍚嚜鍔ㄥ～鍏呭瓧娈?*/
    private Boolean enabled = false;

    /** sql璇彞 */
    private String queryDsSql = "select * from gen_datasource_conf where del_flag = 0";

    public String getQueryDsSql() {
        return queryDsSql;
    }

    public void setQueryDsSql(String queryDsSql) {
        this.queryDsSql = queryDsSql;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
