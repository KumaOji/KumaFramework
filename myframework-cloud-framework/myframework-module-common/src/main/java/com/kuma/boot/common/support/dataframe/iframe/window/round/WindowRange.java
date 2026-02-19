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

package com.kuma.boot.common.support.dataframe.iframe.window.round;

import java.util.List;

/**
 * Window Range Can be generated through {@link Range} construction the window range is a
 * sliding window, so we can specify some boundaries and sliding situations of the window
 *
 * @author caizhihao
 */
public interface WindowRange {

    /**
     * Verify that the parameters are valid
     */
    default void check() {}

    /**
     * is the start boundary fixed
     */
    default boolean isFixedStartIndex() {
        return false;
    }

    /**
     * is the end boundary fixed
     */
    default boolean isFixedEndIndex() {
        return false;
    }

    /**
     * get the window sliding start boundary
     * @param currentRowIndex the current row index
     * @param windowList the current window data
     */
    <T> Integer getStartIndex(Integer currentRowIndex, List<T> windowList);

    /**
     * get the window sliding end boundary
     * @param currentRowIndex the current row index
     * @param windowList the current window data
     */
    <T> Integer getEndIndex(Integer currentRowIndex, List<T> windowList);

    default boolean eq(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (this.getClass() == obj.getClass()) {
            return true;
        }
        return false;
    }
}
