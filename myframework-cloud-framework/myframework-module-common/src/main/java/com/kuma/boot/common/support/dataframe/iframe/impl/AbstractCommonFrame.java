/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSON
 */
package com.kuma.boot.common.support.dataframe.iframe.impl;

import com.alibaba.fastjson2.JSON;
import com.kuma.boot.common.support.dataframe.iframe.IFrame;
import com.kuma.boot.common.support.dataframe.iframe.item.FI2;
import com.kuma.boot.common.support.dataframe.util.ClassUtil;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractCommonFrame<T>
implements IFrame<T> {
    protected static final String MSG = "****";
    protected List<String> fieldList = new ArrayList<String>();
    private Map<String, Field> fieldNameMap = new HashMap<String, Field>();
    protected Class<?> fieldClass;
    protected int defaultScale = 2;
    protected RoundingMode defaultRoundingMode = RoundingMode.HALF_UP;

    public void setFieldList(List<String> fieldList) {
        this.fieldList = fieldList;
    }

    public Map<String, Field> getFieldNameMap() {
        return this.fieldNameMap;
    }

    public void setFieldNameMap(Map<String, Field> fieldNameMap) {
        this.fieldNameMap = fieldNameMap;
    }

    public Class<?> getFieldClass() {
        return this.fieldClass;
    }

    public void setFieldClass(Class<?> fieldClass) {
        this.fieldClass = fieldClass;
    }

    public int getDefaultScale() {
        return this.defaultScale;
    }

    public void setDefaultScale(int defaultScale) {
        this.defaultScale = defaultScale;
    }

    public RoundingMode getDefaultRoundingMode() {
        return this.defaultRoundingMode;
    }

    public void setDefaultRoundingMode(RoundingMode defaultRoundingMode) {
        this.defaultRoundingMode = defaultRoundingMode;
    }

    protected abstract List<T> viewList();

    protected int getOldRoundingMode() {
        switch (this.defaultRoundingMode) {
            case UP: {
                return 0;
            }
            case DOWN: {
                return 1;
            }
            case CEILING: {
                return 2;
            }
            case FLOOR: {
                return 3;
            }
            case HALF_UP: {
                return 4;
            }
            case HALF_DOWN: {
                return 5;
            }
            case HALF_EVEN: {
                return 6;
            }
            case UNNECESSARY: {
                return 7;
            }
        }
        throw new IllegalArgumentException("can not find round mode for " + String.valueOf((Object)this.defaultRoundingMode));
    }

    protected void initDefaultScale(int scale, RoundingMode roundingMode) {
        this.defaultScale = scale;
        this.defaultRoundingMode = roundingMode;
    }

    protected void transmitMember(AbstractCommonFrame<?> from, AbstractCommonFrame<?> toFrame) {
        toFrame.defaultScale = from.defaultScale;
        toFrame.defaultRoundingMode = from.defaultRoundingMode;
    }

    public List<String> getFieldList() {
        if (!this.fieldList.isEmpty()) {
            return this.fieldList;
        }
        if (this.fieldClass == null) {
            return Collections.emptyList();
        }
        List<Field> allFiled = ClassUtil.findAllFiled(this.fieldClass);
        for (Field field : allFiled) {
            String name = field.getName();
            this.fieldNameMap.put(name, field);
            this.fieldList.add(name);
        }
        return this.fieldList;
    }

    protected StringBuilder getShowString(int n) {
        if (this.fieldClass == null) {
            return new StringBuilder();
        }
        if (this.isNormalType(this.fieldClass)) {
            return new StringBuilder(JSON.toJSONString(this.viewList()));
        }
        String[][] dataArr = this.buildPrintDataArr(n);
        if (dataArr == null || dataArr.length <= 0) {
            return new StringBuilder("\n");
        }
        StringBuilder sb = new StringBuilder("\n");
        for (int i = 0; i < dataArr.length; ++i) {
            for (int j = 0; j < dataArr[0].length; ++j) {
                sb.append(dataArr[i][j].replace(MSG, "\t"));
            }
        }
        return sb;
    }

    /*
     * WARNING - void declaration
     */
    protected String[][] buildPrintDataArr(int limit) {
        void var9_15;
        List<T> dataList = this.viewList();
        if (dataList.isEmpty()) {
            return null;
        }
        List<String> filedList = this.getFieldList();
        int rowLen = Math.min(limit, dataList.size()) + 1;
        int colLen = filedList.size() * 2 + 1;
        String[][] dataArr = new String[rowLen][colLen];
        int index1 = 0;
        for (String string : filedList) {
            dataArr[0][index1++] = string;
            dataArr[0][index1++] = MSG;
        }
        dataArr[0][index1] = "\n";
        index1 = 0;
        if (dataList.isEmpty()) {
            for (String string : filedList) {
                dataArr[1][index1++] = "";
                dataArr[1][index1++] = MSG;
            }
        }
        int row = 1;
        for (T t : dataList) {
            if (row > limit) break;
            int tmpIndex = 0;
            for (String fieldName : filedList) {
                try {
                    Field field = this.fieldNameMap.get(fieldName);
                    field.setAccessible(true);
                    Object o = field.get(t);
                    dataArr[row][tmpIndex++] = o == null ? "" : o.toString();
                    dataArr[row][tmpIndex++] = MSG;
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            dataArr[row][tmpIndex] = "\n";
            ++row;
        }
        boolean bl = false;
        while (var9_15 < colLen - 1) {
            int j;
            int maxStrLen = -1;
            for (j = 0; j < rowLen; ++j) {
                if (Objects.equals(dataArr[j][var9_15], MSG) || dataArr[j][var9_15].length() <= maxStrLen) continue;
                maxStrLen = AbstractCommonFrame.getStrLength(dataArr[j][var9_15]);
            }
            if (maxStrLen != -1) {
                for (j = 0; j < rowLen; ++j) {
                    int need = maxStrLen - AbstractCommonFrame.getStrLength(dataArr[j][var9_15]);
                    if (need <= 0) continue;
                    dataArr[j][var9_15] = dataArr[j][var9_15] + this.getSpace(need);
                }
            }
            ++var9_15;
        }
        return dataArr;
    }

    private String getSpace(int need) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < need; ++i) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static int getStrLength(String str) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (AbstractCommonFrame.isChineseChar(c)) {
                count += 2;
                continue;
            }
            ++count;
        }
        return count;
    }

    private static boolean isChineseChar(char checkChar) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(checkChar);
        return Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS == ub || Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS == ub || Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS == ub || Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT == ub || Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A == ub || Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B == ub;
    }

    protected boolean isNormalType(Class<?> fieldClass) {
        if (fieldClass.isPrimitive()) {
            return true;
        }
        if (String.class.equals(fieldClass)) {
            return true;
        }
        List<Class> classes = Arrays.asList(Integer.class, Boolean.class, Double.class, Float.class, BigDecimal.class, Long.class, Byte.class, Short.class, Character.class);
        if (classes.contains(fieldClass)) {
            return true;
        }
        if (fieldClass.isArray() || Collection.class.isAssignableFrom(fieldClass) || Map.class.isAssignableFrom(fieldClass)) {
            return false;
        }
        if (fieldClass.getClassLoader().equals(this.getClass().getClassLoader())) {
            return false;
        }
        return false;
    }

    protected Type[] getSuperClassActualTypeArguments(Class<?> clz) {
        Type superclass = clz.getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            return ((ParameterizedType)superclass).getActualTypeArguments();
        }
        return null;
    }

    protected Type[] getSuperInterfaceActualTypeArguments(Class<?> clz) {
        Type[] genericInterfaces = clz.getGenericInterfaces();
        if (genericInterfaces[0] instanceof ParameterizedType) {
            return ((ParameterizedType)genericInterfaces[0]).getActualTypeArguments();
        }
        return null;
    }

    protected static <R extends Number> R bigDecimalToClassValue(BigDecimal value, Class<R> valueClass) {
        if (value == null) {
            return null;
        }
        if (BigDecimal.class.equals(valueClass)) {
            return (R)value;
        }
        if (Byte.class.equals(valueClass)) {
            return (R)((Number)valueClass.cast(value.byteValue()));
        }
        if (Short.class.equals(valueClass)) {
            return (R)((Number)valueClass.cast(value.shortValue()));
        }
        if (Integer.class.equals(valueClass) || Integer.TYPE.equals(valueClass)) {
            return (R)Integer.valueOf(value.intValue());
        }
        if (Long.class.equals(valueClass) || Long.TYPE.equals(valueClass)) {
            return (R)Long.valueOf(value.longValue());
        }
        if (Float.class.equals(valueClass)) {
            return (R)Float.valueOf(value.floatValue());
        }
        if (Double.class.equals(valueClass)) {
            return (R)Double.valueOf(value.doubleValue());
        }
        throw new IllegalArgumentException("Unsupported Number class: " + valueClass.getName());
    }

    protected List<FI2<T, Integer>> rankingSameAsc(List<T> data, Comparator<T> comparator) {
        return this.rankingSameAsc(data, comparator, data.size());
    }

    protected List<FI2<T, Integer>> rankingSameAsc(List<T> data, Comparator<T> comparator, int n) {
        if (data.isEmpty()) {
            return Collections.emptyList();
        }
        if (n <= 0) {
            throw new IllegalArgumentException("first N should greater than zero");
        }
        data.sort(comparator);
        int rank = 1;
        ArrayList<FI2<T, Integer>> tmpDataList = new ArrayList<FI2<T, Integer>>();
        tmpDataList.add(new FI2<T, Integer>(data.get(0), 1));
        for (int i = 1; i < data.size(); ++i) {
            T cur;
            T pre = data.get(i - 1);
            if (comparator.compare(pre, cur = data.get(i)) != 0) {
                ++rank;
            }
            if (rank > n) break;
            tmpDataList.add(new FI2<T, Integer>(cur, rank));
        }
        return tmpDataList;
    }
}

