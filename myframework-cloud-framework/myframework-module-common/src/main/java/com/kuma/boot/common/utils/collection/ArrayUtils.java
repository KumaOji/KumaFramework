/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.kuma.boot.common.utils.collection;

import com.google.common.collect.Lists;
import com.kuma.boot.common.support.handler.Handler;
import com.kuma.boot.common.utils.collection.ArrayPrimitiveUtils;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.reflect.ClassGenericUtils;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;

public final class ArrayUtils {
    public static final String[] STRING_EMPTY = new String[0];

    private ArrayUtils() {
    }

    public static boolean isEmpty(Object[] objects) {
        return null == objects || objects.length <= 0;
    }

    public static boolean isNotEmpty(Object[] objects) {
        return !ArrayUtils.isEmpty(objects);
    }

    public static <T> List<T> toList(T[] objects) {
        if (ArrayUtils.isEmpty(objects)) {
            return Collections.emptyList();
        }
        ArrayList objectList = new ArrayList(objects.length);
        objectList.addAll(Lists.newArrayList((Object[])objects));
        return objectList;
    }

    public static Object[] toArray(List<?> objectList) {
        if (CollectionUtils.isEmpty(objectList)) {
            return new Object[0];
        }
        Object[] objects = new Object[objectList.size()];
        for (int i = 0; i < objects.length; ++i) {
            objects[i] = objectList.get(i);
        }
        return objects;
    }

    public static <K, V> K[] toArray(V[] values, Handler<? super V, K> keyFunction) {
        if (ArrayUtils.isEmpty(values)) {
            return new Object[0];
        }
        Object[] resultArray = new Object[values.length];
        for (int i = 0; i < values.length; ++i) {
            K result = keyFunction.handle(values[i]);
            resultArray[i] = result;
        }
        return resultArray;
    }

    public static <K> K[] union(K[] values, K ... others) {
        if (ArrayUtils.isEmpty(values)) {
            return others;
        }
        if (ArrayUtils.isEmpty(others)) {
            return values;
        }
        Object[] resultArray = new Object[others.length];
        System.arraycopy(others, 0, resultArray, values.length, others.length);
        return resultArray;
    }

    public static boolean contains(Object[] array, Object objectToFind) {
        return ArrayUtils.indexOf(array, objectToFind) != -1;
    }

    public static boolean notContains(Object[] array, Object objectToFind) {
        return !ArrayUtils.contains(array, objectToFind);
    }

    public static int indexOf(Object[] array, Object objectToFind) {
        return ArrayUtils.indexOf(array, objectToFind, 0);
    }

    public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
        block5: {
            block4: {
                if (array == null) {
                    return -1;
                }
                if (startIndex < 0) {
                    startIndex = 0;
                }
                if (objectToFind != null) break block4;
                for (int i = startIndex; i < array.length; ++i) {
                    if (array[i] != null) continue;
                    return i;
                }
                break block5;
            }
            if (!array.getClass().getComponentType().isInstance(objectToFind)) break block5;
            for (int i = startIndex; i < array.length; ++i) {
                if (!objectToFind.equals(array[i])) continue;
                return i;
            }
        }
        return -1;
    }

    public static <R> R[] listToArray(List<R> list) {
        Class elemClass = ClassGenericUtils.getGenericClass(list);
        Object[] array = (Object[])Array.newInstance(elemClass, list.size());
        for (int i = 0; i < list.size(); ++i) {
            Array.set(array, i, list.get(i));
        }
        return array;
    }

    @SafeVarargs
    public static <E> List<E> arrayToList(E ... array) {
        if (ArrayUtils.isEmpty(array)) {
            return Lists.newArrayList();
        }
        return Lists.newArrayList((Object[])array);
    }

    public static int getStartIndex(int startIndex, Object[] arrays) {
        if (ArrayUtils.isEmpty(arrays)) {
            return 0;
        }
        if (startIndex < 0 || startIndex > arrays.length - 1) {
            return 0;
        }
        return startIndex;
    }

    public static int getEndIndex(int endIndex, Object[] arrays) {
        if (ArrayUtils.isEmpty(arrays)) {
            return 0;
        }
        int maxIndex = arrays.length - 1;
        if (endIndex < 0 || endIndex > maxIndex) {
            return maxIndex;
        }
        return endIndex;
    }

    public static Optional<Object> firstNotNullElem(Object[] objects) {
        if (ArrayUtils.isEmpty(objects)) {
            return Optional.empty();
        }
        for (Object elem : objects) {
            if (!ObjectUtils.isNotNull(elem)) continue;
            return Optional.of(elem);
        }
        return Optional.empty();
    }

    public static Object[] newArray(Object ... objects) {
        return objects;
    }

    public static <K, V> List<K> toList(V[] values, Handler<? super V, K> keyFunction) {
        if (ObjectUtils.isNull(values)) {
            return Collections.emptyList();
        }
        ArrayList list = Lists.newArrayList();
        for (V value : values) {
            K key = keyFunction.handle(value);
            list.add(key);
        }
        return list;
    }

    public static List toList(Object arrayObject, Handler keyFunction) {
        if (ObjectUtils.isNull(arrayObject)) {
            return Collections.emptyList();
        }
        Class<?> arrayClass = arrayObject.getClass();
        if (boolean[].class == arrayClass) {
            boolean[] booleans = (boolean[])arrayObject;
            return ArrayPrimitiveUtils.toList(booleans, keyFunction);
        }
        if (short[].class == arrayClass) {
            short[] shorts = (short[])arrayObject;
            return ArrayPrimitiveUtils.toList(shorts, keyFunction);
        }
        if (byte[].class == arrayClass) {
            byte[] bytes = (byte[])arrayObject;
            return ArrayPrimitiveUtils.toList(bytes, keyFunction);
        }
        if (int[].class == arrayClass) {
            int[] ints = (int[])arrayObject;
            return ArrayPrimitiveUtils.toList(ints, keyFunction);
        }
        if (float[].class == arrayClass) {
            float[] floats = (float[])arrayObject;
            return ArrayPrimitiveUtils.toList(floats, keyFunction);
        }
        if (double[].class == arrayClass) {
            double[] doubles = (double[])arrayObject;
            return ArrayPrimitiveUtils.toList(doubles, keyFunction);
        }
        if (char[].class == arrayClass) {
            char[] chars = (char[])arrayObject;
            return ArrayPrimitiveUtils.toList(chars, keyFunction);
        }
        if (long[].class == arrayClass) {
            long[] longs = (long[])arrayObject;
            return ArrayPrimitiveUtils.toList(longs, keyFunction);
        }
        Object[] objects = (Object[])arrayObject;
        return ArrayUtils.toList(objects, keyFunction);
    }

    public static Object[] shift(Object[] array, int offset) {
        if (ArrayUtils.isEmpty(array)) {
            return array;
        }
        int arrayLength = array.length;
        int actualOffset = offset;
        if (actualOffset < 0) {
            actualOffset += arrayLength;
        }
        Object[] newArray = new Object[arrayLength];
        for (int i = 0; i < arrayLength; ++i) {
            int realIndex = (i + actualOffset) % arrayLength;
            newArray[i] = array[realIndex];
        }
        return newArray;
    }

    public static <T> T[] createGenericArray(IntFunction<T[]> genericArrayCreator, int arrayLength) {
        return genericArrayCreator.apply(arrayLength);
    }

    public static <T> T[] createGenericArray(Class<T[]> classArrayT, int arrayLength) {
        return (Object[])Array.newInstance(classArrayT.getComponentType(), arrayLength);
    }
}

