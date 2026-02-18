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

package com.kuma.boot.common.support.tuple.impl;

import com.google.common.collect.Lists;
import com.kuma.boot.common.support.tuple.ITuple;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 元组抽象父类
 *
 * @author kuma
 * @version 2022.04
 * @since 2022-04-27 17:12:14
 */
public abstract class AbstractTuple implements ITuple {

    /** 列表信息 */
    private final List<Object> valueList;

    /**
     * 构造器
     * @param objects 元素数组
     */
    protected AbstractTuple(Object... objects) {
        this.valueList = Lists.newArrayList(objects);
    }

    @Override
    public int size() {
        return this.valueList.size();
    }

    @Override
    public boolean isEmpty() {
        return this.valueList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.valueList.contains(o);
    }

    @Override
    public Iterator<Object> iterator() {
        return this.valueList.iterator();
    }

    @Override
    public Object[] toArray() {
        return ArrayUtils.toArray(valueList);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.valueList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(int index) {
        return this.valueList.get(index);
    }

    @Override
    public Object set(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        return this.valueList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.valueList.lastIndexOf(o);
    }

    @Override
    public ListIterator<Object> listIterator() {
        return this.valueList.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return this.valueList.listIterator(index);
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Object> toList() {
        return Collections.unmodifiableList(new ArrayList<>(this.valueList));
    }
}
