/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.cloud.context.config.annotation.RefreshScope
 */
package com.kuma.boot.web.support.holidays.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(value="kuma.boot.web.holidays")
public class HolidaysApiProperties {
    public static final String PREFIX = "kuma.boot.web.holidays";
    private List<ExtData> extData = new ArrayList<ExtData>();

    public List<ExtData> getExtData() {
        return this.extData;
    }

    public void setExtData(List<ExtData> extData) {
        this.extData = extData;
    }

    public static class ExtData {
        private Integer year;
        private String dataPath;

        public Integer getYear() {
            return this.year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public String getDataPath() {
            return this.dataPath;
        }

        public void setDataPath(String dataPath) {
            this.dataPath = dataPath;
        }
    }
}

