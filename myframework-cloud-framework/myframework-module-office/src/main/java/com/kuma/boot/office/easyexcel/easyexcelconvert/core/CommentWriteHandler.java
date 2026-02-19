/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.excel.write.handler.RowWriteHandler
 *  com.alibaba.excel.write.metadata.holder.WriteSheetHolder
 *  com.alibaba.excel.write.metadata.holder.WriteTableHolder
 *  org.apache.poi.ss.usermodel.ClientAnchor
 *  org.apache.poi.ss.usermodel.Comment
 *  org.apache.poi.ss.usermodel.Drawing
 *  org.apache.poi.ss.usermodel.RichTextString
 *  org.apache.poi.ss.usermodel.Row
 *  org.apache.poi.ss.usermodel.Sheet
 *  org.apache.poi.xssf.usermodel.XSSFClientAnchor
 *  org.apache.poi.xssf.usermodel.XSSFRichTextString
 */
package com.kuma.boot.office.easyexcel.easyexcelconvert.core;

import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

public class CommentWriteHandler
implements RowWriteHandler {
    public void beforeRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Integer rowIndex, Integer relativeRowIndex, Boolean isHead) {
    }

    public void afterRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
    }

    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        Sheet sheet = writeSheetHolder.getSheet();
        Drawing drawingPatriarch = sheet.createDrawingPatriarch();
        Comment comment1 = drawingPatriarch.createCellComment((ClientAnchor)new XSSFClientAnchor(0, 0, 0, 0, 0, 0, 1, 1));
        comment1.setString((RichTextString)new XSSFRichTextString("\u6279\u6ce81"));
        sheet.getRow(0).getCell(0).setCellComment(comment1);
        Comment comment2 = drawingPatriarch.createCellComment((ClientAnchor)new XSSFClientAnchor(0, 0, 0, 0, 1, 0, 2, 1));
        comment2.setString((RichTextString)new XSSFRichTextString("\u6279\u6ce82"));
        sheet.getRow(0).getCell(1).setCellComment(comment2);
        Comment comment3 = drawingPatriarch.createCellComment((ClientAnchor)new XSSFClientAnchor(0, 0, 0, 0, 2, 0, 3, 1));
        comment3.setString((RichTextString)new XSSFRichTextString("\u6279\u6ce83"));
        sheet.getRow(0).getCell(2).setCellComment(comment3);
    }
}

