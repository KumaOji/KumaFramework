/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.excel.metadata.Head
 *  com.alibaba.excel.metadata.data.WriteCellData
 *  com.alibaba.excel.write.handler.CellWriteHandler
 *  com.alibaba.excel.write.metadata.holder.WriteSheetHolder
 *  com.alibaba.excel.write.metadata.holder.WriteTableHolder
 *  org.apache.poi.common.usermodel.HyperlinkType
 *  org.apache.poi.ss.usermodel.Cell
 *  org.apache.poi.ss.usermodel.CellStyle
 *  org.apache.poi.ss.usermodel.CreationHelper
 *  org.apache.poi.ss.usermodel.Font
 *  org.apache.poi.ss.usermodel.Hyperlink
 *  org.apache.poi.ss.usermodel.IndexedColors
 *  org.apache.poi.ss.usermodel.Row
 *  org.apache.poi.ss.usermodel.Workbook
 */
package com.kuma.boot.office.easyexcel.easyexcelconvert.core;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import java.util.List;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

public class CustomCellWriteHandler
implements CellWriteHandler {
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Head head, Integer integer, Integer integer1, Boolean aBoolean) {
    }

    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell, Head head, Integer integer, Boolean aBoolean) {
    }

    public void afterCellDataConverted(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, WriteCellData<?> cellData, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        super.afterCellDataConverted(writeSheetHolder, writeTableHolder, cellData, cell, head, relativeRowIndex, isHead);
    }

    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        boolean bool;
        if (isHead.booleanValue() && cell.getRowIndex() == 0 && cell.getColumnIndex() == 0) {
            CreationHelper helper = writeSheetHolder.getSheet().getWorkbook().getCreationHelper();
            Hyperlink hyperlink = helper.createHyperlink(HyperlinkType.URL);
            hyperlink.setAddress("https://github.com/alibaba/easyexcel");
            cell.setHyperlink(hyperlink);
        }
        boolean bl = bool = isHead != false && cell.getRowIndex() == 1;
        if (bool) {
            Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
            CellStyle cellStyle = workbook.createCellStyle();
            Font cellFont = workbook.createFont();
            cellFont.setBold(Boolean.TRUE.booleanValue());
            cellFont.setFontHeightInPoints((short)14);
            cellFont.setColor(IndexedColors.SEA_GREEN.getIndex());
            cellStyle.setFont(cellFont);
            cell.setCellStyle(cellStyle);
        }
    }
}

