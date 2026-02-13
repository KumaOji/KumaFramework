/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.tuple.tuple;

import com.kuma.boot.common.support.tuple.tuple.Tuple1;
import com.kuma.boot.common.support.tuple.tuple.Tuple2;
import com.kuma.boot.common.support.tuple.tuple.Tuple3;
import com.kuma.boot.common.support.tuple.tuple.Tuple4;
import com.kuma.boot.common.support.tuple.tuple.Tuple5;
import com.kuma.boot.common.support.tuple.tuple.TupleN;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class Tuple
implements Iterable<Object>,
Serializable {
    private final List<Object> valueList;

    Tuple(Object ... objects) {
        this.valueList = Arrays.asList(objects);
    }

    public final List<Object> toList() {
        return new ArrayList<Object>(this.valueList);
    }

    public final Object[] toArray() {
        return this.valueList.toArray();
    }

    public final int size() {
        return this.valueList.size();
    }

    public final <T> T get(int pos) {
        return (T)this.valueList.get(pos);
    }

    public final boolean contains(Object value) {
        return this.valueList.contains(value);
    }

    @Override
    public final Iterator<Object> iterator() {
        return this.valueList.iterator();
    }

    @Override
    public final Spliterator<Object> spliterator() {
        return this.valueList.spliterator();
    }

    public final Stream<Object> stream() {
        return this.valueList.stream();
    }

    public final Stream<Object> parallelStream() {
        return this.valueList.parallelStream();
    }

    @Override
    public final void forEach(Consumer<? super Object> action) {
        Objects.requireNonNull(action, "action is null");
        this.valueList.forEach(action);
    }

    public final void forEachWithIndex(BiConsumer<Integer, ? super Object> action) {
        Objects.requireNonNull(action, "action is null");
        int length = this.valueList.size();
        for (int i = 0; i < length; ++i) {
            action.accept(i, this.valueList.get(i));
        }
    }

    public final Tuple subTuple(int start, int end) {
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("start, end must >= 0");
        }
        if (end >= this.valueList.size()) {
            throw new IllegalArgumentException("this tuple's size is" + this.valueList.size());
        }
        int length = end - start + 1;
        if (length <= 0) {
            throw new IllegalArgumentException("end must >= start");
        }
        if (start == 0 && end == this.valueList.size() - 1) {
            return this;
        }
        switch (length) {
            case 1: {
                return Tuple1.with(this.valueList.get(start));
            }
            case 2: {
                return Tuple2.with(this.valueList.get(start), this.valueList.get(end));
            }
            case 3: {
                return Tuple3.with(this.valueList.get(start), this.valueList.get(start + 1), this.valueList.get(end));
            }
            case 4: {
                return Tuple4.with(this.valueList.get(start), this.valueList.get(start + 1), this.valueList.get(start + 2), this.valueList.get(end));
            }
            case 5: {
                return Tuple5.with(this.valueList.get(start), this.valueList.get(start + 1), this.valueList.get(start + 2), this.valueList.get(start + 3), this.valueList.get(end));
            }
        }
        return TupleN.with(this.valueList.subList(start, end));
    }

    public final Tuple add(Tuple ... tuples) {
        Objects.requireNonNull(tuples, "tuple is null");
        if (tuples.length == 0) {
            return this;
        }
        List<Object> temp = this.toList();
        for (Tuple t : tuples) {
            temp.addAll(t.valueList);
        }
        switch (temp.size()) {
            case 1: {
                return Tuple1.with(temp.get(0));
            }
            case 2: {
                return Tuple2.with(temp.get(0), temp.get(1));
            }
            case 3: {
                return Tuple3.with(temp.get(0), temp.get(1), temp.get(2));
            }
            case 4: {
                return Tuple4.with(temp.get(0), temp.get(1), temp.get(2), temp.get(3));
            }
            case 5: {
                return Tuple5.with(temp.get(0), temp.get(1), temp.get(2), temp.get(3), temp.get(4));
            }
        }
        return TupleN.with(temp.toArray());
    }

    public final Tuple repeat(int times) {
        if (times < 0) {
            throw new IllegalArgumentException("times must >= 0");
        }
        if (times == 0) {
            return this;
        }
        return TupleN.with(IntStream.range(0, times).mapToObj(i -> this.valueList.toArray()).reduce((a, b) -> {
            Object[] temp = new Object[((Object[])a).length + ((Object[])b).length];
            System.arraycopy(a, 0, temp, 0, ((Object[])a).length);
            System.arraycopy(b, 0, temp, ((Object[])a).length, ((Object[])b).length);
            return temp;
        }).get());
    }

    public final boolean equals(Object obj) {
        if (Objects.isNull(obj)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof Tuple) {
            return ((Tuple)obj).valueList.equals(this.valueList);
        }
        return false;
    }

    public final int hashCode() {
        return this.valueList.hashCode();
    }

    public final String toString() {
        return this.valueList.stream().map(Objects::toString).collect(Collectors.joining(", ", "(", ")"));
    }

    public abstract Tuple reverse();
}

