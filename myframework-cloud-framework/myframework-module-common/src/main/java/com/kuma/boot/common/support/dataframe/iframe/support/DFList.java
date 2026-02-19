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

package com.kuma.boot.common.support.dataframe.iframe.support;

import com.kuma.boot.common.support.dataframe.iframe.JDFrame;
import com.kuma.boot.common.support.dataframe.iframe.SDFrame;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @param <T> 元素类型
 * @author caizhihao
 */
public class DFList<T> {

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    private List<T> data;

    public DFList(List<T> data) {
        this.data = data;
    }

    /**
     * 取前N个
     * @param n
     * @return
     */
    public DFList<T> first(int n) {
        if (data.isEmpty()) {
            return this;
        }

        if (n <= 0) {
            throw new IllegalArgumentException("first N should greater than zero");
        }

        if (n >= data.size()) {
            return this;
        }

        data = data.subList(0, n);
        return this;
    }

    public T first() {
        if (data.isEmpty()) {
            return null;
        }

        return data.get(0);
    }

    /**
     * 取后N个
     * @param n
     * @return
     */
    public DFList<T> last(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("last N should greater than zero");
        }

        if (n >= data.size()) {
            return this;
        }

        int start = data.size() - n + 1;
        data = data.subList(start, data.size());
        return this;
    }

    public DFList<T> sortDesc(Comparator<T> comparator) {
        data = data.stream().sorted(comparator.reversed()).collect(Collectors.toList());
        return this;
    }

    public DFList<T> sortAsc(Comparator<T> comparator) {
        data = data.stream().sorted(comparator).collect(Collectors.toList());
        return this;
    }

    public List<T> build() {
        return data;
    }

    public <K> Map<K, T> toMap(Function<T, K> function) {
        return data.stream().collect(Collectors.toMap(function, e -> e));
    }

    public <K, V> Map<K, V> toMap(Function<T, K> function, Function<T, V> function2) {
        return data.stream().collect(Collectors.toMap(function, function2));
    }

    public SDFrame<T> toSDFrame() {
        return SDFrame.read(this.data);
    }

    public JDFrame<T> toJDFrame() {
        return JDFrame.read(this.data);
    }
}
