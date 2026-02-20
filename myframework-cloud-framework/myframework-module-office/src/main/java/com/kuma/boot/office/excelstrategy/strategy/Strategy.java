package com.kuma.boot.office.excelstrategy.strategy;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;

/**
 *
 */
public interface Strategy {
    Workbook version(String fileType, InputStream is, Workbook wb) throws Exception;
}
