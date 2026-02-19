/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.excel.metadata.Head
 *  com.alibaba.excel.write.merge.AbstractMergeStrategy
 *  org.apache.commons.collections4.CollectionUtils
 *  org.apache.poi.ss.usermodel.Cell
 *  org.apache.poi.ss.usermodel.Sheet
 *  org.apache.poi.ss.util.CellRangeAddress
 */
package com.kuma.boot.office.easyexcel.easyexcelconvert.core;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.kuma.boot.office.easyexcel.easyexcelconvert.annotation.CellMerge;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

public class CellMergeStrategy
extends AbstractMergeStrategy {
    private List<?> list;
    private boolean hasTitle;

    public CellMergeStrategy(List<?> list, boolean hasTitle) {
        this.list = list;
        this.hasTitle = hasTitle;
    }

    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        List<CellRangeAddress> cellList = null;
        try {
            cellList = CellMergeStrategy.handle(this.list, this.hasTitle);
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        if (CollectionUtils.isNotEmpty(cellList) && cell.getRowIndex() == 1 && cell.getColumnIndex() == 0) {
            for (CellRangeAddress item : cellList) {
                sheet.addMergedRegion(item);
            }
        }
    }

    private static List<CellRangeAddress> handle(List<?> list, boolean hasTitle) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ArrayList<CellRangeAddress> cellList = new ArrayList<CellRangeAddress>();
        if (CollectionUtils.isEmpty(list)) {
            return cellList;
        }
        Class<?> clazz = list.get(0).getClass();
        Field[] fields = clazz.getDeclaredFields();
        ArrayList<Field> mergeFields = new ArrayList<Field>();
        ArrayList<Integer> mergeFieldsIndex = new ArrayList<Integer>();
        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            if (!field.isAnnotationPresent(CellMerge.class)) continue;
            CellMerge cm = field.getAnnotation(CellMerge.class);
            mergeFields.add(field);
            mergeFieldsIndex.add(cm.index() == -1 ? i : cm.index());
        }
        int rowIndex = hasTitle ? 1 : 0;
        HashMap<Field, RepeatCell> map = new HashMap<Field, RepeatCell>();
        for (int i = 0; i < list.size(); ++i) {
            for (int j = 0; j < mergeFields.size(); ++j) {
                Field field = (Field)mergeFields.get(j);
                String name = field.getName();
                String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                Method readMethod = clazz.getMethod(methodName, new Class[0]);
                Object val = readMethod.invoke(list.get(i), new Object[0]);
                int colNum = (Integer)mergeFieldsIndex.get(j);
                if (!map.containsKey(field)) {
                    map.put(field, new RepeatCell(val, i));
                    continue;
                }
                RepeatCell repeatCell = (RepeatCell)map.get(field);
                Object cellValue = repeatCell.getValue();
                if (cellValue == null || "".equals(cellValue)) continue;
                if (cellValue != val) {
                    if (i - repeatCell.getCurrent() > 1) {
                        cellList.add(new CellRangeAddress(repeatCell.getCurrent() + rowIndex, i + rowIndex - 1, colNum, colNum));
                    }
                    map.put(field, new RepeatCell(val, i));
                    continue;
                }
                if (i != list.size() - 1 || i <= repeatCell.getCurrent()) continue;
                cellList.add(new CellRangeAddress(repeatCell.getCurrent() + rowIndex, i + rowIndex, colNum, colNum));
            }
        }
        return cellList;
    }

    public List<?> getList() {
        return this.list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    public boolean isHasTitle() {
        return this.hasTitle;
    }

    public void setHasTitle(boolean hasTitle) {
        this.hasTitle = hasTitle;
    }

    public static class RepeatCell {
        private Object value;
        private int current;

        public RepeatCell(Object value, int current) {
            this.value = value;
            this.current = current;
        }

        public Object getValue() {
            return this.value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public int getCurrent() {
            return this.current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }
    }
}

