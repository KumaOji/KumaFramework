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

package com.kuma.boot.common.support.dataframe.iframe.item;

import java.util.Objects;

/**
 * DataFrame （3 x 3 矩阵）
 *
 * @param <T1>
 * @param <T2>
 * @param <T3>
 * @author caizhihao
 */
public class FI3<T1, T2, T3> {

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FI3<?, ?, ?> fi3 = (FI3<?, ?, ?>) o;
        return Objects.equals(c1, fi3.c1)
                && Objects.equals(c2, fi3.c2)
                && Objects.equals(c3, fi3.c3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(c1, c2, c3);
    }

    private T1 c1;

    private T2 c2;

    private T3 c3;

    public FI3() {}

    public FI3(T1 c1, T2 c2, T3 c3) {
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
    }

    public T1 getC1() {
        return c1;
    }

    public void setC1(T1 c1) {
        this.c1 = c1;
    }

    public T2 getC2() {
        return c2;
    }

    public void setC2(T2 c2) {
        this.c2 = c2;
    }

    public T3 getC3() {
        return c3;
    }

    public void setC3(T3 c3) {
        this.c3 = c3;
    }
}
