/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.excel.metadata.data.DataFormatData
 *  com.alibaba.excel.metadata.data.WriteCellData
 *  com.alibaba.excel.write.handler.context.CellWriteHandlerContext
 *  com.alibaba.excel.write.metadata.style.WriteCellStyle
 *  com.alibaba.excel.write.metadata.style.WriteFont
 *  com.alibaba.excel.write.style.HorizontalCellStyleStrategy
 *  org.apache.poi.ss.usermodel.BorderStyle
 *  org.apache.poi.ss.usermodel.HorizontalAlignment
 *  org.apache.poi.ss.usermodel.IndexedColors
 */
package com.kuma.boot.office.easyexcel.easyexcelcell;

import com.alibaba.excel.metadata.data.DataFormatData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

public class CellStyleStrategy
extends HorizontalCellStyleStrategy {
    private final WriteCellStyle headWriteCellStyle;
    private final WriteCellStyle contentWriteCellStyle;
    private final List<Integer> columnIndexes;

    public CellStyleStrategy(List<Integer> columnIndexes, WriteCellStyle headWriteCellStyle, WriteCellStyle contentWriteCellStyle) {
        this.columnIndexes = columnIndexes;
        this.headWriteCellStyle = headWriteCellStyle;
        this.contentWriteCellStyle = contentWriteCellStyle;
    }

    protected void setHeadCellStyle(CellWriteHandlerContext context) {
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontName("\u5b8b\u4f53");
        if (this.columnIndexes.get(0).equals(context.getRowIndex())) {
            this.headWriteCellStyle.setFillForegroundColor(Short.valueOf(IndexedColors.WHITE.getIndex()));
            this.headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
            headWriteFont.setFontHeightInPoints(Short.valueOf((short)12));
            headWriteFont.setBold(Boolean.valueOf(false));
            headWriteFont.setFontName("\u5b8b\u4f53");
        } else {
            this.headWriteCellStyle.setFillForegroundColor(Short.valueOf(IndexedColors.GREY_25_PERCENT.getIndex()));
            this.headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            headWriteFont.setFontHeightInPoints(Short.valueOf((short)11));
            headWriteFont.setBold(Boolean.valueOf(false));
            headWriteFont.setFontName("\u5fae\u8f6f\u96c5\u9ed1");
        }
        this.headWriteCellStyle.setWriteFont(headWriteFont);
        DataFormatData dataFormatData = new DataFormatData();
        dataFormatData.setIndex(Short.valueOf((short)49));
        this.headWriteCellStyle.setDataFormatData(dataFormatData);
        if (this.stopProcessing(context)) {
            return;
        }
        WriteCellData cellData = context.getFirstCellData();
        WriteCellStyle.merge((WriteCellStyle)this.headWriteCellStyle, (WriteCellStyle)cellData.getOrCreateStyle());
    }

    protected void setContentCellStyle(CellWriteHandlerContext context) {
        WriteFont contentWriteFont = new WriteFont();
        contentWriteFont.setFontName("\u5b8b\u4f53");
        contentWriteFont.setFontHeightInPoints(Short.valueOf((short)11));
        this.contentWriteCellStyle.setWriteFont(contentWriteFont);
        this.contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        this.contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        this.contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        this.contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        DataFormatData dataFormatData = new DataFormatData();
        dataFormatData.setIndex(Short.valueOf((short)49));
        this.contentWriteCellStyle.setDataFormatData(dataFormatData);
        this.contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        WriteCellData cellData = context.getFirstCellData();
        WriteCellStyle.merge((WriteCellStyle)this.contentWriteCellStyle, (WriteCellStyle)cellData.getOrCreateStyle());
    }
}

