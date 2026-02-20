package com.kuma.boot.office.excelstrategy.strategy;

import com.kuma.boot.office.excelstrategy.ExcelTypeEnum;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;

/**
 */
public class ExcelVersionStrategy implements Strategy {


    @Override
    public Workbook version(String fileType, InputStream is, Workbook wb) throws Exception {
        if (ExcelTypeEnum.EXCEL_THREE.getText().equalsIgnoreCase(fileType)) {
            wb = new HSSFWorkbook(is);
        } else if (ExcelTypeEnum.EXCEL_SEVEN.getText().equalsIgnoreCase(fileType)) {
            wb = new XSSFWorkbook(is);
        } else {
            throw new Exception("您输入的excel格式不正确");
        }
        return wb;
    }
}
