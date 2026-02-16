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

package com.kuma.boot.common.model.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 分页常量
 *
 * @author kuma
 */
public final class PageableConstants {

    private PageableConstants() {
    }

    public static final String SORT_FILED_REGEX = "(([A-Za-z0-9_]{1,10}\\.)?[A-Za-z0-9_]{1,64})";
    public static final String SORT_FILED_ORDER = "(desc|asc)";
    public static final String SORT_REGEX = "^" + SORT_FILED_REGEX + "(," + SORT_FILED_ORDER + ")*$";

    public static final String DEFAULT_PAGE_PARAMETER = "page";
    public static final String DEFAULT_SIZE_PARAMETER = "size";
    public static final String DEFAULT_SORT_PARAMETER = "sort";
    public static final int DEFAULT_MAX_PAGE_SIZE = 100;

    public static final String ASC = "asc";

    public static final Set<String> SQL_KEYWORDS = new HashSet<>(Arrays.asList(
            "master", "truncate", "insert", "select", "delete", "update", "declare", "alter", "drop", "sleep"));

    @Deprecated
    public static final String SORT_ORDERS = "sortOrders";
    @Deprecated
    public static final String SORT_FIELDS = "sortFields";
}
