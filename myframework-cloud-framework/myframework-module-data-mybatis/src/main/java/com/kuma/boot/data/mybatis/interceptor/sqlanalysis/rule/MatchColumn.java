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

package com.kuma.boot.data.mybatis.interceptor.sqlanalysis.rule;

/**
 * @Author huhaitao21
 * @Description 匹配列
 * @since 15:50 2022/11/9
 **/
public enum MatchColumn {
    SELECT_TYPE("selectType"),
    TABLE("table"),
    PARTITIONS("partitions"),
    TYPE("type"),
    POSSIBLE_KEYS("possibleKeys"),
    KEY("key"),
    KEY_LEN("keyLen"),
    REF("ref"),
    ROWS("rows"),
    FILTERED("filtered"),
    EXTRA("extra");

    /**
     * 匹配字段
     */
    private String column;

    MatchColumn(String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}
