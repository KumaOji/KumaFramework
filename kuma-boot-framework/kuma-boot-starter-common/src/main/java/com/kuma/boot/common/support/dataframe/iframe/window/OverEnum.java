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

package com.kuma.boot.common.support.dataframe.iframe.window;

/**
 * Window Function
 *
 * @author caizhihao
 */
public enum OverEnum {

    /**
     * Rank of current row within its partition, with gaps
     */
    RANK,

    /**
     * Rank of current row within its partition, without gaps
     */
    DENSE_RANK,

    /**
     * Number of current row within its partition
     */
    ROW_NUMBER,

    /**
     * Percentage rank value
     */
    PERCENT_RANK,

    /**
     * Cumulative distribution value
     */
    CUME_DIST,

    /**
     * Value of argument from row lagging current row within partition
     */
    LAG,

    /**
     * Value of argument from row leading current row within partition
     */
    LEAD,

    /**
     * Value of argument from first row of window frame
     */
    FIRST_VALUE,

    /**
     * Value of argument from last row of window frame
     */
    LAST_VALUE,

    /**
     * Value of argument from N-th row of window frame
     */
    NTH_VALUE,

    /**
     * Bucket number of current row within its partition.
     */
    // NTILE,

    SUM,

    AVG,

    MAX,

    MIN,

    COUNT,

    /**
     *
     */
    // PROPORTION

}
