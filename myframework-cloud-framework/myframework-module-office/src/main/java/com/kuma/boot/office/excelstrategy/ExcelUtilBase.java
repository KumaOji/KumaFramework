/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.servlet.ServletOutputStream
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.poi.ooxml.POIXMLDocumentPart
 *  org.apache.poi.openxml4j.opc.PackagePartName
 *  org.apache.poi.openxml4j.opc.PackageRelationship
 *  org.apache.poi.openxml4j.opc.TargetMode
 *  org.apache.poi.ss.formula.functions.T
 *  org.apache.poi.ss.usermodel.Cell
 *  org.apache.poi.ss.usermodel.CellStyle
 *  org.apache.poi.ss.usermodel.CellType
 *  org.apache.poi.ss.usermodel.DateUtil
 *  org.apache.poi.ss.usermodel.HorizontalAlignment
 *  org.apache.poi.ss.usermodel.Row
 *  org.apache.poi.ss.usermodel.Sheet
 *  org.apache.poi.ss.usermodel.Workbook
 *  org.apache.poi.ss.usermodel.WorkbookFactory
 *  org.apache.poi.ss.util.CellAddress
 *  org.apache.poi.ss.util.CellRangeAddress
 *  org.apache.poi.xssf.usermodel.XSSFCellStyle
 *  org.apache.poi.xssf.usermodel.XSSFRelation
 *  org.apache.poi.xssf.usermodel.XSSFRow
 *  org.apache.poi.xssf.usermodel.XSSFSheet
 *  org.apache.poi.xssf.usermodel.XSSFWorkbook
 *  org.springframework.beans.BeanUtils
 */
package com.kuma.boot.office.excelstrategy;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.office.excelstrategy.strategy.Context;
import com.kuma.boot.office.excelstrategy.strategy.ExcelVersionStrategy;
import jakarta.servlet.ServletOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;

public class ExcelUtilBase {
    public static Map<String, String> getMap(String keyValue) {
        HashMap<String, String> map = new HashMap<String, String>(20);
        if (keyValue != null) {
            String[] str;
            for (String element : str = keyValue.split(",")) {
                String[] str2 = element.split(":");
                map.put(str2[0], str2[1]);
            }
        }
        return map;
    }

    public static Map<String, String> getMap(Class<T> clazz) throws NoSuchFieldException {
        int i;
        HashMap<String, String> map = new HashMap<String, String>(20);
        HashMap<String, String> noExcelMap = new HashMap<String, String>(20);
        Field[] fields = clazz.getDeclaredFields();
        for (i = 0; i < fields.length; ++i) {
            fields[i].setAccessible(true);
        }
        for (i = 0; i < fields.length; ++i) {
            Field field = clazz.getDeclaredField(fields[i].getName());
            Excel column = field.getAnnotation(Excel.class);
            if (column != null) {
                map.put(column.title(), field.getName());
            }
            noExcelMap.put(fields[i].getName(), fields[i].getName());
        }
        if (!map.isEmpty()) {
            return map;
        }
        return noExcelMap;
    }

    public static Map<String, Object> getMap(Object obj) {
        HashMap<String, Object> map = new HashMap<String, Object>(20);
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int j = 0; j < fields.length; ++j) {
            fields[j].setAccessible(true);
            try {
                map.put(fields[j].getName(), fields[j].get(obj));
                continue;
            }
            catch (Exception e) {
                System.out.println("\u5b57\u6bb5[" + fields[j].getName() + "]\u89e3\u6790\u5f02\u5e38");
            }
        }
        return map;
    }

    public static List<String> getList(String keyValue) {
        ArrayList<String> list = new ArrayList<String>();
        if (keyValue != null) {
            String[] str;
            for (String element : str = keyValue.split(",")) {
                String[] str2 = element.split(":");
                list.add(str2[0]);
            }
        }
        return list;
    }

    public static List<String> getList(Class<T> clazz) throws NoSuchFieldException {
        int i;
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> noExcellist = new ArrayList<String>();
        Field[] fields = clazz.getDeclaredFields();
        for (i = 0; i < fields.length; ++i) {
            fields[i].setAccessible(true);
        }
        for (i = 0; i < fields.length; ++i) {
            Field field = clazz.getDeclaredField(fields[i].getName());
            Excel column = field.getAnnotation(Excel.class);
            if (column != null) {
                list.add(column.title());
            }
            noExcellist.add(fields[i].getName());
        }
        if (list.size() > 0) {
            return list;
        }
        return noExcellist;
    }

    public static List getResult(ExcelParam excelParam) throws Exception {
        Set keySet = null;
        if (excelParam.getMap() == null || excelParam.getMap().size() == 0) {
            excelParam.setMap(ExcelUtilBase.getMap(excelParam.getClazz()));
            keySet = excelParam.getMap().keySet();
        } else {
            keySet = excelParam.getMap().keySet();
        }
        ArrayList list = new ArrayList();
        String fileType = "";
        InputStream is = null;
        Workbook wb = null;
        if (excelParam.getStream().booleanValue()) {
            is = new ByteArrayInputStream(excelParam.getBuf());
            wb = WorkbookFactory.create((InputStream)is);
        } else {
            fileType = excelParam.getFilePath().substring(excelParam.getFilePath().lastIndexOf(".") + 1, excelParam.getFilePath().length());
            is = new FileInputStream(excelParam.getFilePath());
            Context context = new Context(new ExcelVersionStrategy());
            wb = context.executeStrategy(fileType, is, wb);
        }
        int startSheetNum = 0;
        int endSheetNum = 1;
        if (null != excelParam.getSheetIndex()) {
            startSheetNum = excelParam.getSheetIndex() - 1;
            endSheetNum = excelParam.getSheetIndex();
        }
        for (int sheetNum = startSheetNum; sheetNum < endSheetNum; ++sheetNum) {
            int rowNum_x = -1;
            HashMap<String, Integer> cellmap = new HashMap<String, Integer>(20);
            ArrayList<String> headlist = new ArrayList<String>();
            Sheet hssfSheet = wb.getSheetAt(sheetNum);
            if (hssfSheet.getNumMergedRegions() > 0) {
                excelParam.setRowNumIndex(2);
            }
            if (hssfSheet != null && hssfSheet.getLastRowNum() > 500000) {
                throw new Exception("Excel \u6570\u636e\u8d85\u8fc750w\u884c,\u8bf7\u68c0\u67e5\u662f\u5426\u6709\u7a7a\u884c,\u6216\u5206\u6279\u5bfc\u5165");
            }
            for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); ++rowNum) {
                int i;
                Row hssfRow;
                if (excelParam.getRowNumIndex() != null && rowNum_x == -1 && (hssfRow = hssfSheet.getRow(rowNum = excelParam.getRowNumIndex() - 1)) == null) {
                    throw new RuntimeException("\u6307\u5b9a\u7684\u884c\u4e3a\u7a7a\uff0c\u8bf7\u68c0\u67e5");
                }
                hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow == null) continue;
                boolean flag = false;
                for (i = 0; i < hssfRow.getLastCellNum(); ++i) {
                    if (hssfRow.getCell(i) == null || "".equals(hssfRow.getCell(i).toString().trim())) continue;
                    flag = true;
                }
                if (!flag) continue;
                if (rowNum_x == -1) {
                    for (int cellNum = 0; cellNum <= hssfRow.getLastCellNum(); ++cellNum) {
                        Cell hssfCell = hssfRow.getCell(cellNum);
                        if (hssfCell == null) continue;
                        String tempCellValue = hssfSheet.getRow(rowNum).getCell(cellNum).getStringCellValue();
                        tempCellValue = org.apache.commons.lang3.StringUtils.remove((String)tempCellValue, (char)'\u00a0');
                        tempCellValue = tempCellValue.trim();
                        headlist.add(tempCellValue);
                        for (Object key : keySet) {
                            if (!StringUtils.isNotBlank((String)tempCellValue) || !StringUtils.equals((CharSequence)tempCellValue, (CharSequence)key.toString())) continue;
                            rowNum_x = rowNum;
                            cellmap.put(excelParam.getMap().get(key).toString(), cellNum);
                        }
                    }
                    if (rowNum_x == -1) {
                        throw new Exception("\u6ca1\u6709\u627e\u5230\u5bf9\u5e94\u7684\u5b57\u6bb5\u6216\u8005\u5bf9\u5e94\u5b57\u6bb5\u884c\u4e0a\u9762\u542b\u6709\u4e0d\u4e3a\u7a7a\u767d\u7684\u884c\u5b57\u6bb5");
                    }
                    if (!excelParam.getSameHeader().booleanValue()) continue;
                    for (i = 0; i < headlist.size(); ++i) {
                        boolean boo = false;
                        Iterator itor = keySet.iterator();
                        while (itor.hasNext()) {
                            String tempname = itor.next().toString();
                            if (!tempname.equals(headlist.get(i))) continue;
                            boo = true;
                        }
                        if (boo) continue;
                        throw new Exception("\u8868\u5934\u5b57\u6bb5\u548c\u5b9a\u4e49\u7684\u5c5e\u6027\u5b57\u6bb5\u4e0d\u5339\u914d\uff0c\u8bf7\u68c0\u67e5");
                    }
                    Iterator itor = keySet.iterator();
                    while (itor.hasNext()) {
                        boolean boo = false;
                        String tempname = itor.next().toString();
                        for (int i2 = 0; i2 < headlist.size(); ++i2) {
                            if (!tempname.equals(headlist.get(i2))) continue;
                            boo = true;
                        }
                        if (boo) continue;
                        throw new Exception("\u8868\u5934\u5b57\u6bb5\u548c\u5b9a\u4e49\u7684\u5c5e\u6027\u5b57\u6bb5\u4e0d\u5339\u914d\uff0c\u8bf7\u68c0\u67e5");
                    }
                    continue;
                }
                Object obj = excelParam.getClazz().newInstance();
                for (Object key : keySet) {
                    Integer cellNum_x = (Integer)cellmap.get(excelParam.getMap().get(key).toString());
                    if (cellNum_x == null || hssfRow.getCell(cellNum_x.intValue()) == null) continue;
                    String attr = excelParam.getMap().get(key).toString();
                    Class attrType = BeanUtils.findPropertyType((String)attr, (Class[])new Class[]{obj.getClass()});
                    Cell cell = hssfRow.getCell(cellNum_x.intValue());
                    ExcelUtilBase.getValue(cell, obj, attr, attrType, rowNum, cellNum_x, key);
                }
                list.add(obj);
            }
        }
        is.close();
        return list;
    }

    public static void addWaterMark(XSSFWorkbook wb, XSSFSheet sheet, String waterMark) {
        if (StringUtils.isNotEmpty((CharSequence)waterMark)) {
            FontImage.Watermark watermark = new FontImage.Watermark();
            watermark.setText(waterMark);
            watermark.setEnable(true);
            BufferedImage image = FontImage.createWatermarkImage(watermark);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write((RenderedImage)image, "png", os);
            }
            catch (IOException e) {
                LogUtils.error((Throwable)e);
                System.out.println("\u6dfb\u52a0\u6c34\u5370\u5931\u8d25");
            }
            int pictureIdx = wb.addPicture(os.toByteArray(), 6);
            POIXMLDocumentPart poixmlDocumentPart = (POIXMLDocumentPart)wb.getAllPictures().get(pictureIdx);
            PackagePartName ppn = poixmlDocumentPart.getPackagePart().getPartName();
            String relType = XSSFRelation.IMAGES.getRelation();
            PackageRelationship pr = sheet.getPackagePart().addRelationship(ppn, TargetMode.INTERNAL, relType, null);
            sheet.getCTWorksheet().addNewPicture().setId(pr.getId());
        }
    }

    public static void commonExportExcel(ExcelParam excelParam) throws Exception {
        int i;
        XSSFRow rowHeader;
        Map<String, String> map = ExcelUtilBase.getMap(excelParam.getClazz());
        List<String> keyList = null;
        keyList = StringUtils.isEmpty((String)excelParam.getKeyValue()) ? ExcelUtilBase.getList(excelParam.getClazz()) : ExcelUtilBase.getList(excelParam.getKeyValue());
        Object obj = excelParam.getClazz().newInstance();
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("sheet1");
        if (StringUtils.isNotEmpty((CharSequence)excelParam.getWaterMark())) {
            ExcelUtilBase.addWaterMark(wb, sheet, excelParam.getWaterMark());
        }
        XSSFCellStyle headerStyle = wb.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setWrapText(true);
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        HashMap<Integer, Integer> maxWidth = new HashMap<Integer, Integer>(20);
        HashMap<String, String> attMap = new HashMap<String, String>(20);
        int startRow = 0;
        if (StringUtils.isNotEmpty((CharSequence)excelParam.getHeaderName())) {
            rowHeader = sheet.createRow(0);
            rowHeader.setHeight((short)625);
            rowHeader.setRowStyle((CellStyle)headerStyle);
            Cell rowCell = rowHeader.createCell(0);
            rowCell.setCellStyle((CellStyle)headerStyle);
            rowCell.setCellValue(excelParam.getHeaderName());
            CellRangeAddress cra = new CellRangeAddress(0, 0, 0, keyList.size() - 1);
            sheet.addMergedRegion(cra);
            startRow = 1;
        }
        rowHeader = sheet.createRow(startRow);
        rowHeader.setHeight((short)500);
        rowHeader.setRowStyle((CellStyle)headerStyle);
        int index = 0;
        for (String key : keyList) {
            Cell rowCell = rowHeader.createCell(index);
            rowCell.setCellStyle((CellStyle)headerStyle);
            rowCell.setCellValue(key);
            attMap.put(Integer.toString(index), map.get(key));
            maxWidth.put(index, rowCell.getStringCellValue().getBytes().length * 256 + 200);
            ++index;
        }
        for (i = 0; i < keyList.size(); ++i) {
            sheet.setColumnWidth(i, ((Integer)maxWidth.get(i)).intValue());
        }
        if (null != excelParam.getList() && excelParam.getList().size() > 0) {
            for (i = 0; i < excelParam.getList().size(); ++i) {
                XSSFRow row = sheet.createRow(i + startRow + 1);
                row.setHeight((short)450);
                for (int j = 0; j < map.size(); ++j) {
                    Class attrType = BeanUtils.findPropertyType((String)((String)attMap.get(Integer.toString(j))), (Class[])new Class[]{obj.getClass()});
                    Object value = ExcelUtilBase.getAttrVal(excelParam.getList().get(i), (String)attMap.get(Integer.toString(j)), attrType);
                    if (null == value) {
                        value = "";
                    }
                    Cell rowCell = row.createCell(j);
                    rowCell.setCellStyle((CellStyle)cellStyle);
                    rowCell.setCellValue(value.toString());
                }
            }
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String newFileName = excelParam.getFileName();
        if (StringUtils.isEmpty((String)newFileName)) {
            newFileName = df.format(new Date());
        }
        try {
            if (excelParam.getResponse() != null) {
                ServletOutputStream outstream = excelParam.getResponse().getOutputStream();
                excelParam.getResponse().setHeader("Content-disposition", "attachment; filename=" + new String(newFileName.getBytes(), "iso-8859-1") + ".xlsx");
                excelParam.getResponse().setContentType("application/x-download");
                wb.write((OutputStream)outstream);
                outstream.flush();
                outstream.close();
            } else {
                FileOutputStream out = new FileOutputStream(excelParam.getOutFilePath());
                wb.write((OutputStream)out);
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException e) {
            throw new FileNotFoundException("\u5bfc\u51fa\u5931\u8d25\uff01" + String.valueOf(e));
        }
        catch (IOException e) {
            throw new IOException("\u5bfc\u51fa\u5931\u8d25\uff01" + String.valueOf(e));
        }
    }

    public static void commonExportExcel2(ExcelParamAbstract excelParamAbstract) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook();
        int count = 1;
        for (ExcelParam excelParam : excelParamAbstract.getList()) {
            int i;
            XSSFRow rowHeader;
            Map<String, String> map = ExcelUtilBase.getMap(excelParam.getClazz());
            List<String> keyList = null;
            keyList = StringUtils.isEmpty((String)excelParam.getKeyValue()) ? ExcelUtilBase.getList(excelParam.getClazz()) : ExcelUtilBase.getList(excelParam.getKeyValue());
            Object obj = excelParam.getClazz().newInstance();
            XSSFSheet sheet = wb.createSheet((String)(excelParam.getSheetName() != null ? excelParam.getSheetName() : "sheet" + count));
            if (StringUtils.isNotEmpty((CharSequence)excelParam.getWaterMark())) {
                ExcelUtilBase.addWaterMark(wb, sheet, excelParam.getWaterMark());
            }
            ++count;
            XSSFCellStyle headerStyle = wb.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setWrapText(true);
            XSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            HashMap<Integer, Integer> maxWidth = new HashMap<Integer, Integer>(20);
            HashMap<String, String> attMap = new HashMap<String, String>(20);
            int startRow = 0;
            if (StringUtils.isNotEmpty((CharSequence)excelParam.getHeaderName())) {
                rowHeader = sheet.createRow(0);
                rowHeader.setHeight((short)625);
                rowHeader.setRowStyle((CellStyle)headerStyle);
                Cell rowCell = rowHeader.createCell(0);
                rowCell.setCellStyle((CellStyle)headerStyle);
                rowCell.setCellValue(excelParam.getHeaderName());
                CellRangeAddress cra = new CellRangeAddress(0, 0, 0, keyList.size() - 1);
                sheet.addMergedRegion(cra);
                startRow = 1;
            }
            rowHeader = sheet.createRow(startRow);
            rowHeader.setHeight((short)500);
            rowHeader.setRowStyle((CellStyle)headerStyle);
            int index = 0;
            for (String key : keyList) {
                Cell rowCell = rowHeader.createCell(index);
                rowCell.setCellStyle((CellStyle)headerStyle);
                rowCell.setCellValue(key);
                attMap.put(Integer.toString(index), map.get(key));
                maxWidth.put(index, rowCell.getStringCellValue().getBytes().length * 256 + 200);
                ++index;
            }
            for (i = 0; i < keyList.size(); ++i) {
                sheet.setColumnWidth(i, ((Integer)maxWidth.get(i)).intValue());
            }
            for (i = 0; i < excelParam.getList().size(); ++i) {
                XSSFRow row = sheet.createRow(i + startRow + 1);
                row.setHeight((short)450);
                for (int j = 0; j < map.size(); ++j) {
                    Class attrType = BeanUtils.findPropertyType((String)((String)attMap.get(Integer.toString(j))), (Class[])new Class[]{obj.getClass()});
                    Object value = ExcelUtilBase.getAttrVal(excelParam.getList().get(i), (String)attMap.get(Integer.toString(j)), attrType);
                    if (null == value) {
                        value = "";
                    }
                    Cell rowCell = row.createCell(j);
                    rowCell.setCellStyle((CellStyle)cellStyle);
                    rowCell.setCellValue(value.toString());
                }
            }
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String newFileName = excelParamAbstract.getFileName();
        if (StringUtils.isEmpty((String)newFileName)) {
            newFileName = df.format(new Date());
        }
        try {
            if (excelParamAbstract.getResponse() != null) {
                ServletOutputStream outstream = excelParamAbstract.getResponse().getOutputStream();
                excelParamAbstract.getResponse().setHeader("Content-disposition", "attachment; filename=" + new String(newFileName.getBytes(), "iso-8859-1") + ".xlsx");
                excelParamAbstract.getResponse().setContentType("application/x-download");
                wb.write((OutputStream)outstream);
                outstream.flush();
                outstream.close();
            } else {
                FileOutputStream out = new FileOutputStream(excelParamAbstract.getOutFilePath());
                wb.write((OutputStream)out);
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException e) {
            throw new FileNotFoundException("\u5bfc\u51fa\u5931\u8d25\uff01" + String.valueOf(e));
        }
        catch (IOException e) {
            throw new IOException("\u5bfc\u51fa\u5931\u8d25\uff01" + String.valueOf(e));
        }
    }

    public static void setter(Object obj, String att, Object value, Class<?> type, int row, int col, Object key) throws Exception {
        try {
            Method method = obj.getClass().getMethod("set" + StringUtil.toUpperCaseFirstOne(att), type);
            if (value != null) {
                method.invoke(obj, value);
            }
        }
        catch (Exception e) {
            throw new Exception("\u7b2c" + (row + 1) + " \u884c  " + (col + 1) + "\u5217   \u5c5e\u6027\uff1a" + String.valueOf(key) + " \u8d4b\u503c\u5f02\u5e38  " + String.valueOf(e));
        }
    }

    public static Object getAttrVal(Object obj, String att, Class<?> attType) throws Exception {
        try {
            Method method = obj.getClass().getMethod("get" + StringUtil.toUpperCaseFirstOne(att), new Class[0]);
            Object value = method.invoke(obj, new Object[0]);
            if (attType == Date.class) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                value = sdf.format(value);
            }
            return value;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static void getValue(Cell cell, Object obj, String attr, Class attrType, int row, int col, Object key) throws Exception {
        Object val = null;
        if (cell.getCellType() == CellType.BOOLEAN) {
            val = cell.getBooleanCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted((Cell)cell)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    if (attrType == String.class) {
                        val = sdf.format(DateUtil.getJavaDate((double)cell.getNumericCellValue()));
                    }
                    val = ExcelUtilBase.dateConvertFormat(sdf.format(DateUtil.getJavaDate((double)cell.getNumericCellValue())));
                }
                catch (ParseException e) {
                    throw new Exception("\u7b2c" + (row + 1) + " \u884c  " + (col + 1) + "\u5217   \u5c5e\u6027\uff1a" + String.valueOf(key) + " \u65e5\u671f\u683c\u5f0f\u8f6c\u6362\u9519\u8bef  ");
                }
            } else if (attrType.equals(String.class)) {
                cell.setCellType(CellType.STRING);
                val = cell.getStringCellValue();
            } else {
                val = attrType.equals(BigDecimal.class) ? new BigDecimal(cell.getNumericCellValue()) : (attrType.equals(Long.class) || attrType.equals(Long.TYPE) ? (Number)((long)cell.getNumericCellValue()) : (Number)(attrType.equals(Double.class) || attrType.equals(Double.TYPE) ? (Number)cell.getNumericCellValue() : (Number)(attrType.equals(Float.class) || attrType.equals(Float.TYPE) ? (Number)Float.valueOf((float)cell.getNumericCellValue()) : (Number)(attrType.equals(Integer.TYPE) || attrType.equals(Integer.class) ? (Number)((int)cell.getNumericCellValue()) : (Number)(attrType.equals(Short.class) || attrType.equals(Short.TYPE) ? (Number)((short)cell.getNumericCellValue()) : (Number)cell.getNumericCellValue())))));
            }
        } else if (cell.getCellType() == CellType.STRING) {
            String cellVal = null;
            if (cell.getStringCellValue() != null && cell.getStringCellValue().trim().length() > 0) {
                cellVal = cell.getStringCellValue().trim();
                val = attrType.equals(Double.TYPE) || attrType.equals(Double.class) ? Double.valueOf(Double.parseDouble(cellVal)) : (attrType.equals(BigDecimal.class) ? new BigDecimal(cellVal) : (attrType.equals(Long.TYPE) || attrType.equals(Long.class) ? Long.valueOf(cellVal) : (attrType.equals(Float.class) || attrType.equals(Float.TYPE) ? Float.valueOf(cellVal) : (attrType.equals(Integer.TYPE) || attrType.equals(Integer.class) ? Integer.valueOf(Integer.parseInt(cellVal)) : (attrType.equals(Short.class) || attrType.equals(Short.TYPE) ? Short.valueOf(cellVal) : (attrType.equals(Date.class) ? ExcelUtilBase.dateConvertFormat(cellVal) : cell.getStringCellValue()))))));
            } else {
                val = null;
            }
        }
        ExcelUtilBase.setter(obj, attr, val, attrType, row, col, key);
    }

    public static Date dateConvertFormat(String dateStr) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(dateStr);
        return date;
    }

    public static void templateWrite(ExcelParam excelParam) {
        HashMap<String, String> resultMap = new HashMap<String, String>(20);
        File file = new File(excelParam.getFilePath());
        try {
            FileInputStream excelFileInputStream = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create((InputStream)excelFileInputStream);
            excelFileInputStream.close();
            Sheet sheet = workbook.getSheetAt(0);
            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); ++rowNum) {
                Row hssfRow = sheet.getRow(rowNum);
                if (hssfRow == null) continue;
                for (int cellNum = 0; cellNum < hssfRow.getLastCellNum(); ++cellNum) {
                    Cell hssfCell = hssfRow.getCell(cellNum);
                    if (hssfCell == null || StringUtils.isEmpty((String)hssfCell.getStringCellValue())) continue;
                    String tempCellValue = sheet.getRow(rowNum).getCell(cellNum).getStringCellValue();
                    tempCellValue = org.apache.commons.lang3.StringUtils.remove((String)tempCellValue, (char)'\u00a0');
                    String pattern = "(?s)^#.*}$";
                    if (!Pattern.matches(pattern, tempCellValue = tempCellValue.trim())) continue;
                    String variableName = tempCellValue.substring(2, tempCellValue.length() - 1);
                    resultMap.put(variableName, hssfCell.getAddress().toString());
                }
            }
            Map<String, Object> filedValMap = ExcelUtilBase.getMap(excelParam.getObj());
            for (String key : resultMap.keySet()) {
                CellAddress address = new CellAddress((String)resultMap.get(key));
                Row row = sheet.getRow(address.getRow());
                Cell cell = row.getCell(address.getColumn());
                cell.setCellValue(filedValMap.get(key) == null ? null : filedValMap.get(key).toString());
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            String newFileName = excelParam.getFileName();
            if (StringUtils.isEmpty((String)newFileName)) {
                newFileName = df.format(new Date());
            }
            try {
                if (excelParam.getResponse() != null) {
                    ServletOutputStream outstream = excelParam.getResponse().getOutputStream();
                    excelParam.getResponse().setHeader("Content-disposition", "attachment; filename=" + new String(newFileName.getBytes(), "iso-8859-1") + ".xlsx");
                    excelParam.getResponse().setContentType("application/x-download");
                    workbook.write((OutputStream)outstream);
                    outstream.flush();
                    outstream.close();
                } else {
                    FileOutputStream out = new FileOutputStream(excelParam.getOutFilePath());
                    workbook.write((OutputStream)out);
                    out.flush();
                    out.close();
                }
            }
            catch (FileNotFoundException e) {
                throw new FileNotFoundException("\u5bfc\u51fa\u5931\u8d25\uff01" + String.valueOf(e));
            }
            catch (IOException e) {
                throw new IOException("\u5bfc\u51fa\u5931\u8d25\uff01" + String.valueOf(e));
            }
        }
        catch (Exception e) {
            LogUtils.error((Throwable)e);
        }
    }

    protected static class PoiWriter
    implements Runnable {
        private final CountDownLatch doneSignal;
        private Sheet sheet;
        private int start;
        private int end;
        private List list;
        private Map<String, String> map;
        private Map<String, String> attMap;
        private Object obj;

        private static synchronized Row getRow(Sheet sheet, int rownum) {
            return sheet.createRow(rownum);
        }

        public PoiWriter(CountDownLatch doneSignal, Sheet sheet, int start, int end, List list, Map<String, String> map, Map<String, String> attMap, Object object) {
            this.doneSignal = doneSignal;
            this.sheet = sheet;
            this.start = start;
            this.end = end;
            this.list = list;
            this.map = map;
            this.attMap = attMap;
            this.obj = object;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            int k = this.start;
            try {
                for (int i = 0; i < this.list.size(); ++i) {
                    Row row = PoiWriter.getRow(this.sheet, k);
                    for (int j = 0; j < this.map.size(); ++j) {
                        Class attrType = BeanUtils.findPropertyType((String)this.attMap.get(Integer.toString(j)), (Class[])new Class[]{this.obj.getClass()});
                        Object value = ExcelUtilBase.getAttrVal(this.list.get(i), this.attMap.get(Integer.toString(j)), attrType);
                        if (null == value) {
                            value = "";
                        }
                        row.createCell(j).setCellValue(value.toString());
                    }
                    ++k;
                }
            }
            catch (Exception e) {
                LogUtils.error((Throwable)e);
            }
            finally {
                this.doneSignal.countDown();
                System.out.println("start: " + this.start + " end: " + this.end + " Count: " + this.doneSignal.getCount());
            }
        }
    }
}

