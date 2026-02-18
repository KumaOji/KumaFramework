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

package com.kuma.boot.common.support.dataframe.iframe.support;

import java.util.Objects;

/**
 * MaxMin
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class MaxMin<T> {

    @Override
    public boolean equals( Object o ) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MaxMin<?> maxMin = (MaxMin<?>) o;
        return Objects.equals(max, maxMin.max) && Objects.equals(min, maxMin.min);
    }

    @Override
    public int hashCode() {
        return Objects.hash(max, min);
    }

    private T max;

    private T min;

    public MaxMin() {
    }

    public MaxMin( T max, T min ) {
        this.max = max;
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax( T max ) {
        this.max = max;
    }

    public T getMin() {
        return min;
    }

    public void setMin( T min ) {
        this.min = min;
    }
}
