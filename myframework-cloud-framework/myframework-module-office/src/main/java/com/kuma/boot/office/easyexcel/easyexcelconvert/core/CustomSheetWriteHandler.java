/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.excel.write.handler.SheetWriteHandler
 *  com.alibaba.excel.write.metadata.holder.WriteSheetHolder
 *  com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder
 *  org.apache.poi.ss.usermodel.DataValidation
 *  org.apache.poi.ss.usermodel.DataValidationConstraint
 *  org.apache.poi.ss.usermodel.DataValidationHelper
 *  org.apache.poi.ss.usermodel.Name
 *  org.apache.poi.ss.usermodel.Sheet
 *  org.apache.poi.ss.usermodel.Workbook
 *  org.apache.poi.ss.util.CellRangeAddressList
 */
package com.kuma.boot.office.easyexcel.easyexcelconvert.core;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;

public class CustomSheetWriteHandler
implements SheetWriteHandler {
    private final Map<Integer, String[]> map = new TreeMap<Integer, String[]>();
    private int index = 0;
    private final int batchSize = 2000;

    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
    }

    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        this.map.put(1, new String[]{"\u4e0b\u62c9\u5185\u5bb9\u4e00", "\u4e0b\u62c9\u5185\u5bb9\u4e8c"});
        this.map.put(3, new String[]{"\u5317\u4eac\u5e02", "\u4e0a\u6d77\u5e02", "\u91cd\u5e86\u5e02", "\u5929\u6d25\u5e02"});
        DataValidationHelper helper = writeSheetHolder.getSheet().getDataValidationHelper();
        this.map.forEach((k, v) -> {
            Workbook workbook = writeWorkbookHolder.getWorkbook();
            String sheetName = "sheet" + k;
            Sheet proviceSheet = workbook.createSheet(sheetName);
            ++this.index;
            workbook.setSheetHidden(this.index, true);
            int length = ((String[])v).length;
            for (int i = 0; i < length; ++i) {
                proviceSheet.createRow(i).createCell(0).setCellValue(v[i]);
            }
            Name category1Name = workbook.createName();
            category1Name.setNameName(sheetName);
            category1Name.setRefersToFormula(sheetName + "!$A$1:$A$" + ((String[])v).length);
            CellRangeAddressList addressList = new CellRangeAddressList(1, 2000, k.intValue(), k.intValue());
            DataValidationConstraint constraint8 = helper.createFormulaListConstraint(sheetName);
            DataValidation dataValidation3 = helper.createValidation(constraint8, addressList);
            dataValidation3.setErrorStyle(0);
            dataValidation3.setShowErrorBox(true);
            dataValidation3.setSuppressDropDownArrow(true);
            dataValidation3.createErrorBox("\u63d0\u793a", "\u6b64\u503c\u4e0e\u5355\u5143\u683c\u5b9a\u4e49\u683c\u5f0f\u4e0d\u4e00\u81f4");
            writeSheetHolder.getSheet().addValidationData(dataValidation3);
        });
    }
}

