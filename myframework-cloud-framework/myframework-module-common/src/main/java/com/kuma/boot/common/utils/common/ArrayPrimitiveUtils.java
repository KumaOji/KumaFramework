/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.xkzhangsan.time.utils.CollectionUtil
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.support.handler.Handler;
import com.xkzhangsan.time.utils.CollectionUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ArrayPrimitiveUtils {
    public static final int[] INT_EMPTY = new int[0];
    public static final short[] SHORT_EMPTY = new short[0];
    public static final long[] LONG_EMPTY = new long[0];
    public static final float[] FLOAT_EMPTY = new float[0];
    public static final double[] DOUBLE_EMPTY = new double[0];
    public static final char[] CHAR_EMPTY = new char[0];
    public static final byte[] BYTE_EMPTY = new byte[0];
    public static final boolean[] BOOLEAN_EMPTY = new boolean[0];

    private ArrayPrimitiveUtils() {
    }

    public static boolean isEmpty(int[] objects) {
        return null == objects || objects.length <= 0;
    }

    public static boolean isNotEmpty(int[] objects) {
        return !ArrayPrimitiveUtils.isEmpty(objects);
    }

    public static boolean isEmpty(boolean[] objects) {
        return null == objects || objects.length <= 0;
    }

    public static boolean isNotEmpty(boolean[] objects) {
        return !ArrayPrimitiveUtils.isEmpty(objects);
    }

    public static boolean isEmpty(char[] objects) {
        return null == objects || objects.length <= 0;
    }

    public static boolean isNotEmpty(char[] objects) {
        return !ArrayPrimitiveUtils.isEmpty(objects);
    }

    public static boolean isEmpty(byte[] objects) {
        return null == objects || objects.length <= 0;
    }

    public static boolean isNotEmpty(byte[] objects) {
        return !ArrayPrimitiveUtils.isEmpty(objects);
    }

    public static boolean isEmpty(long[] objects) {
        return null == objects || objects.length <= 0;
    }

    public static boolean isNotEmpty(long[] objects) {
        return !ArrayPrimitiveUtils.isEmpty(objects);
    }

    public static boolean isEmpty(float[] objects) {
        return null == objects || objects.length <= 0;
    }

    public static boolean isNotEmpty(float[] objects) {
        return !ArrayPrimitiveUtils.isEmpty(objects);
    }

    public static boolean isEmpty(double[] objects) {
        return null == objects || objects.length <= 0;
    }

    public static boolean isNotEmpty(double[] objects) {
        return !ArrayPrimitiveUtils.isEmpty(objects);
    }

    public static boolean isEmpty(short[] objects) {
        return null == objects || objects.length <= 0;
    }

    public static boolean isNotEmpty(short[] objects) {
        return !ArrayPrimitiveUtils.isEmpty(objects);
    }

    public static <K> List<K> toList(boolean[] values, Handler<? super Boolean, K> keyFunction) {
        if (ArrayPrimitiveUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        ArrayList<K> list = new ArrayList<K>(values.length);
        for (boolean value : values) {
            K key = keyFunction.handle(value);
            list.add(key);
        }
        return list;
    }

    public static <K> List<K> toList(char[] values, Handler<? super Character, K> keyFunction) {
        if (ArrayPrimitiveUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        ArrayList<K> list = new ArrayList<K>(values.length);
        for (char value : values) {
            K key = keyFunction.handle(Character.valueOf(value));
            list.add(key);
        }
        return list;
    }

    public static <K> List<K> toList(byte[] values, Handler<? super Byte, K> keyFunction) {
        if (ArrayPrimitiveUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        ArrayList<K> list = new ArrayList<K>(values.length);
        for (byte value : values) {
            K key = keyFunction.handle(value);
            list.add(key);
        }
        return list;
    }

    public static <K> List<K> toList(short[] values, Handler<? super Short, K> keyFunction) {
        if (ArrayPrimitiveUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        ArrayList<K> list = new ArrayList<K>(values.length);
        for (short value : values) {
            K key = keyFunction.handle(value);
            list.add(key);
        }
        return list;
    }

    public static <K> List<K> toList(int[] values, Handler<? super Integer, K> keyFunction) {
        if (ArrayPrimitiveUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        ArrayList<K> list = new ArrayList<K>(values.length);
        for (int value : values) {
            K key = keyFunction.handle(value);
            list.add(key);
        }
        return list;
    }

    public static <K> List<K> toList(float[] values, Handler<? super Float, K> keyFunction) {
        if (ArrayPrimitiveUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        ArrayList<K> list = new ArrayList<K>(values.length);
        for (float value : values) {
            K key = keyFunction.handle(Float.valueOf(value));
            list.add(key);
        }
        return list;
    }

    public static <K> List<K> toList(double[] values, Handler<? super Double, K> keyFunction) {
        if (ArrayPrimitiveUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        ArrayList<K> list = new ArrayList<K>(values.length);
        for (double value : values) {
            K key = keyFunction.handle(value);
            list.add(key);
        }
        return list;
    }

    public static <K> List<K> toList(long[] values, Handler<? super Long, K> keyFunction) {
        if (ArrayPrimitiveUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        ArrayList<K> list = new ArrayList<K>(values.length);
        for (long value : values) {
            K key = keyFunction.handle(value);
            list.add(key);
        }
        return list;
    }

    public static int indexOf(char[] chars, char c) {
        if (ArrayPrimitiveUtils.isEmpty(chars)) {
            return -1;
        }
        for (int i = 0; i < chars.length; ++i) {
            char cs = chars[i];
            if (cs != c) continue;
            return i;
        }
        return -1;
    }

    public static boolean contains(boolean[] arrays, boolean val) {
        if (ArrayPrimitiveUtils.isEmpty(arrays)) {
            return false;
        }
        for (boolean va : arrays) {
            if (va != val) continue;
            return true;
        }
        return false;
    }

    public static boolean contains(byte[] arrays, byte val) {
        if (ArrayPrimitiveUtils.isEmpty(arrays)) {
            return false;
        }
        for (byte va : arrays) {
            if (va != val) continue;
            return true;
        }
        return false;
    }

    public static boolean contains(short[] arrays, short val) {
        if (ArrayPrimitiveUtils.isEmpty(arrays)) {
            return false;
        }
        for (short va : arrays) {
            if (va != val) continue;
            return true;
        }
        return false;
    }

    public static boolean contains(int[] arrays, int val) {
        if (ArrayPrimitiveUtils.isEmpty(arrays)) {
            return false;
        }
        for (int va : arrays) {
            if (va != val) continue;
            return true;
        }
        return false;
    }

    public static boolean contains(long[] arrays, long val) {
        if (ArrayPrimitiveUtils.isEmpty(arrays)) {
            return false;
        }
        for (long va : arrays) {
            if (va != val) continue;
            return true;
        }
        return false;
    }

    public static boolean contains(float[] arrays, float val) {
        if (ArrayPrimitiveUtils.isEmpty(arrays)) {
            return false;
        }
        for (float va : arrays) {
            if (va != val) continue;
            return true;
        }
        return false;
    }

    public static boolean contains(double[] arrays, double val) {
        if (ArrayPrimitiveUtils.isEmpty(arrays)) {
            return false;
        }
        for (double va : arrays) {
            if (va != val) continue;
            return true;
        }
        return false;
    }

    public static boolean contains(char[] chars, char c) {
        if (ArrayPrimitiveUtils.isEmpty(chars)) {
            return false;
        }
        for (char cs : chars) {
            if (cs != c) continue;
            return true;
        }
        return false;
    }

    public static int lastIndexOf(char[] chars, char c) {
        if (ArrayPrimitiveUtils.isEmpty(chars)) {
            return -1;
        }
        int lastIndex = -1;
        for (int i = 0; i < chars.length; ++i) {
            char cs = chars[i];
            if (cs != c) continue;
            lastIndex = i;
        }
        return lastIndex;
    }

    public static List<Integer> allIndexOf(char[] chars, char c) {
        if (ArrayPrimitiveUtils.isEmpty(chars)) {
            return Collections.emptyList();
        }
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        for (int i = 0; i < chars.length; ++i) {
            char cs = chars[i];
            if (cs != c) continue;
            indexList.add(i);
        }
        return indexList;
    }

    public static String getStringBeforeSymbol(char[] chars, int startIndex, char symbol) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean doubleQuotesStart = false;
        char preChar = ' ';
        for (int i = startIndex; i < chars.length; ++i) {
            char currentChar = chars[i];
            if ('\\' != (preChar = ArrayPrimitiveUtils.getPreChar(preChar, currentChar)) && '\"' == currentChar) {
                boolean bl = doubleQuotesStart = !doubleQuotesStart;
            }
            if (!doubleQuotesStart && symbol == currentChar) {
                return stringBuilder.toString();
            }
            stringBuilder.append(currentChar);
        }
        return stringBuilder.toString();
    }

    public static char getPreChar(char preChar, char currentChar) {
        if ('\\' == preChar && '\\' == currentChar) {
            return ' ';
        }
        return currentChar;
    }

    public static <E> int[] toIntArray(List<E> list, Handler<E, Integer> handler) {
        if (CollectionUtil.isEmpty(list)) {
            return INT_EMPTY;
        }
        int size = list.size();
        int[] ints = new int[size];
        for (int i = 0; i < size; ++i) {
            ints[i] = handler.handle(list.get(i));
        }
        return ints;
    }

    public static <E> boolean[] toBooleanArray(List<E> list, Handler<E, Boolean> handler) {
        if (CollectionUtil.isEmpty(list)) {
            return BOOLEAN_EMPTY;
        }
        int size = list.size();
        boolean[] arrays = new boolean[size];
        for (int i = 0; i < size; ++i) {
            arrays[i] = handler.handle(list.get(i));
        }
        return arrays;
    }

    public static <E> char[] toCharArray(List<E> list, Handler<E, Character> handler) {
        if (CollectionUtil.isEmpty(list)) {
            return CHAR_EMPTY;
        }
        int size = list.size();
        char[] arrays = new char[size];
        for (int i = 0; i < size; ++i) {
            arrays[i] = handler.handle(list.get(i)).charValue();
        }
        return arrays;
    }

    public static <E> byte[] toByteArray(List<E> list, Handler<E, Byte> handler) {
        if (CollectionUtil.isEmpty(list)) {
            return BYTE_EMPTY;
        }
        int size = list.size();
        byte[] arrays = new byte[size];
        for (int i = 0; i < size; ++i) {
            arrays[i] = handler.handle(list.get(i));
        }
        return arrays;
    }

    public static <E> short[] toShortArray(List<E> list, Handler<E, Short> handler) {
        if (CollectionUtil.isEmpty(list)) {
            return SHORT_EMPTY;
        }
        int size = list.size();
        short[] arrays = new short[size];
        for (int i = 0; i < size; ++i) {
            arrays[i] = handler.handle(list.get(i));
        }
        return arrays;
    }

    public static <E> long[] toLongArray(List<E> list, Handler<E, Long> handler) {
        if (CollectionUtil.isEmpty(list)) {
            return LONG_EMPTY;
        }
        int size = list.size();
        long[] arrays = new long[size];
        for (int i = 0; i < size; ++i) {
            arrays[i] = handler.handle(list.get(i));
        }
        return arrays;
    }

    public static <E> float[] toFloatArray(List<E> list, Handler<E, Float> handler) {
        if (CollectionUtil.isEmpty(list)) {
            return FLOAT_EMPTY;
        }
        int size = list.size();
        float[] arrays = new float[size];
        for (int i = 0; i < size; ++i) {
            arrays[i] = handler.handle(list.get(i)).floatValue();
        }
        return arrays;
    }

    public static <E> double[] toDoubleArray(List<E> list, Handler<E, Double> handler) {
        if (CollectionUtil.isEmpty(list)) {
            return DOUBLE_EMPTY;
        }
        int size = list.size();
        double[] arrays = new double[size];
        for (int i = 0; i < size; ++i) {
            arrays[i] = handler.handle(list.get(i));
        }
        return arrays;
    }

    public static int[] newArray(int ... arrays) {
        return arrays;
    }

    public static boolean[] newArray(boolean ... arrays) {
        return arrays;
    }

    public static char[] newArray(char ... arrays) {
        return arrays;
    }

    public static short[] newArray(short ... arrays) {
        return arrays;
    }

    public static long[] newArray(long ... arrays) {
        return arrays;
    }

    public static byte[] newArray(byte ... arrays) {
        return arrays;
    }

    public static float[] newArray(float ... arrays) {
        return arrays;
    }

    public static double[] newArray(double ... arrays) {
        return arrays;
    }
}

