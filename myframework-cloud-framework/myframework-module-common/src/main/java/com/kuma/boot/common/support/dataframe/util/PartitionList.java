/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.dataframe.util;

import com.kuma.boot.common.utils.log.LogUtils;
import java.math.RoundingMode;
import java.util.AbstractList;
import java.util.List;

public class PartitionList<T> extends AbstractList<List<T>> {
    final List<T> list;
    final int size;

    public PartitionList(List<T> list, int size) {
        this.list = list;
        this.size = size;
    }

    @Override
    public List<T> get(int index) {
        PartitionList.checkElementIndex(index, this.size(), "index");
        int start = index * this.size;
        int end = Math.min(start + this.size, this.list.size());
        return this.list.subList(start, end);
    }

    @Override
    public int size() {
        return PartitionList.divide(this.list.size(), this.size, RoundingMode.CEILING);
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public static int checkElementIndex(int index, int size, String desc) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(PartitionList.badElementIndex(index, size, desc));
        }
        return index;
    }

    private static String badElementIndex(int index, int size, String desc) {
        if (index < 0) {
            return PartitionList.lenientFormat("%s (%s) must not be negative", desc, index);
        }
        if (size < 0) {
            throw new IllegalArgumentException("negative size: " + size);
        }
        return PartitionList.lenientFormat("%s (%s) must be less than size (%s)", desc, index, size);
    }

    public static String lenientFormat(String template, Object ... args) {
        int placeholderStart;
        template = String.valueOf(template);
        if (args == null) {
            args = new Object[]{"(Object[])null"};
        } else {
            for (int i = 0; i < args.length; ++i) {
                args[i] = PartitionList.lenientToString(args[i]);
            }
        }
        StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while (i < args.length && (placeholderStart = template.indexOf("%s", templateStart)) != -1) {
            builder.append(template, templateStart, placeholderStart);
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template, templateStart, template.length());
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);
            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append(']');
        }
        return builder.toString();
    }

    public static int divide(int p, int q, RoundingMode mode) {
        boolean increment;
        if (q == 0) {
            throw new ArithmeticException("/ by zero");
        }
        int div = p / q;
        int rem = p - q * div;
        if (rem == 0) {
            return div;
        }
        int signum = 1 | (p ^ q) >> 31;
        switch (mode) {
            case UNNECESSARY: {
                PartitionList.checkRoundingUnnecessary(rem == 0);
            }
            case DOWN: {
                increment = false;
                break;
            }
            case UP: {
                increment = true;
                break;
            }
            case CEILING: {
                increment = signum > 0;
                break;
            }
            case FLOOR: {
                increment = signum < 0;
                break;
            }
            case HALF_EVEN:
            case HALF_DOWN:
            case HALF_UP: {
                int absRem = Math.abs(rem);
                int cmpRemToHalfDivisor = absRem - (Math.abs(q) - absRem);
                if (cmpRemToHalfDivisor == 0) {
                    increment = mode == RoundingMode.HALF_UP || mode == RoundingMode.HALF_EVEN & (div & 1) != 0;
                    break;
                }
                increment = cmpRemToHalfDivisor > 0;
                break;
            }
            default: {
                throw new AssertionError();
            }
        }
        return increment ? div + signum : div;
    }

    static void checkRoundingUnnecessary(boolean condition) {
        if (!condition) {
            throw new ArithmeticException("mode was UNNECESSARY, but rounding was necessary");
        }
    }

    private static String lenientToString(Object o) {
        if (o == null) {
            return "null";
        }
        try {
            return o.toString();
        }
        catch (Exception e) {
            String objectToString = o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o));
            LogUtils.warn("Exception during lenientFormat for " + objectToString, e);
            return "<" + objectToString + " threw " + e.getClass().getName() + ">";
        }
    }
}

