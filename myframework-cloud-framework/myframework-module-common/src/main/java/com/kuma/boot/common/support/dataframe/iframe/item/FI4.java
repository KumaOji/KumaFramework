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
 * DataFrame （4 x 4 矩阵）
 *
 * @param <T1>
 * @param <T2>
 * @param <T3>
 * @param <T4>
 * @author caizhihao
 */
public class FI4<T1, T2, T3, T4> {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FI4<?, ?, ?, ?> fi4 = (FI4<?, ?, ?, ?>) o;
        return Objects.equals(c1, fi4.c1)
                && Objects.equals(c2, fi4.c2)
                && Objects.equals(c3, fi4.c3)
                && Objects.equals(c4, fi4.c4);
    }

    @Override
    public int hashCode() {
        return Objects.hash(c1, c2, c3, c4);
    }

    private T1 c1;

    private T2 c2;

    private T3 c3;

    private T4 c4;

    public FI4() {}

    public FI4(T1 c1, T2 c2, T3 c3, T4 c4) {
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.c4 = c4;
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

    public T4 getC4() {
        return c4;
    }

    public void setC4(T4 c4) {
        this.c4 = c4;
    }
}
