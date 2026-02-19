/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.javers;

import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.Set;

/**
 * DataCompare
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class DataCompare<T> implements Serializable {

    private static final long serialVersionUID = -1223448352971587416L;
    private Set<T> addSet;
    private Set<T> removedSet;
    private Set<T> changeSet;

    public DataCompare() {
        this.addSet = Sets.newHashSet();
        this.changeSet = Sets.newHashSet();
        this.removedSet = Sets.newHashSet();
    }

    public Set<T> getAddSet() {
        return addSet;
    }

    public void setAddSet( Set<T> addSet ) {
        this.addSet = addSet;
    }

    public Set<T> getRemovedSet() {
        return removedSet;
    }

    public void setRemovedSet( Set<T> removedSet ) {
        this.removedSet = removedSet;
    }

    public Set<T> getChangeSet() {
        return changeSet;
    }

    public void setChangeSet( Set<T> changeSet ) {
        this.changeSet = changeSet;
    }
}
