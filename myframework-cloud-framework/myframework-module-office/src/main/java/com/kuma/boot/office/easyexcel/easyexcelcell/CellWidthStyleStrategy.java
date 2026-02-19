/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.excel.enums.CellDataTypeEnum
 *  com.alibaba.excel.metadata.Head
 *  com.alibaba.excel.metadata.data.CellData
 *  com.alibaba.excel.metadata.data.WriteCellData
 *  com.alibaba.excel.write.metadata.holder.WriteSheetHolder
 *  com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy
 *  org.apache.poi.ss.usermodel.Cell
 */
package com.kuma.boot.office.easyexcel.easyexcelcell;

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;

public class CellWidthStyleStrategy
extends AbstractColumnWidthStyleStrategy {
    private Map<Integer, Map<Integer, Integer>> CACHE = new HashMap<Integer, Map<Integer, Integer>>();

    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        Map<Integer, Integer> maxColumnWidthMap = this.CACHE.get(writeSheetHolder.getSheetNo());
        if (maxColumnWidthMap == null) {
            maxColumnWidthMap = new HashMap<Integer, Integer>();
            this.CACHE.put(writeSheetHolder.getSheetNo(), maxColumnWidthMap);
        }
        if (isHead.booleanValue()) {
            if (relativeRowIndex == 1) {
                Integer length = cell.getStringCellValue().getBytes().length;
                Integer maxColumnWidth = maxColumnWidthMap.get(cell.getColumnIndex());
                if (maxColumnWidth == null || length > maxColumnWidth) {
                    maxColumnWidthMap.put(cell.getColumnIndex(), length);
                    writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), length * 300);
                }
            }
        } else {
            Integer columnWidth = this.dataLength(cellDataList, cell, isHead);
            if (columnWidth >= 0) {
                Integer maxColumnWidth;
                if (columnWidth > 255) {
                    columnWidth = 255;
                }
                if ((maxColumnWidth = maxColumnWidthMap.get(cell.getColumnIndex())) == null || columnWidth > maxColumnWidth) {
                    maxColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
                    writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
                }
            }
        }
    }

    private Integer dataLength(List<WriteCellData<?>> cellDataList, Cell cell, Boolean isHead) {
        if (isHead.booleanValue()) {
            return cell.getStringCellValue().getBytes().length;
        }
        CellData cellData = (CellData)cellDataList.get(0);
        CellDataTypeEnum type = cellData.getType();
        if (type == null) {
            return -1;
        }
        switch (type) {
            case STRING: {
                return cellData.getStringValue().getBytes().length;
            }
            case BOOLEAN: {
                return cellData.getBooleanValue().toString().getBytes().length;
            }
            case NUMBER: {
                return cellData.getNumberValue().toString().getBytes().length;
            }
        }
        return -1;
    }
}

