package com.kuma.boot.office.excelstrategy.strategy;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;

/**
 */
public class Context {

    Strategy strategy;

    public Context(Strategy strategy) {
        this.strategy = strategy;
    }

    public Workbook executeStrategy(String fileType, InputStream is, Workbook wb) throws Exception {
        return strategy.version(fileType,is,wb);
    }
}
