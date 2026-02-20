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

package com.kuma.boot.core.startup;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Stat model witch hold children list.
 *
 * @author huzijie
 * @version ChildrenStat.java, v 0.1 2022年03月14日 12:18 PM huzijie Exp $
 */
public class ChildrenStat<T extends com.kuma.boot.core.startup.BaseStat> extends com.kuma.boot.core.startup.BaseStat {

    private List<T> children = new CopyOnWriteArrayList<>();

    public void addChild(T child) {
        this.children.add(child);
    }

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }
}
