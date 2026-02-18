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

package com.kuma.boot.common.support.async.worker;

import com.kuma.boot.common.support.async.wrapper.WorkerWrapper;
import java.util.Objects;

/**
 * 对依赖的wrapper的封装
 *
 * @author wuweifeng wrote on 2019-12-20
 * @version 1.0
 */
public class DependWrapper {

    private WorkerWrapper<?, ?> dependWrapper;

    /**
     * 是否该依赖必须完成后才能执行自己.
     * <p>
     * 因为存在一个任务，依赖于多个任务，是让这多个任务全部完成后才执行自己，还是某几个执行完毕就可以执行自己 如 1 ---3 2 或 1---3 2---3
     * 这两种就不一样，上面的就是必须12都完毕，才能3 下面的就是1完毕就可以3
     */
    private boolean must = true;

    public DependWrapper(WorkerWrapper<?, ?> dependWrapper, boolean must) {
        this.dependWrapper = dependWrapper;
        this.must = must;
    }

    public DependWrapper() {}

    public WorkerWrapper<?, ?> getDependWrapper() {
        return dependWrapper;
    }

    public void setDependWrapper(WorkerWrapper<?, ?> dependWrapper) {
        this.dependWrapper = dependWrapper;
    }

    public boolean isMust() {
        return must;
    }

    public void setMust(boolean must) {
        this.must = must;
    }

    @Override
    public String toString() {
        return "DependWrapper{" + "dependWrapper=" + dependWrapper + ", must=" + must + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DependWrapper that = (DependWrapper) o;
        return must == that.must && Objects.equals(dependWrapper, that.dependWrapper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependWrapper, must);
    }
}
