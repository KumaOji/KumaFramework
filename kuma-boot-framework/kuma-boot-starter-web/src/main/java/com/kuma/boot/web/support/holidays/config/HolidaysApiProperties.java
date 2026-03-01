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

package com.kuma.boot.web.support.holidays.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.ArrayList;
import java.util.List;

/**
 * HolidaysApi 配置类
 */
@RefreshScope
@ConfigurationProperties(HolidaysApiProperties.PREFIX)
public class HolidaysApiProperties {
    public static final String PREFIX = "kuma.boot.web.holidays";

    /**
     * 自行扩展的 json 文件路径
     */
    private List<ExtData> extData = new ArrayList<>();

    public static class ExtData {
        /**
         * 年份
         */
        private Integer year;

        /**
         * 数据目录
         */
        private String dataPath;

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public String getDataPath() {
            return dataPath;
        }

        public void setDataPath(String dataPath) {
            this.dataPath = dataPath;
        }
    }

    public List<ExtData> getExtData() {
        return extData;
    }

    public void setExtData(List<ExtData> extData) {
        this.extData = extData;
    }
}
