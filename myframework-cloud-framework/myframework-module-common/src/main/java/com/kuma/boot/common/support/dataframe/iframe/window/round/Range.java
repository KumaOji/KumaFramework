/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

/**
 * WindowRange Builder
 *
 * @author caizhihao
 */
public class Range {

    private Range() {}

    /**
     * The first row of the window
     */
    public static final WindowRange START_ROW = new StartRowRange();

    /**
     * The first 0 row of the current row
     */
    public static final WindowRange BEFORE_ROW = new BeforeRange(0);

    /**
     * The current row of the window
     */
    public static final WindowRange CURRENT_ROW = new CurrentRowRange();

    /**
     * The last 0 row of the current row
     */
    public static final WindowRange AFTER_ROW = new AfterRange(0);

    /**
     * The last row of the window
     */
    public static final WindowRange END_ROW = new EndRowRange();

    /**
     * The first n row of the current row
     */
    public static WindowRange BEFORE(int n) {
        return new BeforeRange(n);
    }

    /**
     * The last n row of the current row
     */
    public static WindowRange AFTER(int n) {
        return new AfterRange(n);
    }
}
