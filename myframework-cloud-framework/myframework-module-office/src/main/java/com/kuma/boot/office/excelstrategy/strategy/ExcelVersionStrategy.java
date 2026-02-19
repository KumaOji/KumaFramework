/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.poi.hssf.usermodel.HSSFWorkbook
 *  org.apache.poi.ss.usermodel.Workbook
 *  org.apache.poi.xssf.usermodel.XSSFWorkbook
 */
package com.kuma.boot.office.excelstrategy.strategy;

import com.kuma.boot.office.excelstrategy.ExcelTypeEnum;

import java.io.InputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelVersionStrategy
implements Strategy {
    @Override
    public Workbook version(String fileType, InputStream is, Workbook wb) throws Exception {
        if (ExcelTypeEnum.EXCEL_THREE.getText().equalsIgnoreCase(fileType)) {
            wb = new HSSFWorkbook(is);
        } else if (ExcelTypeEnum.EXCEL_SEVEN.getText().equalsIgnoreCase(fileType)) {
            wb = new XSSFWorkbook(is);
        } else {
            throw new Exception("\u60a8\u8f93\u5165\u7684excel\u683c\u5f0f\u4e0d\u6b63\u786e");
        }
        return wb;
    }
}

