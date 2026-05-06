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

package com.kuma.boot.web.laytpl.properties;

import com.kuma.boot.common.utils.date.DateUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * LayTpl閰嶇疆
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:30:16
 */
@ConfigurationProperties(prefix = LayTplProperties.PREFIX)
public class LayTplProperties {

    public static final String PREFIX = "kuma.boot.laytpl";

    private boolean enabled = false;

    /** 妯℃澘鍒嗛殧绗﹀紑濮嬶紝榛樿锛歿{ */
    private String open = "{{";

    /** 妯℃澘鍒嗛殧绗︾粨鏉燂紝榛樿锛殅} */
    private String close = "}}";

    /** 妯℃澘鍓嶇紑锛岄粯璁わ細classpath:templates/tpl/ */
    private String prefix = "classpath:templates/tpl/";

    /** 缂撳瓨妯℃澘锛岄粯璁わ細true */
    private boolean cache = true;

    /** 鏁板瓧鏍煎紡鍖栵紝榛樿锛?.00 */
    private String numPattern = "#.00";

    /** Date 鏃ユ湡鏍煎紡鍖栵紝榛樿锛?yyyy-MM-dd HH:mm:ss" */
    private String datePattern = DateUtils.PATTERN_DATETIME;

    /** java8 LocalTime鏃堕棿鏍煎紡鍖栵紝榛樿锛?HH:mm:ss" */
    private String localTimePattern = DateUtils.PATTERN_TIME;

    /** java8 LocalDate鏃ユ湡鏍煎紡鍖栵紝榛樿锛?yyyy-MM-dd" */
    private String localDatePattern = DateUtils.PATTERN_DATE;

    /** java8 LocalDateTime鏃ユ湡鏃堕棿鏍煎紡鍖栵紝榛樿锛?yyyy-MM-dd HH:mm:ss" */
    private String localDateTimePattern = DateUtils.PATTERN_DATETIME;

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public String getNumPattern() {
        return numPattern;
    }

    public void setNumPattern(String numPattern) {
        this.numPattern = numPattern;
    }

    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public String getLocalTimePattern() {
        return localTimePattern;
    }

    public void setLocalTimePattern(String localTimePattern) {
        this.localTimePattern = localTimePattern;
    }

    public String getLocalDatePattern() {
        return localDatePattern;
    }

    public void setLocalDatePattern(String localDatePattern) {
        this.localDatePattern = localDatePattern;
    }

    public String getLocalDateTimePattern() {
        return localDateTimePattern;
    }

    public void setLocalDateTimePattern(String localDateTimePattern) {
        this.localDateTimePattern = localDateTimePattern;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
